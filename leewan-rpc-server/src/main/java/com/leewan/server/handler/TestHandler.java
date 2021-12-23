package com.leewan.server.handler;

import com.leewan.share.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHandler extends SimpleChannelInboundHandler<RequestMessage> {

    long total = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        log.debug("收到消息:{}", msg);
    }
}
