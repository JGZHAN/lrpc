package cn.jgzhan.lrpc.common.config;


import cn.jgzhan.lrpc.client.loadbalance.LoadBalancerType;
import cn.jgzhan.lrpc.common.serializer.SerializerEnum;
import cn.jgzhan.lrpc.registry.enums.RegistryType;
import lombok.*;

import java.io.InputStream;
import java.util.Properties;

@Data
public class LrpcProperties {

    private Protocol protocol;

    private Registry registry;

    private Server server;

    private Client client;


    // 获取protocol信息
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Protocol {

        private SerializerEnum serializer;

    }

    //注册中心
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Registry {

        private RegistryType type;

        private Zookeeper zookeeper;

        @Data
        public static class Zookeeper {

            private String address;

            private String account;

            private byte[] passWord;

            private String rootPath;

        }
    }

    @Data
    public static class Server {

        private int port;

        private int workerMax;

    }

    @Data
    public static class Client {

        private int addressMaxConnection;

        private LoadBalancerType loadBalance;

    }
}
