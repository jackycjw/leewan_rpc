package com.leewan.rpc.server.handler;

import com.leewan.ref.Invoke;
import com.leewan.rpc.server.ServiceContainer;
import com.leewan.rpc.server.filter.FilterChain;
import com.leewan.rpc.server.filter.DefaultFilterChain;
import com.leewan.rpc.server.filter.Filter;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<RequestMessage> implements Filter {

    private ServiceContainer serviceContainer;

    private FilterChain filterChain;

    public ServiceHandler(ServiceContainer serviceContainer, List<Filter> filters) {
        super();
        this.serviceContainer = serviceContainer;
        DefaultFilterChain filterChain = new DefaultFilterChain();
        filters.stream().forEach(filterChain::addFilter);
        filterChain.addFilter(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        ResponseMessage response = new ResponseMessage();
        this.filterChain.doFilter(msg, response);
        response.setSequence(msg.getSequence());
        ctx.writeAndFlush(response);
    }

    private Object[] formatParameters(List<Object> parameters){
        Object[] objects = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            objects[i] = parameters.get(i);
        }
        return objects;
    }

    @Override
    public void doFilter(RequestMessage request, ResponseMessage response, FilterChain chain) {
        InvokeMeta meta = request.getInvokeMeta();
        try {
            Invoke invoke = serviceContainer.getInvoke(meta);
            Object result = invoke.invoke(formatParameters(request.getParameters()));
            response.setResponse(result);
            response.setInvokeId(meta.getInvokeId());
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setExceptionMessage(e.getMessage());
        }
    }
}
