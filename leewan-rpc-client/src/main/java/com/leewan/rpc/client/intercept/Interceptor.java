package com.leewan.rpc.client.intercept;

import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;

/**
 * @author chenjw
 * @Date 2021/12/30 15:44
 */
public interface Interceptor {

    void preHandle(RequestMessage request);

    void postHandle(RequestMessage request, ResponseMessage response);
}
