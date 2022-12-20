package com.leewan.rpc.share.handler;

import com.leewan.rpc.share.configuration.Configuration;
import com.leewan.rpc.share.databind.DatabindService;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import com.leewan.rpc.share.except.DataBinderCreateException;
import com.leewan.rpc.share.except.IllegalTypeException;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.Assert;
import com.leewan.rpc.share.util.ObjectUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Slf4j
public class ClientMessageCodec extends ByteToMessageCodec {

    private RequestDataBinder requestDataBinder;

    public ClientMessageCodec(Class requestDataBinderClass) {
        log.debug("创建序列化");
        requestDataBinder = DatabindService.getRequestDataBinder(requestDataBinderClass);
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (o instanceof RequestMessage request) {
            try {
                //写入序列化类型
                byteBuf.writeByte(requestDataBinder.getType());

                //实体序列化
                byte[] bytes = requestDataBinder.serialize(request);
                log.debug("序列化：size {}, data:{}", bytes.length, new String(bytes));
                byteBuf.writeBytes(bytes);
                return;
            }catch (Throwable e){
                log.error(e.getMessage(), e);
                throw e;
            }
        }
        throw new IllegalTypeException("request is not RequestMessage");
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        byte type = byteBuf.readByte();
        byte[] content = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(content);
        ResponseMessage responseMessage = DatabindService.getResponseDataBinder(type).deserialize(content);
        list.add(responseMessage);
    }
}
