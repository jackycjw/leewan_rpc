package com.leewan.server;


import com.leewan.util.ReflectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjw
 * @Date 2021/12/17 12:56
 */
public class RpcServer {

    private RpcServerConfiguration configuration;

    public void configure(RpcServerConfiguration configuration){
        this.configuration = configuration;
    }

    private Map<Class<?>, Object> services = new HashMap<>();

    /**
     * 绑定服务
     * @param service
     */
    public void bind(Object service){
        List<Class<?>> interfaces = ReflectUtils.findInterfaces(service.getClass());
        bind(service, interfaces);
    }

    public void bind(Object service, Class<?>...interfaces){
        this.bind(service, Arrays.stream(interfaces).collect(Collectors.toList()));
    }

    public void bind(Object service, List<Class<?>> interfaces){
    }


    public void start(){

    }

}
