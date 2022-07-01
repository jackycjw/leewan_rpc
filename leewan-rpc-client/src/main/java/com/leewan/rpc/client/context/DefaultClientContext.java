package com.leewan.rpc.client.context;

import com.leewan.rpc.client.call.CallPerformance;
import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.netty.PooledChannelFactory;
import com.leewan.rpc.client.context.proxy.ProxyServiceInvocation;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.client.pool.ClientChannelPoolHandler;
import com.leewan.rpc.share.call.Call;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.Assert;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultClientContext implements ClientContext {

    private final ClientConfiguration configuration;

    public DefaultClientContext(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

//    private ChannelPool channelPool;
    private GenericObjectPool<Channel> channelPool;

    private boolean inited = false;


    public <T> T getService(Class<T> inter){
        this.init();
        Object service = Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{inter},
                new ProxyServiceInvocation(inter, channelPool, this, interceptors));
        return (T) service;
    }

    private final List<Interceptor> interceptors = new ArrayList<>(1);

    @Override
    public void registerInterceptor(Interceptor interceptor) {
        Assert.notNull(interceptor, "拦截器不能为空");
        this.interceptors.add(interceptor);
    }


    private final Map<Method, InvokeMeta> invokeMetaMap = new ConcurrentHashMap<>();



    public InvokeMeta getInvokeMeta(Method method){
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

    /**
     * 初始化网络连接
     */
    protected void init(){
        if (inited) {
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            PooledChannelFactory factory = new PooledChannelFactory(configuration, this);
            channelPool = new GenericObjectPool<>(factory, configuration.getPoolConfig());
            inited = true;
        }
    }


    private final AtomicInteger sequence = new AtomicInteger();

    private final Map<Integer, CompletableFuture<ResponseMessage>> promises = new ConcurrentHashMap<>();

    @Override
    public int getSequence() {
        return this.sequence.incrementAndGet();
    }

    @Override
    public Future<ResponseMessage> createFuture(int sequence) {
        CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        promises.put(sequence, future);
        return future;
    }

    @Override
    public void completeFuture(int sequence, ResponseMessage response) {
        CompletableFuture<ResponseMessage> future = promises.remove(sequence);
        if (future != null) {
            future.complete(response);
        }
    }
}
