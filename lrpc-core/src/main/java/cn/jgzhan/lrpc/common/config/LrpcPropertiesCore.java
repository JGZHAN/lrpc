package cn.jgzhan.lrpc.common.config;

import cn.jgzhan.lrpc.client.loadbalance.LoadBalancerType;
import cn.jgzhan.lrpc.common.serializer.SerializerEnum;
import cn.jgzhan.lrpc.registry.enums.RegistryType;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/26
 */
public class LrpcPropertiesCore extends LrpcProperties {

    private static final String DEFAULT_PROPERTIES_FILE_NAME = "application.properties";

    // 午餐构造函数，默认配置文件名为application.properties
    public LrpcPropertiesCore() {
        this(DEFAULT_PROPERTIES_FILE_NAME);
    }

    public LrpcPropertiesCore(String propertiesFileName) {
        super();
        try (InputStream resourceAsStream = LrpcProperties.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            this.setProtocol(new ProtocolCore(properties));
            this.setRegistry(new RegistryCore(properties));
            this.setServer(new ServerCore(properties));
            this.setClient(new ClientCore(properties));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ProtocolCore extends Protocol {
        private final Properties properties;

        public ProtocolCore(Properties properties) {
            this.properties = properties;
        }

        // 获取序列化算法（默认为JSON）
        public SerializerEnum getSerializer() {
            final String serializerEnumName = properties.getProperty("lrpc.protocol.serializer", "JSON");
            return SerializerEnum.valueOf(serializerEnumName);
        }
    }

    public static class RegistryCore extends Registry {
        private final Properties properties;

        public RegistryCore(Properties properties) {
            super();
            this.properties = properties;
            this.setZookeeper(new ZookeeperCore(properties));
        }

        public RegistryType getType() {
            return RegistryType.valueOf(properties.getProperty("lrpc.registry.type", "LOCAL"));
        }

        public static class ZookeeperCore extends Zookeeper {
            private final Properties properties;

            public ZookeeperCore(Properties properties) {
                this.properties = properties;
            }

            public String getAddress() {
                return properties.getProperty("lrpc.registry.zookeeper.address");
            }

            public String getAccount() {
                return properties.getProperty("lrpc.registry.zookeeper.account");
            }

            public byte[] getPassWord() {
                var pw = properties.getProperty("lrpc.registry.zookeeper.password");
                return pw == null ? null : pw.getBytes();
            }

            public String getRootPath() {
                return properties.getProperty("registry.zookeeper.path", "lrpc");
            }

        }
    }

    public static class ServerCore extends Server {

        private final Properties properties;

        public ServerCore(Properties properties) {
            this.properties = properties;
        }

        public int getPort() {
            return Integer.parseInt(properties.getProperty("lrpc.server.port", "23923"));
        }

        public int getWorkerMax() {
            return Integer.parseInt(properties.getProperty("lrpc.server.workerMax", "1000"));
        }

    }

    public static class ClientCore extends Client {
        private final Properties properties;

        public ClientCore(Properties properties) {
            this.properties = properties;
        }

        public int getAddressMaxConnection() {
            return Integer.parseInt(properties.getProperty("lrpc.client.addressMaxConnection", "1000"));
        }

        // 获取负载均衡算法，默认为轮询
        public LoadBalancerType getLoadBalance() {
            return LoadBalancerType.valueOf(properties.getProperty("lrpc.client.loadbalance.type", "ROUND_ROBIN"));
        }
    }

}
