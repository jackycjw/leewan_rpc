package com.leewan.rpc.client.context.netty.handler;

import com.leewan.rpc.client.context.call.CallBack;
import com.leewan.rpc.client.context.call.ExecuteCall;
import com.leewan.rpc.share.internal.dto.HearBeatDTO;
import com.leewan.rpc.share.internal.service.HeartBeatService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjw
 * @Date 2022/1/11 13:17
 */
@Slf4j
public class IdleHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private HeartBeatService heartBeatService;

    public IdleHeartBeatHandler(HeartBeatService heartBeatService) {
        this.heartBeatService = heartBeatService;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent stateEvent) {
            IdleState state = stateEvent.state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("send heart beat.");
                //防止断链
                ExecuteCall.executeAsyn(() -> {
                    heartBeatService.send(new HearBeatDTO());
                }, new CallBack<Object>() {
                    @Override
                    public void accept(Object o) {
                        log.info("heart beat ok.");
                    }

                    @Override
                    public void completeExceptionally(Throwable throwable) {
                        log.error("heart beat timeout, close channel.");
                        ctx.close();
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.error("channel {} inactive", ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }
}
