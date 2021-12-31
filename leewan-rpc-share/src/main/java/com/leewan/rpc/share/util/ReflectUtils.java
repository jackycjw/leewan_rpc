package com.leewan.rpc.share.util;



import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenjw
 * @Date 2021/12/17 16:50
 */
public class ReflectUtils {

    public static List<Class<?>> findInterfaces(Class<?> clazz){
        List<Class<?>> result = new ArrayList<>();
        Class<?>[] interfaces = clazz.getInterfaces();
        Arrays.stream(interfaces)
                .filter(ReflectUtils::isEffectiveInterface)
                .forEach(result::add);

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !Object.class.equals(superclass)) {
            result.addAll(findInterfaces(superclass));
        }

        Arrays.stream(interfaces)
                .map(inter -> findInterfaces(inter))
                .forEach(result::addAll);

        return result;
    }

    public static boolean isEffectiveInterface(Class<?> clazz){
        return clazz.getMethods().length > 0;
    }

    @SneakyThrows
    public static Class getClassByName(String name){
        switch (name){
            case "int":
                return int.class;
            case "short":
                return short.class;
            case "long":
                return long.class;
            case "byte":
                return byte.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                return Class.forName(name);
        }
    }
}
