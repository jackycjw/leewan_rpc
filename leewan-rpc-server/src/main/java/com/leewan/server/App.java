package com.leewan.server;

public class App {

    public static void main(String[] args) throws InterruptedException {
        RpcServerConfiguration configuration = new RpcServerConfiguration();
        RpcServer server = new RpcServer();
        server.configure(configuration);
        server.start();
    }
}
