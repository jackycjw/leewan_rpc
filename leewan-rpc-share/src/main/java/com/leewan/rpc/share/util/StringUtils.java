package com.leewan.rpc.share.util;

public class StringUtils {

    public static boolean hasText(String text){
        return text != null && text.length()>0 ;
    }

    public static boolean hasLength(String text){
        return hasText(text);
    }

}
