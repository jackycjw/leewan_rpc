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

    /**
     * 单个请求时长 毫秒
     */
    private int requestTimeout = 5000;

    /**
     * 重试次数
     */
    private int retry = 0;


    private int maxConnections = 5;

    private int idleHeartBeat = 20;
}
