package com.leewan.rpc.client.boot;

import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.leewan.rpc.client.context.netty.PooledChannelFactory;
import com.leewan.rpc.client.context.netty.handler.IdleHeartBeatHandler;
import com.leewan.rpc.client.context.netty.handler.LogHandler;
import com.leewan.rpc.share.databind.jackson.JacksonRequestDataBinder;
import com.leewan.rpc.share.databind.jackson.JacksonResponseDataBinder;
import com.leewan.rpc.share.handler.LengthBasedOutboundHandler;
import com.leewan.rpc.share.internal.dto.HearBeatDTO;
import com.leewan.rpc.share.message.InvokeMeta;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;
import com.leewan.rpc.share.message.SequenceMessage;
import lombok.SneakyThrows;
import org.springframework.aot.generate.GeneratedTypeReference;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.javapoet.ClassName;

import javax.crypto.Cipher;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HintRegister implements RuntimeHintsRegistrar {
    @SneakyThrows
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.proxies()
                .registerJdkProxy(com.leewan.rpc.share.internal.service.HeartBeatService.class);


        registerType(hints, IdleHeartBeatHandler.class);
        registerType(hints, LengthBasedOutboundHandler.class);
        registerType(hints, PooledChannelFactory.class);
        registerType(hints, SelectorProvider.class);
        registerType(hints, LogHandler.class);
        registerType(hints, JacksonResponseDataBinder.class);
        registerType(hints, JacksonRequestDataBinder.class);
        registerType(hints, RequestMessage.class);
        registerType(hints, ResponseMessage.class);
        registerType(hints, InvokeMeta.class);
        registerType(hints, SequenceMessage.class);
        registerType(hints, HashSet.class);
        registerType(hints, HashMap.class);
        registerType(hints, ArrayList.class);
        registerType(hints, HearBeatDTO.class);
        registerType(hints, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class);
        registerType(hints, com.esotericsoftware.kryo.serializers.MapSerializer.class);
        registerType(hints, com.esotericsoftware.kryo.serializers.CollectionSerializer.class);
        registerType(hints, com.esotericsoftware.kryo.serializers.DefaultArraySerializers.class);
        registerType(hints, DefaultArraySerializers.BooleanArraySerializer.class);
        registerType(hints, DefaultArraySerializers.ByteArraySerializer.class);
        registerType(hints, DefaultArraySerializers.CharArraySerializer.class);
        registerType(hints, DefaultArraySerializers.DoubleArraySerializer.class);
        registerType(hints, DefaultArraySerializers.FloatArraySerializer.class);
        registerType(hints, DefaultArraySerializers.IntArraySerializer.class);
        registerType(hints, DefaultArraySerializers.LongArraySerializer.class);
        registerType(hints, DefaultArraySerializers.ObjectArraySerializer.class);
        registerType(hints, DefaultArraySerializers.ShortArraySerializer.class);
        registerType(hints, DefaultArraySerializers.StringArraySerializer.class);
        registerType(hints, Cipher.getInstance("RSA").getClass());
    }

    private void registerType(RuntimeHints hints, Class cls){
        hints.reflection().registerType(cls,
                MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS);

        hints.serialization().registerType(GeneratedTypeReference.of(ClassName.get(cls)));

        Class superclass = cls.getSuperclass();
        if (superclass != null) {
            registerType(hints, superclass);
        }
    }


}
