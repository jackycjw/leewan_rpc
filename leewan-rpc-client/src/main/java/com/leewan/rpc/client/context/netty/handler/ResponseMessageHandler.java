package com.leewan.rpc.client.context.netty.handler;

import com.leewan.rpc.client.context.RequestResponseContainer;
import com.leewan.rpc.share.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseMessageHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    private final RequestResponseContainer requestResponseContainer;

    public ResponseMessageHandler(RequestResponseContainer requestResponseContainer) {
        this.requestResponseContainer = requestResponseContainer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) {
        this.requestResponseContainer.completeFuture(msg.getSequence(), msg);
    }
}
