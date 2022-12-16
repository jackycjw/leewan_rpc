package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.configuration.ClientConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leewan-rpc.client")
@Data
@Slf4j
public class BootClientProperties extends ClientConfiguration implements InitializingBean {
    private boolean enable;


    public BootClientProperties(){
        log.info("配置对象初始化");
    }

    @Override
    public void setRemoteAddress(String remoteAddress) {
        log.info("配置对象： 设置前 addr: {}", getRemoteAddress());
        super.setRemoteAddress(remoteAddress);
        log.info("配置对象： 设置后 addr: {}", getRemoteAddress());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("配置对象： addr:{}", this.getRemoteAddress());
    }
}
