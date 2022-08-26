package com.leewan.rpc.server.boot;

import com.leewan.rpc.server.RpcServer;
import com.leewan.rpc.server.filter.Filter;
import com.leewan.rpc.share.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Factory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BootServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private BootServerProperties properties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        RpcServer server = new RpcServer();
        server.configure(properties);

        AbstractApplicationContext context = (AbstractApplicationContext) event.getApplicationContext();

        String[] servicesNames = context.getBeanNamesForAnnotation(RpcService.class);
        Arrays.stream(servicesNames).forEach(name->{
            Object service = context.getBean(name);
            RpcService rpcService = context.findAnnotationOnBean(name, RpcService.class);
            Class<?>[] value = rpcService.value();

            if (ObjectUtils.isEmpty(value)) {
                List<Class<?>> interfaces = ReflectUtils.findInterfaces(service.getClass());
                //过滤掉一些特殊的接口
                interfaces = interfaces.stream().filter(this::isCandidate).collect(Collectors.toList());
                Assert.notEmpty(interfaces, "服务【"+service.getClass()+"】未实现任何有效接口");
                server.bind(service, interfaces);
            } else {
                Arrays.stream(value).forEach(aClass -> {
                    Assert.state(aClass.isAssignableFrom(service.getClass()),
                            "服务【"+service.getClass()+"】没有实现【"+aClass.getName()+"】接口");

                    Assert.state(aClass.isInterface() && !aClass.isAnnotation(),
                            "服务【"+service.getClass()+"】的注解值只能是接口");
                });
                server.bind(service, value);
            }
        });

        List<Filter> filters = getFilters(context);
        filters.forEach(filter -> {
            server.addFilter(filter);
        });

        log.info("RPC绑定 {}个服务, {}个Filter",servicesNames.length, filters.size());

        server.start();
        log.info("RPC启动成功,端口{}", properties.getPort());
    }

    private boolean isCandidate(Class<?> clazz){
        if (TargetClassAware.class.isAssignableFrom(clazz)
        || Aware.class.isAssignableFrom(clazz)
        || Factory.class.isAssignableFrom(clazz)) {
            return false;
        }

        return true;
    }

    private List<Filter> getFilters(ApplicationContext context){
        String[] names = context.getBeanNamesForType(Filter.class);
        return Arrays.stream(names).map(name -> context.getBean(name)).map(o -> (Filter)o).collect(Collectors.toList());
    }
}
