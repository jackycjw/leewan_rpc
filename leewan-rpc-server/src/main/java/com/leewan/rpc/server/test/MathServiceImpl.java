package com.leewan.rpc.server.test;

import com.leewan.rpc.share.test.MathService;

public class MathServiceImpl implements MathService {
    @Override
    public int plus(int a, int b) {
        return a+b;
    }

    @Override
    public void say(String msg) {
        System.out.println("say方法调用了: " + msg);
    }

    @Override
    public void sayNone() {
        System.out.println("啥也不说方法调用了");
    }
}
