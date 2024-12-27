package cn.jgzhan.lrpc.example.spring;

import cn.jgzhan.lrpc.example.client.Comsumer;
import cn.jgzhan.lrpc.example.common.annotation.LrpcReference;
import cn.jgzhan.lrpc.example.common.annotation.LrpcService;
import cn.jgzhan.lrpc.example.common.config.LrpcProperties;
import cn.jgzhan.lrpc.example.common.util.AddressUtils;
import cn.jgzhan.lrpc.example.common.util.FieldUtils;
import cn.jgzhan.lrpc.example.registry.ServiceManager;
import cn.jgzhan.lrpc.example.server.Provider;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import static cn.jgzhan.lrpc.example.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/18
 */
@Component
public class ReferenceBeanPostProcessor implements BeanPostProcessor, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ReferenceBeanPostProcessor.class);

    private final Provider provider;
    private final Comsumer comsumer;
    private final ServiceManager serviceManager;

    public ReferenceBeanPostProcessor(LrpcProperties lrpcProperties) {
        PROPERTIES_THREAD_LOCAL.set(lrpcProperties);
        this.serviceManager = new ServiceManager();
        // 服务端使用的远程服务缓存
        this.provider = new Provider(serviceManager.getServer());
        // 代理工厂
        this.comsumer = new Comsumer(serviceManager.getClient());
        // 启动
        serviceManager.start();
        provider.start();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(LrpcService.class)) {
            log.info("{} 类被注解为服务提供者, benaName: {}", bean.getClass().getName(), beanName);
            provider.registry(bean, provider.getPort());
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) throws BeansException {
        final var fields = bean.getClass().getDeclaredFields();
        for (var field : fields) {
            if (!field.isAnnotationPresent(LrpcReference.class)) {
                continue;
            }
            final var annotation = field.getAnnotation(LrpcReference.class);
            final var addressSet = AddressUtils.toAddressPair(annotation.addressArr());
            // 生成代理对象
            final Object proxy = comsumer.getProxy(field.getType(), addressSet);
            FieldUtils.setField(bean, field, proxy);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void destroy() {
        serviceManager.close();
    }
}
