package com.leewan.rpc.share.message;

import lombok.Data;

@Data
public class ResponseMessage extends SequenceMessage {
    private int invokeId;

    /**
     * 方法元数据
     */
    private InvokeMeta invokeMeta;
    private Object response;
    private String exceptionMessage;
    private String exceptionType;
}
