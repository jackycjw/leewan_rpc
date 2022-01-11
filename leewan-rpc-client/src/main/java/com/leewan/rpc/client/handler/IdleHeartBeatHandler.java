package com.leewan.rpc.client.handler;

import com.leewan.rpc.share.message.HeartBeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author chenjw
 * @Date 2022/1/11 13:17
 */
public class IdleHeartBeatHandler extends IdleStateHandler {
    public IdleHeartBeatHandler(int writerIdleTimeSeconds) {
        super(0, writerIdleTimeSeconds, 0);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent stateEvent) {
            IdleState state = stateEvent.state();
            if (state == IdleState.WRITER_IDLE) {
                //防止断链
                ctx.channel().writeAndFlush(new HeartBeat());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
