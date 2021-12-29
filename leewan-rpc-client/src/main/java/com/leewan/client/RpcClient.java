package com.leewan.client;

import com.leewan.client.except.InvokeException;
import com.leewan.share.handler.LengthBasedOutboundHandler;
import com.leewan.share.handler.codec.KryoRequestMessageEncoder;
import com.leewan.share.handler.codec.KryoResponseMessageDecoder;
import com.leewan.share.message.InvokeMeta;
import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RpcClient {

    private ClientConfiguration configuration;

    private AtomicInteger sequence = new AtomicInteger();

    public void configure(ClientConfiguration configuration){
        this.configuration = configuration;
    }

    private Channel channel;
    private boolean inited = false;

    private Map<Integer, CompletableFuture<ResponseMessage>> promises = new ConcurrentHashMap<>();

    public <T> T getService(Class<T> inter){
        this.init();
        return (T)Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{inter},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        InvokeMeta meta = getInvokeMeta(method);
                        RequestMessage request = getRequestMessage(meta, args);
                        Future<ResponseMessage> future = getFuture(request);
                        channel.writeAndFlush(request);
                        ResponseMessage message = future.get();
                        if (message.getExceptionMessage() != null) {
                            throw new InvokeException(message.getExceptionMessage());
                        }
                        promises.remove(request.getSequence());
                        return message.getResponse();
                    }
                });
    }

    private Map<Method, InvokeMeta> invokeMetaMap = new ConcurrentHashMap<>();

    private RequestMessage getRequestMessage(InvokeMeta meta, Object[] args){
        RequestMessage request = new RequestMessage();
        request.setInvokeMeta(meta);
        if (args != null && args.length > 0) {
            request.setParameters(Arrays.stream(args).toList());
        }
        return request;
    }

    private CompletableFuture<ResponseMessage> getFuture(RequestMessage request){
        int seq = sequence.incrementAndGet();
        request.setSequence(seq);
        CompletableFuture<ResponseMessage> future = new CompletableFuture<>();
        promises.put(seq, future);
        return future;
    }

    private InvokeMeta getInvokeMeta(Method method){
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

    private void init(){
        if (inited) {
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            ChannelFuture channelFuture = null;
            try {
                channelFuture = new Bootstrap()
                        .group(new NioEventLoopGroup())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .handler(new ChannelInitializer<>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                        configuration.getMaxMessageSize(), 0, 4, 0,4));

                                pipeline.addLast(new LengthBasedOutboundHandler(configuration.getMaxMessageSize()));
                                pipeline.addLast(new JdkZlibEncoder());
                                pipeline.addLast(new KryoRequestMessageEncoder());
                                pipeline.addLast(new KryoResponseMessageDecoder());
                                pipeline.addLast(new ResponseMessageHandler(RpcClient.this));
                            }
                        })
                        .connect(configuration.getRemoteAddress(), configuration.getPort()).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel = channelFuture.channel();
            inited = true;
        }
    }


    void setResponseMessage(ResponseMessage msg) throws Exception {
        CompletableFuture<ResponseMessage> promise = promises.get(msg.getSequence());
        promise.complete(msg);
    }
}
