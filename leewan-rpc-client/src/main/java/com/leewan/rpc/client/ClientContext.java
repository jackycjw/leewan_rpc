package com.leewan.rpc.client;

import com.leewan.rpc.client.intercept.Interceptor;
/**
 * @author chenjw
 * @Date 2021/12/30 16:32
 */
public interface ClientContext extends InvokeMetaResolver, RequestResponseContainer {

    /**
     * 获取服务实例
     * @param service
     * @return
     * @param <T>
     */
    <T> T getService(Class<T> service);

    void registerInterceptor(Interceptor interceptor);
}
