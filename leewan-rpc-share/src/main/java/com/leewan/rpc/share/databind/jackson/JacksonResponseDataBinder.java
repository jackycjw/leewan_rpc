package com.leewan.rpc.share.databind.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leewan.rpc.share.databind.DataBinderType;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import com.leewan.rpc.share.except.DataBinderException;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.util.Assert;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.leewan.rpc.share.util.CollectionUtils.isEmpty;
import static com.leewan.rpc.share.util.ReflectUtils.getTypeReference;
import static com.leewan.rpc.share.util.ReflectUtils.resolveMethod;

public class JacksonResponseDataBinder implements ResponseDataBinder {

    private ObjectMapper mapper;


    public JacksonResponseDataBinder(){
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
    }


    @Override
    public byte getType() {
        return DataBinderType.JACKSON;
    }

    @Override
    public byte[] serialize(ResponseMessage response) {
        try {
            return mapper.writeValueAsBytes(response);
        } catch (JsonProcessingException e) {
            throw new DataBinderException(e.getMessage(), e);
        }
    }

    @Override
    public ResponseMessage deserialize(byte[] bytes) {

        try {
            ResponseMessage response = mapper.readValue(bytes, ResponseMessage.class);
            Method method = resolveMethod(response.getInvokeMeta());

            if (response.getResponse() != null) {
                Type returnType = method.getGenericReturnType();
                Object typedResponse = parse(response.getResponse(), returnType);
                response.setResponse(typedResponse);
            }
            return response;
        } catch (IOException e) {
            throw new DataBinderException(e.getMessage(), e);
        }
    }

    private Object parse(Object o, Type type){
        try {
            return mapper.readValue(mapper.writeValueAsBytes(o), getTypeReference(type));
        } catch (IOException e) {
            throw new DataBinderException(e.getMessage(), e);
        }
    }
}
