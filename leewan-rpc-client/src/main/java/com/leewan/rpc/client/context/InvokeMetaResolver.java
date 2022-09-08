package com.leewan.rpc.client.context;

import com.leewan.rpc.client.context.call.CallPerformance;
import com.leewan.rpc.share.message.InvokeMeta;

import java.lang.reflect.Method;

/**
 * @author chenjw
 * @Date 2021/12/30 15:56
 */
public interface InvokeMetaResolver {

    InvokeMeta getInvokeMeta(Method method);

    /**
     * 获取请求的执行超时时长
     * @param method 方法对象
     * @return 调用参数
     */
    CallPerformance getCallPerformance(Method method);
}
