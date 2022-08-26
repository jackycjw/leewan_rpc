package com.leewan.rpc.server.boot;

import com.leewan.rpc.server.ServerConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leewan-rpc.server")
@Data
public class BootServerProperties extends ServerConfiguration {
    private boolean enable;
}
