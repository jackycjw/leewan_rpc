package com.leewan.rpc.client.call;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CallPerformance {
    private int requestTimeout;
    private int retry;

    public CallPerformance(int requestTimeout, int retry) {
        this.requestTimeout = requestTimeout;
        this.retry = retry;
    }
}
