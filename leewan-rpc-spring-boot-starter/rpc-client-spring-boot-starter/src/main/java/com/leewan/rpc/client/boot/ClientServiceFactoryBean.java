package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class ClientServiceFactoryBean<T> implements FactoryBean<T> {

    private Class<T> serviceInterface;

    @Autowired
    private ApplicationContext context;

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setClientContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public T getObject() throws Exception {
        DefaultClientContext clientContext = context.getBean(DefaultClientContext.class);
        log.info("clientContext: {}", clientContext);
        return clientContext.getService(this.serviceInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return serviceInterface;
    }
}
