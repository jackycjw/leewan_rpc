package com.leewan.rpc.share.call;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Call {
    /**
     * 调用时长
     * @return
     */
    int requestTimeout() default -1;

    int retry() default -1;
}
