package com.leewan.rpc.client.configuration;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Data
public class PoolConfiguration {
    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
    private int maxWait = -1;
    private long minEvictableIdleTimeMillis =  1000L * 60L * 30L;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = true;
    private int timeBetweenEvictionRunsMillis;
}
