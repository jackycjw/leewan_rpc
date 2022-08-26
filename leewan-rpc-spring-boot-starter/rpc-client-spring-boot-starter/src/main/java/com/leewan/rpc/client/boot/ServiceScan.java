package com.leewan.rpc.client.boot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcClientServiceScannerRegistrar.class)
public @interface ServiceScan {
    /**
     * 用于扫描 RPC 接口包
     * @return
     */
    String[] value() default {};
}
