package com.leewan.share;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leewan.share.message.MethodMeta;
import com.leewan.share.message.RequestMessage;
import io.netty.handler.codec.compression.GzipOptions;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Test {

    public static void main(String[] args) throws Exception {
        jackson();

        kryo();

//        GzipOptions options = new GzipOptions();
    }

    static void jackson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writeValueAsBytes(getRequestMessage());
        System.out.println(bytes.length);
    }
    static void kryo(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        Output output = new Output(outputStream);
        kryo.writeObject(output, getRequestMessage());
        output.flush();
        output.close();

        System.out.println(outputStream.toByteArray().length);
    }
    public static RequestMessage getRequestMessage(){
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethodMeta(getMethodMeta());
        HashMap<Object, Object> map1 = new HashMap<>();
        map1.put("k1","v1");

        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("k2","v2");

        HashMap<Object, Object> map3 = new LinkedHashMap<>();
        map3.put("k3","v3");

        requestMessage.getParameters().add("111111111");
        requestMessage.getParameters().add(map1);
        requestMessage.getParameters().add(map2);
        requestMessage.getParameters().add(map3);
        return requestMessage;
    }

    public static MethodMeta getMethodMeta(){
        MethodMeta meta = new MethodMeta();
//        meta.setMethod(10);
        meta.setMethodName("hhhh");
        meta.setParameterTypeNames(Arrays.asList("aa","bb"));
        return meta;
    }
}
