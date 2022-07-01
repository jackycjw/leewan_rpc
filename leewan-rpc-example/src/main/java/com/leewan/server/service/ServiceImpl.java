package com.leewan.server.service;

import com.leewan.share.IService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceImpl implements IService {
    @Override
    public void s1() {
        log.info("s1 调用");
    }

    @Override
    public void s2(int n) {
        log.info("s2 调用: {}", n);
    }

    @Override
    public int s3(int a, int b) {
        log.info("s3 调用: {},{}", a,b);
        return a+b;
    }
}
