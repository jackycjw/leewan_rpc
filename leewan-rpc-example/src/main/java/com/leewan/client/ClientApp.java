package com.leewan.client;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import com.leewan.rpc.client.context.call.ExecuteCall;
import com.leewan.rpc.server.RpcServer;
import com.leewan.share.IService;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp {

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxIdle(2);
        configuration.setMaxTotal(2);
        ClientContext clientContext = new DefaultClientContext(configuration);
        clientContext.initialize();
        IService service = clientContext.getService(IService.class);


        for (int i = 0; i < 10; i++) {


        }

        int num = service.s3(0, 1);
        System.out.println(num);
        System.out.println("--------------");
        num = service.s3(2, 4);
        System.out.println(num);

//        ExecuteCall.executeAsyn(()->{
//            service.s3(12, 13);
//        }, re -> {
//            System.out.println("异步: "+re);
//        });

    }
}
