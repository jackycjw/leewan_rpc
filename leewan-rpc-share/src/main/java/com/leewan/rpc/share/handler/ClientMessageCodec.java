package com.leewan.rpc.share.handler;

import com.leewan.rpc.share.configuration.Configuration;
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
    private ResponseDataBinder responseDataBinder;

    public ClientMessageCodec(Configuration configuration) {
        log.debug("创建序列化");
        String requestDataBinderClass = configuration.getRequestDataBinderClass();
        requestDataBinder = ObjectUtils.getSingleton(requestDataBinderClass,
                ()-> "请求序列化/反序列化方式["+requestDataBinderClass + "]实例化失败：");

        String responseDataBinderClass = configuration.getResponseDataBinderClass();
        responseDataBinder = ObjectUtils.getSingleton(responseDataBinderClass,
                ()-> "请求序列化/反序列化方式["+requestDataBinderClass + "]实例化失败：");
        log.debug("创建序列化结束");
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        log.debug("序列化。。。。。。");
        if (o instanceof RequestMessage request) {
            try {
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
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        ResponseMessage responseMessage = responseDataBinder.deserialize(bytes);
        list.add(responseMessage);
    }
}
