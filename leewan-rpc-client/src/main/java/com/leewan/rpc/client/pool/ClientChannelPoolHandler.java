package com.leewan.rpc.client.pool;

import com.leewan.rpc.client.ClientConfiguration;
import com.leewan.rpc.client.RequestResponseContainer;
import com.leewan.rpc.client.ResponseMessageHandler;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import com.leewan.rpc.share.handler.codec.KryoRequestMessageEncoder;
import com.leewan.rpc.share.handler.codec.KryoResponseMessageDecoder;
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
        pipeline.addLast(new KryoRequestMessageEncoder());
        pipeline.addLast(new KryoResponseMessageDecoder());
        pipeline.addLast(new ResponseMessageHandler(container));
    }
}
