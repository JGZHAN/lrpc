package cn.jgzhan.lrpc.example.registry;

import cn.jgzhan.lrpc.example.registry.impl.LocalRegistryCenter;
import cn.jgzhan.lrpc.example.registry.impl.ZookeeperRegistryCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.jgzhan.lrpc.example.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/17
 */
public class RegistryFactory {
    private static final Logger log = LoggerFactory.getLogger(RegistryFactory.class);

    private static RegistryCenter REGISTRY_CENTER;

    public static RegistryCenter getRegistry() {
        if (REGISTRY_CENTER != null) {
            return REGISTRY_CENTER;
        }
        final var registry = PROPERTIES_THREAD_LOCAL.get().getRegistry();
        synchronized (RegistryFactory.class) {
            if (REGISTRY_CENTER == null) {
                final var registryType = registry.getType();
                switch (registryType) {
                    case ZOOKEEPER -> REGISTRY_CENTER = new ZookeeperRegistryCenter(){{log.info("使用Zookeeper注册中心");}};
                    case LOCAL -> REGISTRY_CENTER = new LocalRegistryCenter(){{log.info("使用本地注册中心, 直连模式");}};
                    default -> throw new RuntimeException("不支持的注册中心类型");
                }
            }
        }
        return REGISTRY_CENTER;
    }
}
