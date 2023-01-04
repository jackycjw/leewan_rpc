package com.leewan.rpc.share.databind.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.leewan.rpc.share.databind.DataBinderType;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.message.Message;
import com.leewan.rpc.share.message.RequestMessage;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;

public class KryoRequestDataBinder extends AbstractKryoDataBinder implements RequestDataBinder {

    @Override
    public byte getType() {
        return DataBinderType.KRYO;
    }
    @Override
    public byte[] serialize(RequestMessage request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        getKryo().writeClassAndObject(output, request);
        output.close();
        return outputStream.toByteArray();
    }

    @Override
    public RequestMessage deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        RequestMessage message = (RequestMessage) getKryo().readClassAndObject(input);
        input.close();
        return message;
    }
}
