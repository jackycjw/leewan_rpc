package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.util.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(BootClientProperties.class)
@ConditionalOnProperty(prefix = "leewan-rpc.client", name = "enable", havingValue = "true")
public class RpcClientAutoConfiguration {

    public static final String BEAN_NAME_CLIENT_CONTEXT = "RpcClientContext";

    @Bean(BEAN_NAME_CLIENT_CONTEXT)
    public ClientContext clientContext(BootClientProperties clientProperties,
                                       ObjectProvider<List<Interceptor>> interceptorListProvider){
        DefaultClientContext clientContext = new DefaultClientContext(clientProperties);
        List<Interceptor> interceptorList = interceptorListProvider.getIfAvailable();
        if (!CollectionUtils.isEmpty(interceptorList)) {
            interceptorList.stream().forEach(clientContext::registerInterceptor);
        }
        return clientContext;
    }

}
