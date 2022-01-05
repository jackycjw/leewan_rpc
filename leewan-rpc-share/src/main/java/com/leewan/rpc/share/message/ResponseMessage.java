package com.leewan.rpc.share.message;

import lombok.Data;

@Data
public class ResponseMessage extends Message {
    private int invokeId;
    private Object response;
    private String exceptionMessage;
    private String exceptionType;
}
