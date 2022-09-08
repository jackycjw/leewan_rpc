package com.leewan.rpc.client.context;

import com.leewan.rpc.client.context.call.CallBack;
import com.leewan.rpc.client.context.call.CallPerformance;
import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.netty.PooledChannelFactory;
import com.leewan.rpc.client.context.proxy.ProxyServiceInvocation;
import com.leewan.rpc.client.except.ContextException;
import com.leewan.rpc.client.except.InvokeException;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.call.Call;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.Assert;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultClientContext implements ClientContext {

    private final ClientConfiguration configuration;

    public DefaultClientContext(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    //    private ChannelPool channelPool;
    private GenericObjectPool<Channel> channelPool;

    private boolean initialized = false;
    private NioEventLoopGroup executors;

    @Override
    public void initialize() {
        if (initialized) {
            throw new ContextException("context has initialized.");
        }
        synchronized (this) {
            if (initialized) {
                throw new ContextException("context has initialized.");
            }
            initialized = true;
            executors = new NioEventLoopGroup(configuration.getThreadNum());
            PooledChannelFactory factory = new PooledChannelFactory(configuration, this, executors);
            channelPool = new GenericObjectPool<>(factory, configuration.getPoolConfig());
            factory.initHeartBeatService();
        }
    }

    /**
     * check weather context initialized
     */
    private void checkInitialized() {
        if (!initialized) {
            throw new ContextException("context has not initialized.");
        }
    }

    public <T> T getService(Class<T> inter) {
        this.checkInitialized();
        Object service = Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{inter},
                new ProxyServiceInvocation(inter, channelPool, this, interceptors));
        return (T) service;
    }

    private final List<Interceptor> interceptors = new ArrayList<>(1);

    @Override
    public void registerInterceptor(Interceptor interceptor) {
        this.checkInitialized();
        Assert.notNull(interceptor, "拦截器不能为空");
        this.interceptors.add(interceptor);
    }


    private final Map<Method, InvokeMeta> invokeMetaMap = new ConcurrentHashMap<>();


    public InvokeMeta getInvokeMeta(Method method) {
        this.checkInitialized();
        if (invokeMetaMap.containsKey(method)) {
            return invokeMetaMap.get(method);
        }

        Class<?> declaringClass = method.getDeclaringClass();
        InvokeMeta meta = new InvokeMeta();
        meta.setClazzName(declaringClass.getName());
        meta.setMethodName(method.getName());
        List<String> parameterTypeNames = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        meta.setParameterTypeNames(parameterTypeNames);
        invokeMetaMap.put(method, meta);
        return meta;
    }

    private final Map<Method, CallPerformance> requestTimeoutMap = new HashMap<>();

    @Override
    public CallPerformance getCallPerformance(Method method) {
        this.checkInitialized();
        if (requestTimeoutMap.containsKey(method)) {
            return requestTimeoutMap.get(method);
        } else {
            int requestTimeout = configuration.getRequestTimeout();
            int retry = configuration.getRetry();
            CallPerformance performance = new CallPerformance(requestTimeout, retry);

            Call call = method.getAnnotation(Call.class);
            if (call != null) {
                if (call.retry() > -1) {
                    performance.setRetry(call.retry());
                }
                if (call.requestTimeout() > -1) {
                    performance.setRequestTimeout(call.requestTimeout());
                }
            }
            requestTimeoutMap.put(method, performance);
            return performance;
        }
    }


    private final AtomicInteger sequence = new AtomicInteger();

    private final Map<Integer, Object> promises = new ConcurrentHashMap<>();

    /**
     * 存储请求
     */
    private final Map<Integer, RequestMessage> requests = new ConcurrentHashMap<>();

    /**
     * 超时任务
     */
    private final Map<Integer, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    @Override
    public int getSequence() {
        this.checkInitialized();
        return this.sequence.incrementAndGet();
    }

    @Override
    public Future<ResponseMessage> createFuture(RequestMessage request) {
        this.checkInitialized();
        CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        promises.put(request.getSequence(), future);
        requests.put(request.getSequence(), request);
        return future;
    }

    @Override
    public void saveCallback(RequestMessage request, CallBack callback, CallPerformance performance) {
        this.checkInitialized();
        promises.put(request.getSequence(), callback);
        requests.put(request.getSequence(), request);
        ScheduledFuture<?> scheduledFuture = executors.schedule(() -> {
            // 任务超时
            synchronized (request) {
                if (requests.containsKey(request.getSequence())) {
                    promises.remove(request.getSequence());
                    requests.remove(request.getSequence());
                    scheduledFutures.remove(sequence);
                    callback.completeExceptionally(new InvokeException("调用超时"));
                }
            }
        }, performance.getRequestTimeout(), TimeUnit.MILLISECONDS);
        scheduledFutures.put(request.getSequence(), scheduledFuture);
    }

    @Override
    public void completeFuture(int sequence, ResponseMessage response) {
        this.checkInitialized();

        Object promise = promises.remove(sequence);
        RequestMessage request = requests.remove(sequence);
        ScheduledFuture<?> scheduledFuture = scheduledFutures.remove(sequence);
        if (promise != null) {
            // 同步调用
            if (promise instanceof CompletableFuture future) {
                future.complete(response);
            }
            // 异步调用
            else if (promise instanceof CallBack callBack) {
                // 取消超时监听任务
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
                synchronized (request) {
                    //拦截器 后置 postHandle
                    interceptors.stream().forEach(interceptor -> interceptor.postHandle(request, response));
                    if (response.getExceptionType() != null) {
                        // 异常回调
                        callBack.completeExceptionally(new InvokeException(response.getExceptionMessage(), response.getExceptionType()));
                    }
                    callBack.accept(response.getResponse());
                }

            } else {
                throw new InvokeException("promise should be CompletableFuture or Consumer. type "
                        + promise.getClass() + " is illegal.");
            }
        }
    }
}
