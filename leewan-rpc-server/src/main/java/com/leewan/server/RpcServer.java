package com.leewan.server;


import com.leewan.server.except.BindServiceException;
import com.leewan.server.handler.ServiceHandler;
import com.leewan.share.handler.LengthBasedOutboundHandler;
import com.leewan.share.handler.codec.KryoRequestMessageDecoder;
import com.leewan.share.handler.codec.KryoResponseMessageEncoder;
import com.leewan.share.util.Assert;
import com.leewan.share.util.ReflectUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjw
 * @Date 2021/12/17 12:56
 */
@Slf4j
public class RpcServer {

    private ServerConfiguration configuration;

    public void configure(ServerConfiguration configuration){
        this.configuration = configuration;
    }

    private ServiceContainer serviceContainer = new ServiceContainer();
    /**
     * 绑定服务
     * @param service
     */
    public void bind(Object service){
        this.serviceContainer.bind(service);
    }

    public void bind(Object service, Class<?>...interfaces){
        this.serviceContainer.bind(service, interfaces);
    }

    public void bind(Object service, List<Class<?>> interfaces){
        this.serviceContainer.bind(service, interfaces);
    }


    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    @SneakyThrows
    public void start()  {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        DefaultEventLoopGroup serviceGroup = new DefaultEventLoopGroup();

        ChannelFuture channelFuture = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                configuration.getMaxMessageSize(), 0, 4, 0,4));
                        pipeline.addLast(new LengthBasedOutboundHandler(configuration.getMaxMessageSize()));
                        pipeline.addLast(new JdkZlibDecoder());
                        pipeline.addLast(new KryoRequestMessageDecoder());
                        pipeline.addLast(new KryoResponseMessageEncoder());
                        pipeline.addLast(serviceGroup, new ServiceHandler(serviceContainer));
                    }
                })
                .bind(this.configuration.getBindAddress(), this.configuration.getPort()).sync();

        channel = channelFuture.channel();
    }

}
