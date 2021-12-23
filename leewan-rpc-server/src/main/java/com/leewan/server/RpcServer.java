package com.leewan.server;


import com.leewan.server.except.BindServiceException;
import com.leewan.server.handler.Test2Handler;
import com.leewan.server.handler.TestHandler;
import com.leewan.share.handler.codec.KryoRequestMessageDecoder;
import com.leewan.share.message.RequestMessage;
import com.leewan.share.util.Assert;
import com.leewan.share.util.ReflectUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjw
 * @Date 2021/12/17 12:56
 */
public class RpcServer {

    private RpcServerConfiguration configuration;

    public void configure(RpcServerConfiguration configuration){
        this.configuration = configuration;
    }

    private Map<Class<?>, Object> services = new HashMap<>();

    /**
     * 绑定服务
     * @param service
     */
    public void bind(Object service){
        List<Class<?>> interfaces = ReflectUtils.findInterfaces(service.getClass());
        bind(service, interfaces);
    }

    public void bind(Object service, Class<?>...interfaces){
        this.bind(service, Arrays.stream(interfaces).collect(Collectors.toList()));
    }

    public void bind(Object service, List<Class<?>> interfaces){
        Assert.notNull(service, "绑定的服务不能为空");
        interfaces.stream().forEach(inter -> {
            if (services.containsKey(inter)) {
                Object bindedService = services.get(inter);
                if (bindedService.getClass().equals(service.getClass())) {
                    throw new BindServiceException(
                            bindedService.getClass().getName() + "服务已存在，无需重新绑定");
                } else {
                    throw new BindServiceException(
                            "【"+ bindedService.getClass().getName() + "】与【"
                            + service.getClass().getName() + "】,实现了共同的接口【" + inter.getName() + "】");
                }
            } else {
                services.put(inter, service);
            }
        });
    }


    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    configuration.getMaxMessageSize(), 0, 4, 0,4));
                            pipeline.addLast(new JdkZlibDecoder());
                            pipeline.addLast(new KryoRequestMessageDecoder());
                            pipeline.addLast(new TestHandler());
                        }
                    })
                    .bind(this.configuration.getBindAddress(), this.configuration.getPort()).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
