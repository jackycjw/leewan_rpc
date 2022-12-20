package com.leewan.rpc.share.databind;

import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;

/**
 * 响应数据绑定
 */
public interface ResponseDataBinder {

    byte getType();

    /**
     * 序列化
     * @param response
     * @return
     */
    public abstract byte[] serialize(ResponseMessage response);


    /**
     * 反序列化
     * @param bytes
     * @return
     */
    public abstract ResponseMessage deserialize(byte[] bytes);


}
