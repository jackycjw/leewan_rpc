package com.leewan.rpc.client.context.call;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用性能参数
 */
@Data
@NoArgsConstructor
public class CallPerformance {
    /**
     * 超时时长 毫秒
     */
    private int requestTimeout;

    /**
     * 重试次数
     */
    private int retry;

    public CallPerformance(int requestTimeout, int retry) {
        this.requestTimeout = requestTimeout;
        this.retry = retry;
    }
}
