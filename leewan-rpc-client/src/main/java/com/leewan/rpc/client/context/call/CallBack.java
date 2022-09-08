package com.leewan.rpc.client.context.call;

import java.util.function.Consumer;

public interface CallBack<T> extends Consumer<T> {
    default void completeExceptionally(Throwable throwable){
        throwable.printStackTrace();
    }
}
