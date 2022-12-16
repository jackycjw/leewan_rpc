package com.leewan.rpc.client.boot;

import com.leewan.rpc.client.context.ClientContext;
import com.leewan.rpc.client.context.InvokeMetaResolver;
import com.leewan.rpc.client.context.RequestResponseContainer;
import com.leewan.rpc.client.intercept.Interceptor;
import com.leewan.rpc.share.internal.service.HeartBeatService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
import static org.springframework.context.annotation.ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR;

@Slf4j
public class RpcClientServiceScanner implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {

    @Getter
    @Setter
    private String[] packages;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("package size is {}", packages.length);
        if (packages != null && packages.length > 0) {
            Arrays.stream(packages).forEach(basePackage -> {
                log.info("package is {}", basePackage);
                scan(basePackage, registry);
            });
        }
    }

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    Set<BeanDefinition> scan(String basePackage, BeanDefinitionRegistry registry){
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                log.info("RPC 扫描资源 {}", resource);
                try {
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    if (metadataReader.getClassMetadata().isInterface() && !metadataReader.getClassMetadata().isAnnotation()) {
                        Class<?> serviceInterface = Class.forName(metadataReader.getClassMetadata().getClassName());
                        if (isCandidate(serviceInterface)) {
                            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                                    .genericBeanDefinition(ClientServiceFactoryBean.class)
                                    .addPropertyValue("serviceInterface", serviceInterface)
                                    .getBeanDefinition();
                            beanDefinition.setAutowireMode(AUTOWIRE_BY_TYPE);
                            String beanName = IMPORT_BEAN_NAME_GENERATOR.generateBeanName(beanDefinition, registry);
                            registry.registerBeanDefinition(beanName + "$" + serviceInterface.getName(), beanDefinition);
                        }
                    }

                }
                catch (FileNotFoundException ex) {
                    log.trace("Ignored non-readable " + resource + ": " + ex.getMessage());
                }
                catch (Throwable ex) {
                    throw new BeanDefinitionStoreException(
                            "Failed to read candidate component class: " + resource, ex);
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return candidates;
    }

    private static boolean isCandidate(Class<?> clazz){
        if (clazz.isInterface()
                && !clazz.isAnnotation()
                && clazz.getMethods().length > 0
                && !isExcluded(clazz)) {
            return true;
        }
        return false;
    }

    private static boolean isExcluded(Class<?> clazz){
        if (Aware.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (HeartBeatService.class.equals(clazz)) {
            return true;
        }
        if (ClientContext.class.isAssignableFrom(clazz)
                || InvokeMetaResolver.class.isAssignableFrom(clazz)
                || RequestResponseContainer.class.isAssignableFrom(clazz)
                || Interceptor.class.isAssignableFrom(clazz)) {
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(isExcluded(ResourceLoaderAware.class));
    }
    @Nullable
    private MetadataReaderFactory metadataReaderFactory;

    private ResourcePatternResolver resourcePatternResolver;

    private Environment environment;

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //do nothing
    }
}
