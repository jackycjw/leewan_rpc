package com.leewan.rpc.share.databind;

import com.esotericsoftware.kryo.io.Output;
import com.leewan.rpc.share.message.Message;
import com.leewan.rpc.share.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 请求数据绑定
 */
public interface RequestDataBinder {


    /**
     * 序列化
     * @param request
     * @return
     */
    public abstract byte[] serialize(RequestMessage request);


    /**
     * 反序列化
     * @param bytes
     * @return
     */
    public abstract RequestMessage deserialize(byte[] bytes);

}
