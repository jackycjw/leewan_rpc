package com.leewan.share.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseMessage extends Message {
    private int methodFlag;
    private Object response;
    private String exceptionMessage;
}
