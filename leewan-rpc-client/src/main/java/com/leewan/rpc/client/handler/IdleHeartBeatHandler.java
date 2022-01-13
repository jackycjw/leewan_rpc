package com.leewan.rpc.client.handler;

import com.leewan.rpc.share.message.HeartBeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjw
 * @Date 2022/1/11 13:17
 */
@Slf4j
public class IdleHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent stateEvent) {
            IdleState state = stateEvent.state();
            if (state == IdleState.WRITER_IDLE) {
                //防止断链
                ctx.channel().writeAndFlush(new HeartBeat());
                log.debug("心跳包");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("channel {} inactive", ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }
}
