package cn.jgzhan.lrpc.registry.enums;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/17
 */
public enum RegistryType {
    LOCAL,// 无注册中心，直连
    ZOOKEEPER,
    EUREKA,// todo
    NACOS,// todo
}
