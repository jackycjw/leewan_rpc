package com.leewan.server;

import com.leewan.rpc.server.RpcServer;
import com.leewan.rpc.server.ServerConfiguration;
import com.leewan.server.service.ServiceImpl;

public class ServerApp {

    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        server.configure(new ServerConfiguration());
        server.bind(new ServiceImpl());
        server.start();
    }
}
