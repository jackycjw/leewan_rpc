package com.leewan.rpc.client;

import com.leewan.rpc.client.except.InvokeException;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ProxyServiceInvocation implements InvocationHandler {

    private ChannelPool channelPool;

    private ClientContext context;

    private List<Interceptor> interceptors;

    public ProxyServiceInvocation(ChannelPool channelPool, ClientContext context, List<Interceptor> interceptors) {
        this.channelPool = channelPool;
        this.context = context;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Channel channel = channelPool.acquire().get();
        try {
            //调用元数据
            InvokeMeta meta = context.getInvokeMeta(method);
            RequestMessage request = getRequestMessage(meta, args);

            //获取请求唯一键
            int sequence = context.getSequence();
            request.setSequence(sequence);
            //获得future
            Future<ResponseMessage> future = context.getFuture(sequence);

            //拦截器 preHandle
            interceptors.stream().forEach(interceptor -> interceptor.preHandle(request));
            //正式发送请求
            channel.writeAndFlush(request);

            ResponseMessage response = future.get();

            //拦截器 postHandle
            interceptors.stream().forEach(interceptor -> interceptor.postHandle(request, response));

            if (response.getExceptionMessage() != null) {
                throw new InvokeException(response.getExceptionMessage());
            }
            return response.getResponse();
        } finally {
            if (channel != null) {
                channelPool.release(channel);
            }
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