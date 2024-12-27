package cn.jgzhan.lrpc.example.common.config;


import cn.jgzhan.lrpc.example.client.loadbalance.LoadBalancerType;
import cn.jgzhan.lrpc.example.common.serializer.SerializerEnum;
import cn.jgzhan.lrpc.example.registry.enums.RegistryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/3
 */
@Getter
@Setter
public class LrpcProperties {

    private Protocol protocol;

    private Registry registry;

    private Server server;

    private Client client;


    public LrpcProperties() {

    }
    public LrpcProperties(String propertiesFileName) {
        try (InputStream resourceAsStream = LrpcProperties.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            this.protocol = new Protocol(properties);
            this.registry = new Registry(properties);
            this.server = new Server(properties);
            this.client = new Client(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // 获取protocol信息
    @Setter
    @NoArgsConstructor
    public static class Protocol {
        private Properties properties;

        private SerializerEnum serializer;

        public Protocol(Properties properties) {
            this.properties = properties;
        }

        // 获取序列化算法（默认为JSON）
        public SerializerEnum getSerializer() {
            final String serializerEnumName = properties.getProperty("lrpc.protocol.serializer", "JSON");
            return SerializerEnum.valueOf(serializerEnumName);
        }
    }

    //注册中心
    @Setter
    @NoArgsConstructor
    public static class Registry {
        private Properties properties;

        private RegistryType type;

        @Getter
        private Zookeeper zookeeper;

        public Registry(Properties properties) {
            this.properties = properties;
        }

        public RegistryType getType() {
            return RegistryType.valueOf(properties.getProperty("lrpc.registry.type", "LOCAL"));
        }

        /**
         * 获取zk注册中心的地址
         */
        @Setter
        @NoArgsConstructor
        public static class Zookeeper {
            private Properties properties;

            private String address;

            private String account;

            private byte[] passWord;

            private String rootPath;

            public Zookeeper(Properties properties) {
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

    /**
     * 获取server信息
     */
    @Setter
    @NoArgsConstructor
    public class Server {

        private Properties properties;

        private int port;

        private int workerMax;

        public Server(Properties properties) {
            this.properties = properties;
        }

        public int getPort() {
            return Integer.parseInt(properties.getProperty("lrpc.server.port", "23923"));
        }

        public int getWorkerMax() {
            return Integer.parseInt(properties.getProperty("lrpc.server.workerMax", "1000"));
        }

    }

    /**
     * 获取client信息
     */
    @Setter
    @NoArgsConstructor
    public static class Client {
        private Properties properties;

        private int addressMaxConnection;

        private LoadBalancerType loadBalance;

        public Client(Properties properties) {
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
