package com.leewan.rpc.share.message;

import lombok.Data;

@Data
public class ResponseMessage extends SequenceMessage {
    private int invokeId;
    private Object response;
    private String exceptionMessage;
    private String exceptionType;

    @Override
    public byte getType() {
        return TYPE_RESPONSE_MESSAGE;
    }
}
