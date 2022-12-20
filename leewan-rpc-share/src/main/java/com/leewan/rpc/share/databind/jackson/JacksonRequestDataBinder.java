package com.leewan.rpc.share.databind.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leewan.rpc.share.databind.DataBinderType;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.except.DataBinderException;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.util.Assert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.leewan.rpc.share.util.CollectionUtils.isEmpty;
import static com.leewan.rpc.share.util.ReflectUtils.getTypeReference;
import static com.leewan.rpc.share.util.ReflectUtils.resolveMethod;

@Slf4j
public class JacksonRequestDataBinder implements RequestDataBinder {

    private ObjectMapper mapper;


    public JacksonRequestDataBinder(){
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
    public byte[] serialize(RequestMessage request) {
        try {
            return mapper.writeValueAsBytes(request);
        } catch (IOException e) {
            throw new DataBinderException(e.getMessage(), e);
        }
    }


    @Override
    public RequestMessage deserialize(byte[] bytes) {

        try {
            System.out.println(new String(bytes));
            RequestMessage request = mapper.readValue(bytes, RequestMessage.class);

            List<Object> parameters = request.getParameters();
            if (!isEmpty(parameters)) {
                List<Object> typedParameters = new ArrayList<>(parameters.size());
                Method method = resolveMethod(request.getInvokeMeta());
                Type[] parameterTypes = method.getGenericParameterTypes();
                Assert.state(parameterTypes.length == parameters.size(), "参数非法");

                for (int i = 0; i < parameters.size(); i++) {
                    Object parameter = parameters.get(i);
                    Type type = parameterTypes[i];
                    typedParameters.add(parse(parameter, type));
                }
                request.setParameters(typedParameters);
            }
            return request;

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
