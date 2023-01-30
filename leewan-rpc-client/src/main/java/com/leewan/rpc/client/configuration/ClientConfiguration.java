package com.leewan.rpc.client.configuration;

import com.leewan.rpc.share.configuration.Configuration;
import com.leewan.rpc.share.databind.RequestDataBinder;
import com.leewan.rpc.share.databind.kryo.KryoRequestDataBinder;
import io.netty.channel.Channel;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

@Data
public class ClientConfiguration extends Configuration {
    private String remoteAddress = "localhost";

    /**
     * 请求序列化/反序列化方式
     */
    private Class<? extends RequestDataBinder> requestDataBinderClass = KryoRequestDataBinder.class;

    /**
     * 连接超时 毫秒
     */
    private int connectTimeout = 5000;

    /**
     * 单个请求时长 毫秒
     */
    private int requestTimeout = 5000;

    /**
     * 重试次数
     */
    private int retry = 0;


    private int idleHeartBeat = 20;

    /**
     * thread num defaultValue 0 i.e cpu core processor
     */
    private int threadNum = 0;

    /**
     * RSA公钥
     */
    private String publicRsaKey;


    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
    private int maxWait = -1;
    private long minEvictableIdleTimeMillis =  1000L * 60L * 30L;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = true;
    private int timeBetweenEvictionRunsMillis;

    public GenericObjectPoolConfig<Channel> getPoolConfig(){
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWait(Duration.ofMillis(maxWait));
        config.setMinEvictableIdleTime(Duration.ofMillis(minEvictableIdleTimeMillis));
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnBorrow(testOnReturn);
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictionRunsMillis));
        return config;
    }

}
