package com.leewan.rpc.share.handler;

import com.leewan.rpc.share.configuration.Configuration;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import com.leewan.rpc.share.except.DataBinderCreateException;
import com.leewan.rpc.share.except.IllegalTypeException;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.ObjectUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class ServerMessageCodec extends ByteToMessageCodec {

    private RequestDataBinder requestDataBinder;
    private ResponseDataBinder responseDataBinder;

    public ServerMessageCodec(Configuration configuration) {
        String requestDataBinderClass = configuration.getRequestDataBinderClass();
        requestDataBinder = ObjectUtils.getSingleton(requestDataBinderClass,
                ()-> "请求序列化/反序列化方式["+requestDataBinderClass + "]实例化失败：");

        String responseDataBinderClass = configuration.getResponseDataBinderClass();
        responseDataBinder = ObjectUtils.getSingleton(responseDataBinderClass,
                ()-> "请求序列化/反序列化方式["+requestDataBinderClass + "]实例化失败：");
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (o instanceof ResponseMessage response) {
            byte[] bytes = responseDataBinder.serialize(response);
            byteBuf.writeBytes(bytes);
            return;
        }
        throw new IllegalTypeException("response is not ResponseMessage");
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        RequestMessage requestMessage = requestDataBinder.deserialize(bytes);
        list.add(requestMessage);
    }
}
