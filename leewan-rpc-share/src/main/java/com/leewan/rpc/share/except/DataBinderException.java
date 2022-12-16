package com.leewan.rpc.share.except;

public class DataBinderException extends RuntimeException{

    public DataBinderException(String message){
        super(message);
    }

    public DataBinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
