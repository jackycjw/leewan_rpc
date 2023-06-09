package com.leewan.rpc.share.databind.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.leewan.rpc.share.databind.DataBinderType;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;

public class KryoResponseDataBinder extends AbstractKryoDataBinder implements ResponseDataBinder {

    @Override
    public byte getType() {
        return DataBinderType.KRYO;
    }

    @Override
    public byte[] serialize(ResponseMessage response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        getKryo().writeClassAndObject(output, response);
        output.close();
        return outputStream.toByteArray();
    }

    @Override
    public ResponseMessage deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        ResponseMessage message = (ResponseMessage) getKryo().readClassAndObject(input);
        input.close();
        return message;
    }
}
