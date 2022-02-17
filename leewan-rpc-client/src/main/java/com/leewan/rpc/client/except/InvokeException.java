package com.leewan.rpc.client.except;

import lombok.Data;

@Data
public class InvokeException extends RuntimeException {

    private String exceptionType;

    public InvokeException(String message, String exceptionType) {
        super(message, new InvokeCauseException(exceptionType));
    }
}
