package com.leewan.rpc.share.configuration;

import com.leewan.rpc.share.databind.jackson.JacksonRequestDataBinder;
import com.leewan.rpc.share.databind.jackson.JacksonResponseDataBinder;
import com.leewan.rpc.share.databind.kryo.KryoRequestDataBinder;
import com.leewan.rpc.share.databind.kryo.KryoResponseDataBinder;
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

    /**
     * 请求序列化/反序列化方式
     */
    private String requestDataBinderClass = KryoRequestDataBinder.class.getName();

    /**
     * 响应体序列化/反序列化方式
     */
    private String responseDataBinderClass = KryoResponseDataBinder.class.getName();
}
