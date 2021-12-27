package com.leewan.server.handler;

import com.leewan.ref.Invoke;
import com.leewan.server.ServiceContainer;
import com.leewan.share.message.InvokeMeta;
import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServiceHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private ServiceContainer serviceContainer;

    public ServiceHandler(ServiceContainer serviceContainer) {
        super();
        this.serviceContainer = serviceContainer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        ResponseMessage response = new ResponseMessage();
        response.setSequence(msg.getSequence());
        InvokeMeta meta = msg.getInvokeMeta();
        try {
            Invoke invoke = serviceContainer.getInvoke(meta);
            Object result = invoke.invoke(formatParameters(msg.getParameters()));
            response.setResponse(result);
            response.setInvokeId(meta.getInvokeId());
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setExceptionMessage(e.getMessage());
        }
        ctx.writeAndFlush(response);
    }

    private Object[] formatParameters(List<Object> parameters){
        Object[] objects = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            objects[i] = parameters.get(i);
        }
        return objects;
    }
}
