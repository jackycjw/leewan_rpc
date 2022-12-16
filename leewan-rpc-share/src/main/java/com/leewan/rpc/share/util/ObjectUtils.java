package com.leewan.rpc.share.util;

import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.except.DataBinderCreateException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ObjectUtils {

    private static Map<String, Object> singletonMap = new HashMap();
    public static <T> T getSingleton(String className, Supplier<String> exceptionMsg){
        if (!singletonMap.containsKey(className)) {
            synchronized (singletonMap) {
                if (!singletonMap.containsKey(className)) {
                    try {
                        T object = (T) Class.forName(className).getConstructor().newInstance();
                        singletonMap.put(className, object);
                    } catch (Exception e) {
                        throw new DataBinderCreateException(exceptionMsg.get() + e.getMessage(), e);
                    }
                }
            }
        }
        return (T) singletonMap.get(className);
    }
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
