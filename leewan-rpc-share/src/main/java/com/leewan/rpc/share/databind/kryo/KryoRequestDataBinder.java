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

public class KryoRequestDataBinder implements RequestDataBinder {
    private Kryo kryo;

    public KryoRequestDataBinder(){
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    public byte getType() {
        return DataBinderType.KRYO;
    }
    @Override
    public byte[] serialize(RequestMessage request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeClassAndObject(output, request);
        output.close();
        return outputStream.toByteArray();
    }

    @Override
    public RequestMessage deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        RequestMessage message = (RequestMessage) kryo.readClassAndObject(input);
        input.close();
        return message;
    }
}
