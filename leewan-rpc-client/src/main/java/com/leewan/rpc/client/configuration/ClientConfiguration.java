package com.leewan.rpc.client.configuration;

import com.leewan.rpc.share.configuration.Configuration;
import io.netty.channel.Channel;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

@Data()
public class ClientConfiguration extends Configuration {
    private String remoteAddress = "localhost";

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

    private PoolConfiguration poolConfiguration = new PoolConfiguration();

    public GenericObjectPoolConfig<Channel> getPoolConfig(){
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(poolConfiguration.getMaxTotal());
        config.setMaxIdle(poolConfiguration.getMaxIdle());
        config.setMinIdle(poolConfiguration.getMinIdle());
        config.setMaxWait(Duration.ofMillis(poolConfiguration.getMaxWait()));
        config.setMinEvictableIdleTime(Duration.ofMillis(poolConfiguration.getMinEvictableIdleTimeMillis()));
        config.setTestOnBorrow(poolConfiguration.isTestOnBorrow());
        config.setTestOnBorrow(poolConfiguration.isTestOnReturn());
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(poolConfiguration.getTimeBetweenEvictionRunsMillis()));
        return config;
    }

}
