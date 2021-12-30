package com.leewan.rpc.share.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LengthBasedOutboundHandler extends ChannelOutboundHandlerAdapter {

    private int maxMessageSize;

    public LengthBasedOutboundHandler(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf byteBuf) {
            //可读字节数 即上一环节写入了多少
            int length = byteBuf.readableBytes();
            if (length > this.maxMessageSize) {
                throw new TooLongFrameException();
            }
            ByteBuf lengthBuf = ctx.alloc().buffer(4);
            lengthBuf.writeInt(length);
            ctx.write(lengthBuf);
            ctx.write(msg, promise);
            log.debug("消息体长度:{}, 消息有效内容长度:{}", length + 4, length);
        }
    }
}
