package com.leewan.rpc.client.context;

import com.leewan.rpc.client.context.call.CallPerformance;
import com.leewan.rpc.client.intercept.Interceptor;

import java.lang.reflect.Method;

/**
 * @author chenjw
 * @Date 2021/12/30 16:32
 */
public interface ClientContext extends InvokeMetaResolver, RequestResponseContainer {

    /**
     * 初始化
     */
    void initialize();
    /**
     * 获取服务实例
     * @param service 服务接口类
     * @return 服务对象
     */
    <T> T getService(Class<T> service);

    /**
     * 注册拦截器
     * @param interceptor
     */
    void registerInterceptor(Interceptor interceptor);

    /**
     * 获取请求的执行超时时长
     * @param method 方法对象
     * @return 调用参数
     */
    CallPerformance getCallPerformance(Method method);
}
