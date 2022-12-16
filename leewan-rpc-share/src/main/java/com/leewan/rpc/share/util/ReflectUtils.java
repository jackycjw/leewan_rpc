package com.leewan.rpc.share.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.leewan.rpc.share.message.InvokeMeta;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenjw
 * @Date 2021/12/17 16:50
 */
public class ReflectUtils {

    public static List<Class<?>> findInterfaces(Class<?> clazz) {
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

    @SneakyThrows
    public static Method resolveMethod(InvokeMeta meta) {
        String clazzName = meta.getClazzName();
        Class<?> interClazz = Class.forName(clazzName);
        List<String> parameterTypeNames = meta.getParameterTypeNames();
        Class[] classes = new Class[parameterTypeNames.size()];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = getClassByName(parameterTypeNames.get(i));
        }
        return interClazz.getDeclaredMethod(meta.getMethodName(), classes);
    }

    @SneakyThrows
    public static TypeReference getTypeReference(Type type) {
        TypeReference reference = new TypeReference<>() {
        };
        Field field = TypeReference.class.getDeclaredField("_type");
        field.setAccessible(true);
        field.set(reference, type);
        return reference;
    }

    public static boolean isEffectiveInterface(Class<?> clazz) {
        return clazz.getMethods().length > 0;
    }

    @SneakyThrows
    public static Class getClassByName(String name) {
        switch (name) {
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
