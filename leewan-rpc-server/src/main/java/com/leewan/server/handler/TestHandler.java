package com.leewan.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

public class TestHandler extends SimpleChannelInboundHandler<ByteBuf> {

    long total = 0;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int readableBytes = msg.readableBytes();
        byte[] bs = new byte[readableBytes];

        msg.readBytes(bs);

        total += readableBytes;

        System.out.println(total);
    }
}
