package com.leewan.share.configuration;

import lombok.Data;

@Data
public class Configuration {
    /**
     * 消息体最大长度
     */
    private int maxMessageSize = 1024 * 1024 * 10;

    /**
     * 端口
     */
    private int port = 5540;
}
