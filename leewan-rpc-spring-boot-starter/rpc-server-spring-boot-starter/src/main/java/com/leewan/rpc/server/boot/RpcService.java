package com.leewan.rpc.server.boot;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 绑定的接口，如果为空，则扫描当前对象的所有接口
     * @return
     */
    Class<?>[] value() default {};
}
