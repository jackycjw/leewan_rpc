package com.leewan.rpc.client.context;

import com.leewan.rpc.client.context.call.CallBack;
import com.leewan.rpc.client.context.call.CallPerformance;
import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author chenjw
 * @Date 2021/12/30 15:59
 */
public interface RequestResponseContainer {

    /**
     * 获取请求序号
     * @return 请求的序号
     */
    int getSequence();

    /**
     * 创建Future
     * @param request 根据请求创建Future
     * @return Future
     */
    Future<ResponseMessage> createFuture(RequestMessage request);

    /**
     * 保存回调
     * @param request
     * @param callback
     */
    void saveCallback(RequestMessage request, CallBack callback, CallPerformance performance);


    /**
     *
     * @param response 相应体
     */
    void completeFuture(int sequence, ResponseMessage response);


}
