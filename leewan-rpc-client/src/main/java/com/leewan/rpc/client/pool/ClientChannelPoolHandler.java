package com.leewan.rpc.client.pool;

import com.leewan.rpc.client.ClientConfiguration;
import com.leewan.rpc.client.RequestResponseContainer;
import com.leewan.rpc.client.handler.IdleHeartBeatHandler;
import com.leewan.rpc.client.handler.ResponseMessageHandler;
import com.leewan.rpc.share.handler.KryoMessageDecoder;
import com.leewan.rpc.share.handler.KryoMessageEncoder;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

/**
 * @author chenjw
 * @Date 2021/12/30 14:52
 */
public class ClientChannelPoolHandler extends AbstractChannelPoolHandler {

    private ClientConfiguration configuration;

    private RequestResponseContainer container;

    public ClientChannelPoolHandler(ClientConfiguration configuration, RequestResponseContainer container) {
        this.configuration = configuration;
        this.container = container;
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                configuration.getMaxMessageSize(), 0, 4, 0,4));
        pipeline.addLast(new LengthBasedOutboundHandler(configuration.getMaxMessageSize()));
        pipeline.addLast(new JdkZlibEncoder());
        pipeline.addLast(new KryoMessageEncoder());
        pipeline.addLast(new KryoMessageDecoder());
        pipeline.addLast(new ResponseMessageHandler(container));
        pipeline.addLast(new IdleHeartBeatHandler(configuration.getIdleHeartBeat()));
    }
}
