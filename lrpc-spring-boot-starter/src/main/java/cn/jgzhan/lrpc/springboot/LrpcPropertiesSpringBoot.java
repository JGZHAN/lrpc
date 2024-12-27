package cn.jgzhan.spring;

import cn.jgzhan.lrpc.client.loadbalance.LoadBalancerType;
import cn.jgzhan.lrpc.common.serializer.SerializerEnum;
import cn.jgzhan.lrpc.registry.enums.RegistryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/19
 */
@EqualsAndHashCode(callSuper = true)
@Component
@ConfigurationProperties(prefix = "lrpcc")
@Getter
public class LrpcProperties extends cn.jgzhan.lrpc.common.config.LrpcProperties {

}
