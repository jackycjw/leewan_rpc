package com.leewan.rpc.client.context.netty;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.netty.handler.IdleHeartBeatHandler;
import com.leewan.rpc.client.context.netty.handler.LogHandler;
import com.leewan.rpc.client.context.netty.handler.ResponseMessageHandler;
import com.leewan.rpc.share.handler.ClientMessageCodec;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import com.leewan.rpc.share.internal.service.HeartBeatService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PooledChannelFactory implements PooledObjectFactory<Channel> {

    private ClientContext context;
    private final Bootstrap bootstrap;
    private HeartBeatService heartBeatService;


    public PooledChannelFactory(ClientConfiguration configuration, ClientContext context, NioEventLoopGroup executors) {
        bootstrap = new Bootstrap();
        this.context = context;
        bootstrap.channel(NioSocketChannel.class)
                .group(executors)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        log.info("new channel {} created", ch);
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0,configuration.getIdleHeartBeat(),0, TimeUnit.SECONDS));
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                configuration.getMaxMessageSize(), 0, 4, 0,4));
                        pipeline.addLast(new LogHandler());
                        pipeline.addLast(new LengthBasedOutboundHandler(configuration.getMaxMessageSize()));
                        pipeline.addLast(new JdkZlibEncoder(configuration.getCompressionLevel()));
                        pipeline.addLast(new JdkZlibDecoder());

                        //请求的序列化  响应的反序列化
                        pipeline.addLast(new ClientMessageCodec(configuration.getRequestDataBinderClass()));

                        pipeline.addLast(new ResponseMessageHandler(context));
                        pipeline.addLast(new IdleHeartBeatHandler(heartBeatService));
                    }
                })
                .remoteAddress(configuration.getRemoteAddress(), configuration.getPort());
    }

    public void initHeartBeatService(){
        heartBeatService = context.getService(HeartBeatService.class);
    }

    @Override
    public void activateObject(PooledObject<Channel> pooledObject) {
    }

    @Override
    public void destroyObject(PooledObject<Channel> pooledObject) {
        Channel channel = pooledObject.getObject();

        if (channel != null) {
            if (log.isInfoEnabled()) {
                log.info("will destroy channel:" + channel);
            }
            channel.disconnect();
            channel.close();
        }
    }

    @Override
    public PooledObject<Channel> makeObject() throws Exception {
        Channel channel = bootstrap.connect().sync().channel();
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) {

    }

    @Override
    public boolean validateObject(PooledObject<Channel> pooledObject) {
        Channel channel = pooledObject.getObject();
        if (channel != null && channel.isActive()) {
            return true;
        }
        if (log.isInfoEnabled()) {
            log.info("channel valid false,channel:" + channel);
        }
        return false;
    }
}
