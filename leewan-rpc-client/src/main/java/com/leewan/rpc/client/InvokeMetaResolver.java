package com.leewan.rpc.client;

import com.leewan.rpc.client.call.CallPerformance;
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
     * @param method
     * @return
     */
    CallPerformance getCallPerformance(Method method);
}
