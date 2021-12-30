package com.leewan.rpc.client;

import com.leewan.rpc.share.message.ResponseMessage;

import java.util.concurrent.Future;

/**
 * @author chenjw
 * @Date 2021/12/30 15:59
 */
public interface RequestResponseContainer {

    /**
     * 获取请求序号
     * @return
     */
    int getSequence();

    /**
     * 获取Future
     * @param sequence
     * @return
     */
    Future<ResponseMessage> getFuture(int sequence);


    /**
     *
     * @param response
     */
    void completeFuture(int sequence, ResponseMessage response);


}
