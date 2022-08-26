package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.context.ClientContext;
import org.springframework.beans.factory.FactoryBean;

public class ClientServiceFactoryBean<T> implements FactoryBean<T> {

    private Class<T> serviceInterface;

    private ClientContext clientContext;

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public T getObject() throws Exception {
        return clientContext.getService(this.serviceInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return serviceInterface;
    }
}
