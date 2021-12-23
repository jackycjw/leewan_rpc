package com.leewan.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test2Handler extends ChannelInboundHandlerAdapter {

    long total = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("{},当前线程:{}, 消息：{}", this.toString(), Thread.currentThread().getName(), msg.getClass().getName());
        super.channelRead(ctx, msg);
    }
}
