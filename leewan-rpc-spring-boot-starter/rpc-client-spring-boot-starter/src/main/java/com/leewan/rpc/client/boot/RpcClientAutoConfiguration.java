package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.util.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.List;

@AutoConfiguration
@ImportRuntimeHints(HintRegister.class)
@EnableConfigurationProperties(BootClientProperties.class)
@ConditionalOnProperty(prefix = "leewan-rpc.client", name = "enable", havingValue = "true")
@Slf4j
public class RpcClientAutoConfiguration implements InitializingBean {

    public static final String BEAN_NAME_CLIENT_CONTEXT = "RpcClientContext";

    @Autowired
    private BootClientProperties clientProperties;
    @Bean
    public DefaultClientContext clientContext(
                                       ObjectProvider<List<Interceptor>> interceptorListProvider){
        log.info("创建 clientContext  remoteAddress: {}", clientProperties.getRemoteAddress());
        DefaultClientContext clientContext = new DefaultClientContext(clientProperties);
        clientContext.initialize();
        List<Interceptor> interceptorList = interceptorListProvider.getIfAvailable();

        log.info("创建 拦截器  remoteAddress: {}", interceptorList.size());
        if (!CollectionUtils.isEmpty(interceptorList)) {
            interceptorList.stream().forEach(clientContext::registerInterceptor);
        }
        return clientContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("clientProperties: " + clientProperties);
        log.info("clientProperties:  addr {}" , clientProperties.getRemoteAddress());
    }
}
