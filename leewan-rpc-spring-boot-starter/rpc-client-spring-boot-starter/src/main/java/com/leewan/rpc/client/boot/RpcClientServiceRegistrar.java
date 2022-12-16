package com.leewan.rpc.client.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

//
@Slf4j
public class RpcClientServiceRegistrar implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(ServiceScan.class.getName());
        String[] packages = (String[]) attributes.get("value");

        if (packages == null || packages.length == 0) {
            MergedAnnotation<ServiceScan> annotation = importingClassMetadata.getAnnotations().get(ServiceScan.class);
            Class<?> source = (Class)annotation.getSource();
            if (source != null) {
                packages = new String[]{source.getPackageName()};
            }
        }

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(RpcClientServiceScanner.class)
                .addPropertyValue("packages", packages)
                .getBeanDefinition();
        registry.registerBeanDefinition(RpcClientServiceScanner.class.getName(), beanDefinition);


    }



}
