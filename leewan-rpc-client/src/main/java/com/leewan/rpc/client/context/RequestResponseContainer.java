package com.leewan.rpc.client.context;

import com.leewan.rpc.share.message.ResponseMessage;

import java.util.concurrent.Future;

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
     * @param sequence 根据请求序号创建Future
     * @return Future
     */
    Future<ResponseMessage> createFuture(int sequence);


    /**
     *
     * @param response 相应体
     */
    void completeFuture(int sequence, ResponseMessage response);


}
