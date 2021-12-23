package com.leewan.share.handler.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.leewan.share.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.List;

public class KryoRequestMessageDecoder extends ByteToMessageDecoder {
    private Kryo kryo;

    public KryoRequestMessageDecoder(){
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bs = new byte[in.readableBytes()];
        in.readBytes(bs);
        RequestMessage requestMessage = kryo.readObject(new Input(bs), RequestMessage.class);
        out.add(requestMessage);
        //无需释放，解码器会自动释放
//        in.release();
    }
}
