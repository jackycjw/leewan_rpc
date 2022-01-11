package com.leewan.rpc.share.handler;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.leewan.rpc.share.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.leewan.rpc.share.message.Message.*;

public class KryoMessageDecoder extends ByteToMessageDecoder {
    private Kryo kryo;

    public KryoMessageDecoder(){
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        Class<?> clazz = null;
        switch (type) {
            case TYPE_REQUEST_MESSAGE:
                clazz = RequestMessage.class;
                break;
            case TYPE_RESPONSE_MESSAGE:
                clazz = ResponseMessage.class;
                break;
            case TYPE_HEART_BEAT:
                clazz = HeartBeat.class;
                break;
            default:
                throw new IllegalArgumentException("message type " + type + " is not Allowed");
        }
        byte[] bs = new byte[in.readableBytes()];
        in.readBytes(bs);
        RequestMessage requestMessage = kryo.readObject(new Input(bs), RequestMessage.class);
        out.add(requestMessage);
    }

}
