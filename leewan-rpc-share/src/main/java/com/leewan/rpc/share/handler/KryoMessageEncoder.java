package com.leewan.rpc.share.handler;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.leewan.rpc.share.message.Message;
import com.leewan.rpc.share.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;

import static com.leewan.rpc.share.message.Message.TYPE_REQUEST_MESSAGE;

/**
 * @author chenjw
 * @Date 2022/1/11 13:11
 */
public class KryoMessageEncoder extends MessageToByteEncoder<Message> {
    private Kryo kryo;

    public KryoMessageEncoder(){
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output, msg);
        output.flush();
        output.close();
        out.writeByte(msg.getType());
        out.writeBytes(outputStream.toByteArray());
    }
}
