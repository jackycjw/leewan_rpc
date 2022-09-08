package com.leewan.rpc.share.util;

public class ObjectUtils {

    public static boolean isEmpty(Object[] array){
        return array==null || array.length == 0;
    }

    public static Object getDefaultBasicValue(Class clazz){
        if (byte.class.equals(clazz)) {
            return 0;
        } else if (short.class.equals(clazz)) {
            return 0;
        } else if (int.class.equals(clazz)) {
            return 0;
        } else if (long.class.equals(clazz)) {
            return 0;
        } else if (double.class.equals(clazz)) {
            return 0.0;
        } else if (boolean.class.equals(clazz)) {
            return false;
        } else if (float.class.equals(clazz)) {
            return 0.0;
        } else if (char.class.equals(clazz)) {
            return (char)0;
        }
        return null;
    }
}
