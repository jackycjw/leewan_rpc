package com.leewan.server;

import com.leewan.ref.Invoke;
import com.leewan.ref.InvokeUtils;
import com.leewan.server.except.BindServiceException;
import com.leewan.share.message.InvokeMeta;
import com.leewan.share.util.Assert;
import com.leewan.share.util.ReflectUtils;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.leewan.share.util.ReflectUtils.getClassByName;

public class ServiceContainer {
    /**
     * 接口
     */
    private Map<Class<?>, Object> services = new HashMap<>();

    private Map<InvokeMeta, Integer> invokeMetaIdMap = new HashMap<>();

    private Map<Integer, Invoke> invokeIdMap = new HashMap<>();

    private AtomicInteger sequence = new AtomicInteger();



    public void bind(Object service){
        List<Class<?>> interfaces = ReflectUtils.findInterfaces(service.getClass());
        bind(service, interfaces);
    }

    public void bind(Object service, Class<?>...interfaces){
        this.bind(service, Arrays.stream(interfaces).collect(Collectors.toList()));
    }

    public void bind(Object service, List<Class<?>> interfaces){
        Assert.notNull(service, "绑定的服务不能为空");
        interfaces.stream().forEach(inter -> {
            Assert.isAssignable(inter, service.getClass());
            if (services.containsKey(inter)) {
                Object bindedService = services.get(inter);
                if (bindedService.getClass().equals(service.getClass())) {
                    throw new BindServiceException(
                            bindedService.getClass().getName() + "服务已存在，无需重新绑定");
                } else {
                    throw new BindServiceException(
                            "【"+ bindedService.getClass().getName() + "】与【"
                                    + service.getClass().getName() + "】,实现了共同的接口【" + inter.getName() + "】");
                }
            } else {
                services.put(inter, service);
            }
        });
    }

    @SneakyThrows
    public Invoke getInvoke(InvokeMeta methodMeta){
        int invokeId = methodMeta.getInvokeId();
        if (invokeId > -1) {
            Invoke invoke = invokeIdMap.get(invokeId);
            Assert.notNull(invoke, "invokeId:【" + invokeId + "】 无效");
            return invoke;
        } else {
            String clazzName = methodMeta.getClazzName();
            Class<?> interClazz = Class.forName(clazzName);
            synchronized (interClazz) {
                Integer id = invokeMetaIdMap.get(methodMeta);
                if (id == null) {
                    Object service = services.get(interClazz);
                    Invoke invoke = createInvoke(service, methodMeta);
                    id = sequence.incrementAndGet();
                    invokeMetaIdMap.put(methodMeta, id);
                    invokeIdMap.put(id, invoke);
                }
                Invoke invoke = invokeIdMap.get(id);
                methodMeta.setInvokeId(id);
                return invoke;
            }
        }
    }

    @SneakyThrows
    private Invoke createInvoke(Object service, InvokeMeta methodMeta){
        List<String> parameterTypeNames = methodMeta.getParameterTypeNames();
        Class[] classes = new Class[parameterTypeNames.size()];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = getClassByName(parameterTypeNames.get(i));
        }
        return InvokeUtils.get(service, methodMeta.getMethodName(), classes);
    }


}
