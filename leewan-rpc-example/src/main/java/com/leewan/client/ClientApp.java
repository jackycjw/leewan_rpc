package com.leewan.client;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.DefaultClientContext;
import com.leewan.rpc.client.context.call.ExecuteCall;
import com.leewan.rpc.server.RpcServer;
import com.leewan.rpc.share.databind.jackson.JacksonRequestDataBinder;
import com.leewan.rpc.share.databind.kryo.KryoRequestDataBinder;
import com.leewan.share.IService;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp {

    static String string = "sfdjasdlf;sd;fjasdifhweedks;fsad12f1g524gs5df1v24v4cva4d4c2xc1v245asdf2sd121z2xc1v2zxc1v2z1v2ca12dfretsodk;LXPIlchenjiawen1172980";
    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setIdleHeartBeat(10000);
        configuration.setCompressionLevel(1);

        configuration.setRequestDataBinderClass(KryoRequestDataBinder.class);
        configuration.setMaxIdle(2);
        configuration.setMaxTotal(2);
        ClientContext clientContext = new DefaultClientContext(configuration);
        clientContext.initialize();
        IService service = clientContext.getService(IService.class);
        System.out.println("--------------------");
        service.say(string);
        System.out.println("--------------------");
        service.say(string);

    }
}
