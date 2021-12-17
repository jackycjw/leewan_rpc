package com.leewan.server;

import lombok.Data;

/**
 * @author chenjw
 * @Date 2021/12/17 12:59
 */
@Data
public class RpcServerConfiguration {
    private String address = "0.0.0.0";
    private int port = 5540;


}
