package com.leewan.rpc.share.except;

public class IllegalTypeException extends RuntimeException {

    public IllegalTypeException(String message) {
        super(message);
    }

    public IllegalTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
