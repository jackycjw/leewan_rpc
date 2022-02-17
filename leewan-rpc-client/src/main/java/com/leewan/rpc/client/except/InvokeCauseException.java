package com.leewan.rpc.client.except;

import lombok.Data;

@Data
public class InvokeCauseException extends RuntimeException {

    private String exceptionType;

    public InvokeCauseException(String exceptionType) {
        super("Server Exception Type : "+exceptionType);
    }
}
