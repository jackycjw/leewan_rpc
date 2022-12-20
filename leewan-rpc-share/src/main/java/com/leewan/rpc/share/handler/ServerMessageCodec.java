package com.leewan.rpc.share.handler;

import com.leewan.rpc.share.configuration.Configuration;
import com.leewan.rpc.share.databind.DatabindService;
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

    private ResponseDataBinder responseDataBinder;

    public ServerMessageCodec(Class<? extends ResponseDataBinder> clazz) {
        responseDataBinder = DatabindService.getResponseDataBinder(clazz);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (o instanceof ResponseMessage response) {
            //序列化类型
            byteBuf.writeByte(responseDataBinder.getType());

            //序列化
            byte[] bytes = responseDataBinder.serialize(response);
            byteBuf.writeBytes(bytes);
            return;
        }
        throw new IllegalTypeException("response is not ResponseMessage");
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        //序列化类型
        byte type = byteBuf.readByte();

        //序列化内容
        byte[] content = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(content);
        RequestMessage requestMessage = DatabindService.getRequestDataBinder(type).deserialize(content);
        list.add(requestMessage);
    }
}
