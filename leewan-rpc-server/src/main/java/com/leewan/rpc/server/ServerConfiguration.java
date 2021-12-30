package com.leewan.rpc.server;

import com.leewan.rpc.share.configuration.Configuration;
import lombok.Data;

/**
 * @author chenjw
 * @Date 2021/12/17 12:59
 */
@Data
public class ServerConfiguration extends Configuration {
    private String bindAddress = "0.0.0.0";
}
