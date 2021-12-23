package com.leewan.client;

import com.leewan.share.configuration.Configuration;
import lombok.Data;

@Data
public class RpcClientConfiguration extends Configuration {
    private String remoteAddress = "localhost";
}
