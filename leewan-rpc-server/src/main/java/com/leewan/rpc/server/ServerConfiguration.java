package com.leewan.rpc.server;

import com.leewan.rpc.share.configuration.Configuration;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import com.leewan.rpc.share.databind.kryo.KryoResponseDataBinder;
import lombok.Data;

/**
 * @author chenjw
 * @Date 2021/12/17 12:59
 */
@Data
public class ServerConfiguration extends Configuration {
    private String bindAddress = "0.0.0.0";

    /**
     * 响应体序列化/反序列化方式
     */
    private Class<? extends ResponseDataBinder> responseDataBinderClass = KryoResponseDataBinder.class;
}
