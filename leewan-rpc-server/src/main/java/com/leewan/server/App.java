package com.leewan.server;

import com.leewan.server.test.MathServiceImpl;

public class App {

    public static void main(String[] args) throws InterruptedException {
        ServerConfiguration configuration = new ServerConfiguration();
        RpcServer server = new RpcServer();
        server.configure(configuration);
        server.bind(new MathServiceImpl());
        server.start();
    }


    public void plus(int a, int b){

    }
}
