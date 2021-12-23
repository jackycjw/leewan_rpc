package com.leewan.server;

import com.leewan.share.configuration.Configuration;
import lombok.Builder;
import lombok.Data;

/**
 * @author chenjw
 * @Date 2021/12/17 12:59
 */
@Data
public class RpcServerConfiguration extends Configuration {
    private String bindAddress = "0.0.0.0";
}
