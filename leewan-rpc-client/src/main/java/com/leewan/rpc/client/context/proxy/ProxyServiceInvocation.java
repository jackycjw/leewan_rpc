package com.leewan.rpc.client.context.proxy;

import com.leewan.rpc.client.call.CallPerformance;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.except.InvokeException;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
        if (method.getDeclaringClass().equals(proxyedInterface) && !method.isDefault()) {
            CallPerformance performance = context.getCallPerformance(method);
            int retry = performance.getRetry();
            do {
                retry--;
                Channel channel = channelPool.borrowObject();
                try {
                    //调用元数据
                    InvokeMeta meta = context.getInvokeMeta(method);
                    RequestMessage request = getRequestMessage(meta, args);

                    //获取请求唯一键
                    int sequence = context.getSequence();
                    request.setSequence(sequence);
                    //获得future
                    Future<ResponseMessage> future = context.createFuture(sequence);

                    //拦截器 preHandle
                    interceptors.stream().forEach(interceptor -> interceptor.preHandle(request));
                    //正式发送请求
                    channel.writeAndFlush(request);
                    ResponseMessage response = future.get(performance.getRequestTimeout(), TimeUnit.MILLISECONDS);
                    //拦截器 postHandle
                    interceptors.stream().forEach(interceptor -> interceptor.postHandle(request, response));

                    if (response.getExceptionType() != null) {
                        throw new InvokeException(response.getExceptionMessage(), response.getExceptionType());
                    }
                    return response.getResponse();
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
            return method.invoke(proxy, args);
        }
        throw exception;
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