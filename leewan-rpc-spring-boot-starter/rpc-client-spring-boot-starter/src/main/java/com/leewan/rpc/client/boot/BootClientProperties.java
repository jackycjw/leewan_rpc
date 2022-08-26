package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leewan-rpc.client")
@Data
public class BootClientProperties extends ClientConfiguration {
    private boolean enable;
}
