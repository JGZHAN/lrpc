package cn.jgzhan.lrpc.springboot.properties;

import cn.jgzhan.lrpc.client.loadbalance.LoadBalancerType;
import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.serializer.SerializerEnum;
import cn.jgzhan.lrpc.registry.enums.RegistryType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/19
 */
@Component
@ConfigurationProperties(prefix = "lrpc")
public class LrpcPropertiesSpringBoot extends LrpcProperties {
    // 设置默认值，通过配置文件覆盖
    public LrpcPropertiesSpringBoot() {
        final var protocol = new Protocol(SerializerEnum.JSON);
        this.setProtocol(protocol);

        final var registry = new Registry();
        registry.setType(RegistryType.LOCAL);
        this.setRegistry(registry);

        final var client = new Client();
        client.setLoadBalance(LoadBalancerType.ROUND_ROBIN);
        client.setAddressMaxConnection(1000);
        this.setClient(client);

        final var server = new Server();
        server.setPort(23923);
        server.setWorkerMax(1000);
        this.setServer(server);
    }
}
