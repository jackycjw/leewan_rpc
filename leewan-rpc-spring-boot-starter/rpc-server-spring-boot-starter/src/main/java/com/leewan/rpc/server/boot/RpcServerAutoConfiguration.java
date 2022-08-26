package com.leewan.rpc.server.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(BootServerProperties.class)
@ConditionalOnProperty(prefix = "leewan-rpc.server", name="enable", havingValue = "true")
@Import(BootServerStarter.class)
public class RpcServerAutoConfiguration {

}
