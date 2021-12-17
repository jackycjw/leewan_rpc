package com.leewan.util;



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
        return true;
    }

}
