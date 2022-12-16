package com.leewan.rpc.share.except;

public class DataBinderCreateException extends RuntimeException{

    public DataBinderCreateException(String message){
        super(message);
    }

    public DataBinderCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
