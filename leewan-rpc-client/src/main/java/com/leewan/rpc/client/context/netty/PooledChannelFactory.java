package com.leewan.rpc.client.context.netty;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.RequestResponseContainer;
import com.leewan.rpc.client.context.netty.handler.IdleHeartBeatHandler;
import com.leewan.rpc.client.context.netty.handler.ResponseMessageHandler;
import com.leewan.rpc.share.handler.KryoMessageDecoder;
import com.leewan.rpc.share.handler.KryoMessageEncoder;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PooledChannelFactory implements PooledObjectFactory<Channel> {

    private final Bootstrap bootstrap;

    public PooledChannelFactory(ClientConfiguration configuration, RequestResponseContainer container) {
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup(configuration.getThreadNum()))
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        log.info("new channel {} created", ch);
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0,configuration.getIdleHeartBeat(),0, TimeUnit.SECONDS));

                        //
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                configuration.getMaxMessageSize(), 0, 4, 0,4));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof ByteBuf buf) {
                                    log.debug("收到: {}", buf.readableBytes());
                                }
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast(new LengthBasedOutboundHandler(configuration.getMaxMessageSize()));
                        pipeline.addLast(new JdkZlibEncoder());

                        pipeline.addLast(new KryoMessageEncoder());
                        pipeline.addLast(new KryoMessageDecoder());

                        pipeline.addLast(new ResponseMessageHandler(container));
                        pipeline.addLast(new IdleHeartBeatHandler());
                    }
                })
                .remoteAddress(configuration.getRemoteAddress(), configuration.getPort());
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
