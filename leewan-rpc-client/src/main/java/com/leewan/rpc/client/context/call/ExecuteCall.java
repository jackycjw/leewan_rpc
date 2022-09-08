package com.leewan.rpc.client.context.call;

import java.util.function.Consumer;

/**
 * 执行调用
 * 提供异步调用
 */
public class ExecuteCall {

    private static ThreadLocal<Boolean> asyn = new ThreadLocal<>();

    private static ThreadLocal<CallBack> consumers = new ThreadLocal<>();
    public static <T> void executeAsyn(Runnable serviceExecutor, CallBack<T> callBack){
        asyn.set(true);
        consumers.set(callBack);
        try {
            serviceExecutor.run();
        } finally {
            consumers.remove();
            asyn.remove();
        }
    }

    /**
     * 判断当前是否是异步
     * @return
     */
    public static boolean isCurrentAsyn(){
        Boolean flag = asyn.get();
        return flag != null && flag;
    }

    /**
     * 获取异步调用回调
     * @return
     */
    public static CallBack getCurrentConsumer(){
        return consumers.get();
    }

    public static void main(String[] args) {
    }
}
