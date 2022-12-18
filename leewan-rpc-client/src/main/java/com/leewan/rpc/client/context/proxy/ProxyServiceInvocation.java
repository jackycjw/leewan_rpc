package com.leewan.rpc.client.context.proxy;

import com.leewan.rpc.client.context.call.CallPerformance;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.call.ExecuteCall;
import com.leewan.rpc.client.except.InvokeException;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.ObjectUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ProxyServiceInvocation implements InvocationHandler {

    private final GenericObjectPool<Channel> channelPool;

    private final ClientContext context;

    private final List<Interceptor> interceptors;

    private final Class<?> proxyedInterface;

    public ProxyServiceInvocation(Class<?> proxyedInterface, GenericObjectPool<Channel> channelPool,
                                  ClientContext context, List<Interceptor> interceptors) {
        this.proxyedInterface = proxyedInterface;
        this.channelPool = channelPool;
        this.context = context;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Exception exception = null;
        if (method.getDeclaringClass().equals(proxyedInterface)) {

            //默认方法则执行默认方法
            if (method.isDefault()) {
                MethodHandle handle = getMethodHandleJava9(method);
                return handle.bindTo(proxy).invokeWithArguments(args);
            }
            CallPerformance performance = context.getCallPerformance(method);
            int retry = performance.getRetry();
            do {
                retry--;
                Channel channel = null;
                try {
                    channel = channelPool.borrowObject();
                    return doInvoke(method, args, performance, channel);
                } catch (RuntimeException e) {
                    // 业务异常直接抛出
                    throw e;
                }catch (Exception e) {
                    log.error(e.getMessage(), e);
                    channel.close();
                    exception = e;
                } finally {
                    if (channel != null) {
                        channelPool.returnObject(channel);
                    }
                }
                if (retry >= 0 ) {
                    log.info("开始第{}次重试:{}.{}",(performance.getRetry()-retry),
                            method.getDeclaringClass().getName(), method.getName());
                }
            } while (retry >= 0);

        } else {
            return method.invoke(this, args);
        }
        throw exception;
    }

    private MethodHandle getMethodHandleJava9(Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup)privateLookupInMethod.invoke((Object)null, declaringClass, MethodHandles.lookup())).findSpecial(declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()), declaringClass);
    }

    static Method privateLookupInMethod;
    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException var5) {
            privateLookupIn = null;
        }
        privateLookupInMethod = privateLookupIn;
    }



    /**
     * 执行代理方法
     * @param method
     * @param args
     * @param performance
     * @return
     * @throws Exception
     */
    public Object doInvoke(Method method, Object[] args, CallPerformance performance, Channel channel) throws Exception{

        //调用元数据
        InvokeMeta meta = context.getInvokeMeta(method);
        RequestMessage request = getRequestMessage(meta, args);

        //获取请求唯一键
        int sequence = context.getSequence();
        request.setSequence(sequence);

        //拦截器 前置 preHandle
        interceptors.stream().forEach(interceptor -> interceptor.preHandle(request));
        //异步
        if (ExecuteCall.isCurrentAsyn()) {
            context.saveCallback(request, ExecuteCall.getCurrentConsumer(), performance);
            //正式发送请求
            channel.writeAndFlush(request);
            return ObjectUtils.getDefaultBasicValue(method.getReturnType());
        }
        // 同步
        else {
            Future<ResponseMessage> future = context.createFuture(request);
            channel.writeAndFlush(request);
            ResponseMessage response = future.get(performance.getRequestTimeout(), TimeUnit.MILLISECONDS);
            //拦截器 后置 postHandle
            interceptors.stream().forEach(interceptor -> interceptor.postHandle(request, response));
            if (response.getExceptionType() != null) {
                throw new InvokeException(response.getExceptionMessage(), response.getExceptionType());
            }
            return response.getResponse();
        }
    }



    private RequestMessage getRequestMessage(InvokeMeta meta, Object[] args) {
        RequestMessage request = new RequestMessage();
        request.setInvokeMeta(meta);
        if (args != null && args.length > 0) {
            request.setParameters(Arrays.stream(args).collect(Collectors.toList()));
        }
        return request;
    }
}
