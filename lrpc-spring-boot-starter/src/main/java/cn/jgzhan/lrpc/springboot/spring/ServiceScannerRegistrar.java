package cn.jgzhan.lrpc.springboot.spring;

import cn.jgzhan.lrpc.springboot.annotation.LrpcScan;
import cn.jgzhan.lrpc.springboot.annotation.LrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/19
 */
public class ServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(ServiceScannerRegistrar.class);
    private static final String BASE_PACKAGE = "basePackages";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        final var basePackages = getBasePackages(importingClassMetadata);
        // Scan the LrpcService annotation
        final var scanner = new AnnotationScanner(registry, LrpcService.class);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        final var count = scanner.scan(basePackages);
        log.info("LrpcServiceScanner扫描的数量 [{}]", count);
    }

    /**
     * 获取扫描的包
     *  1. 优先使用注解上的basePackages属性
     *  2. 没有则使用当前类所在的包
     * @param metadata 注解元数据
     * @return 扫描的包
     */
    private static String[] getBasePackages(AnnotationMetadata metadata) {

        final var annotationAttributes = metadata.getAnnotationAttributes(LrpcScan.class.getName());

        String[] basePackages = null;
        if (annotationAttributes != null) {
            // 获取注解上的basePackages属性
            basePackages = (String[]) annotationAttributes.get(BASE_PACKAGE);
        }
        if (basePackages == null || basePackages.length == 0) {
            // 没有则使用当前类所在的包
            basePackages = new String[]{((StandardAnnotationMetadata) metadata).getIntrospectedClass().getPackage().getName()};
        }
        return basePackages;
    }
}