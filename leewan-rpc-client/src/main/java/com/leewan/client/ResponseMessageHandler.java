package com.leewan.client;

import com.leewan.share.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseMessageHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    private RpcClient rpcClient;

    public ResponseMessageHandler(RpcClient rpcClient) {
        super();
        this.rpcClient = rpcClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        this.rpcClient.setResponseMessage(msg);
    }
}
