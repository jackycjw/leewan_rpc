package com.leewan.rpc.server;


import com.leewan.rpc.server.filter.Filter;
import com.leewan.rpc.server.handler.ServiceHandler;
import com.leewan.rpc.server.internal.HeartBeatServiceImpl;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import com.leewan.rpc.share.handler.ServerMessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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

    private final ServiceContainer serviceContainer = new ServiceContainer();
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

    private final List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter){
        filters.add(filter);
    }


    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    @SneakyThrows
    public void start()  {
        // 绑定心跳服务
        bind(new HeartBeatServiceImpl());
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
                        pipeline.addLast(new JdkZlibEncoder(9));


                        //请求的反序列化  响应的序列化
                        pipeline.addLast(new ServerMessageCodec(configuration));
                        pipeline.addLast(serviceGroup, new ServiceHandler(serviceContainer, filters));
                    }
                })
                .bind(this.configuration.getBindAddress(), this.configuration.getPort()).sync();

        channel = channelFuture.channel();
    }

}
