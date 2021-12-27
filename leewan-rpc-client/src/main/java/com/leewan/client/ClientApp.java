package com.leewan.client;

import com.leewan.share.test.MathService;

public class ClientApp {

    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        client.configure(new ClientConfiguration());
        MathService service = client.getService(MathService.class);
        int plus = service.plus(34, 11);
        System.out.println(plus);

        service.say("你大爷的");


        service.sayNone();
    }
}
