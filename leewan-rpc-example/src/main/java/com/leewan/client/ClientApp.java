package com.leewan.client;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import com.leewan.rpc.server.RpcServer;
import com.leewan.share.IService;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp {

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.getPoolConfiguration().setMaxIdle(2);
        configuration.getPoolConfiguration().setMaxTotal(2);
        ClientContext clientContext = new DefaultClientContext(configuration);
        IService service = clientContext.getService(IService.class);

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            threadPool.submit(()->{
               service.s1();
               service.s2(random.nextInt(100));
                System.out.println(service.s3(random.nextInt(100), random.nextInt(100)));
            });
        }

    }
}
