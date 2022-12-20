package com.leewan.rpc.share.databind;

import com.leewan.rpc.share.util.Assert;
import lombok.SneakyThrows;

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import static java.lang.String.format;

public class DatabindService {

    /**
     * 获取 RequestDataBinder
     *
     * @param type
     * @return
     */
    public static RequestDataBinder getRequestDataBinder(byte type) {
        init();
        RequestDataBinder dataBinder = requestDataBinderMap.get(type);
        Assert.notNull(dataBinder, () -> format("类型[%d]找不到对应的RequestDataBinder", type));
        return dataBinder;
    }


    /**
     * 根据类名 获取 RequestDataBinder
     *
     * @param clazz
     * @return
     */
    @SneakyThrows
    public static RequestDataBinder getRequestDataBinder(Class<? extends RequestDataBinder> clazz) {
        init();
        RequestDataBinder dataBinder = requestDataBinderClassMap.get(clazz);
        Assert.notNull(dataBinder, () -> format("类型[%s]找不到对应的RequestDataBinder", clazz.getName()));
        return dataBinder;
    }

    /**
     * 获取 ResponseDataBinder
     *
     * @param type
     * @return
     */
    public static ResponseDataBinder getResponseDataBinder(byte type) {
        init();
        ResponseDataBinder dataBinder = responseDataBinderMap.get(type);
        Assert.notNull(dataBinder, () -> format("类型[%d]找不到对应的RequestDataBinder", type));
        return dataBinder;
    }

    /**
     * 根据类名 获取 ResponseDataBinder
     *
     * @param clazz
     * @return
     */
    @SneakyThrows
    public static ResponseDataBinder getResponseDataBinder(Class<? extends ResponseDataBinder> clazz) {
        init();
        ResponseDataBinder dataBinder = responseDataBinderClassMap.get(clazz);
        Assert.notNull(dataBinder, () -> format("类型[%s]找不到对应的RequestDataBinder", clazz.getName()));
        return dataBinder;
    }


    private static boolean inited = false;
    private static Map<Byte, RequestDataBinder> requestDataBinderMap = new HashMap<>();
    private static Map<Class<? extends RequestDataBinder>, RequestDataBinder> requestDataBinderClassMap = new HashMap<>();
    private static Map<Byte, ResponseDataBinder> responseDataBinderMap = new HashMap<>();
    private static Map<Class<? extends ResponseDataBinder>, ResponseDataBinder> responseDataBinderClassMap = new HashMap<>();

    private static void init() {
        if (!inited) {
            synchronized (DatabindService.class) {
                if (!inited) {
                    initRequestDataBinderMap();
                    initResponseDataBinderMap();
                    inited = true;
                }
            }
        }
    }

    private static void initRequestDataBinderMap() {
        ServiceLoader<RequestDataBinder> requestDataBinders = ServiceLoader.load(RequestDataBinder.class);
        requestDataBinders.stream()
                .map(Supplier::get)
                .forEach(dataBinder -> {
                    byte type = dataBinder.getType();
                    Assert.state(!requestDataBinderMap.containsKey(type), () -> "RequestDataBinder服务类型冲突: "
                            + dataBinder.getClass() + ", " + requestDataBinderMap.get(type).getClass());
                    requestDataBinderMap.put(type, dataBinder);
                    requestDataBinderClassMap.put(dataBinder.getClass(), dataBinder);
                });
    }

    private static void initResponseDataBinderMap() {
        ServiceLoader<ResponseDataBinder> requestDataBinders = ServiceLoader.load(ResponseDataBinder.class);
        requestDataBinders.stream()
                .map(Supplier::get)
                .forEach(dataBinder -> {
                    byte type = dataBinder.getType();
                    Assert.state(!responseDataBinderMap.containsKey(type), () -> "ResponseDataBinder服务类型冲突: "
                            + dataBinder.getClass() + ", " + responseDataBinderMap.get(type).getClass());
                    responseDataBinderMap.put(type, dataBinder);
                    responseDataBinderClassMap.put(dataBinder.getClass(), dataBinder);
                });
    }

}
