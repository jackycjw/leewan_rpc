package com.leewan.share.handler.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.List;

public class KryoResponseMessageDecoder extends ByteToMessageDecoder {
    private Kryo kryo;

    public KryoResponseMessageDecoder(){
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bs = new byte[in.readableBytes()];
        in.readBytes(bs);
        ResponseMessage responseMessage = kryo.readObject(new Input(bs), ResponseMessage.class);
        out.add(responseMessage);
    }
}
