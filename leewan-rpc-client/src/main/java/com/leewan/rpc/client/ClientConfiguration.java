package com.leewan.rpc.client;

import com.leewan.rpc.share.configuration.Configuration;
import lombok.Data;

@Data
public class ClientConfiguration extends Configuration {
    private String remoteAddress = "localhost";

    /**
     * 连接超时 毫秒
     */
    private int connectTimeout = 5000;

    private int maxConnections = 5;
}
