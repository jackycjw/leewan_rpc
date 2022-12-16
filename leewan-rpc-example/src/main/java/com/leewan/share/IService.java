package com.leewan.share;

import com.leewan.rpc.share.call.Call;

public interface IService {

    void s1();
    void s2(int n);

    @Call(requestTimeout = 100000000)
    int s3(int a, int b);



}
