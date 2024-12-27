package cn.jgzhan.lrpc.registry;

import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.dto.ProviderInfo;
import cn.jgzhan.lrpc.registry.enums.Change;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/23
 */
@Getter
public class ServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private final Client client;

    private final Server server;


    private static RegistryCenter registryCenter;

    public ServiceManager() {
        registryCenter = RegistryFactory.getRegistry();
        this.client = new Client();
        this.server = new Server();
    }

    public void start() {
        registryCenter.start();
        client.watch();
    }

    @SneakyThrows
    public void close() {
        registryCenter.close();
        client.close();
    }


    public static class Client {

        // 本地缓存连接池
        private static final Map<String, FixedChannelPool> ADDRESS_POOL_MAP = new ConcurrentHashMap<>();
        // 本地缓存注册中心的服务提供者
        private static final Map<String, Set<Pair<String, Integer>>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();


        // 监听注册中心的变化
        public void watch() {

            // 观察者模式，监听注册中心的变化
            registryCenter.watch((change, providerInfo) -> {
                switch (change) {
                    case Change.ADD -> addServiceAddress(providerInfo);
                    case Change.UPDATE -> updateServiceAddress(providerInfo);
                    case Change.REMOVE -> deleteServiceAddress(providerInfo);
                }
            });
        }

        // 关闭
        public void close() {
            try {
                ADDRESS_POOL_MAP.values().forEach(ChannelPool::close);
                log.info("关闭消费者连接池成功");
            } catch (Exception e) {
                log.error("关闭消费者连接池失败", e);
                throw new RuntimeException(e);
            }
        }

        // 将服务地址放入缓存
        public Set<Pair<String, Integer>> putToAddressMap(Method method) {
            return SERVICE_ADDRESS_MAP.computeIfAbsent(method.toString(),
                    _ -> new CopyOnWriteArraySet<>(registryCenter.getService(method)));
        }

        // 获取服务地址
        public Set<Pair<String, Integer>> getServiceAddress(Method method) {
            return putToAddressMap(method);
        }

        public FixedChannelPool getChannelPool(Pair<String, Integer> address, Function<String, FixedChannelPool> mappingFunction) {
            final var host = address.left;
            final var port = address.right;
            return ADDRESS_POOL_MAP.computeIfAbsent(host + ":" + port, mappingFunction);
        }

        private void deleteServiceAddress(ProviderInfo providerInfo) {
            log.info("删除服务提供者: {}", providerInfo);
            final var serviceName = providerInfo.getServiceName();
            final var address = providerInfo.getAddress();
            final Set<Pair<String, Integer>> addresses = SERVICE_ADDRESS_MAP.get(serviceName);
            if (addresses != null) {
                addresses.remove(address);
            }
            FixedChannelPool pool = ADDRESS_POOL_MAP.remove(address.left + address.right);
            if (pool != null) {
                pool.close();
            }
        }

        private void updateServiceAddress(ProviderInfo providerInfo) {
            log.info("更新服务提供者: {}", providerInfo);
            addOrUpdateServiceAddress(providerInfo.getServiceName(), providerInfo.getAddress());
        }

        private void addServiceAddress(ProviderInfo providerInfo) {
            log.info("新增服务提供者: {}", providerInfo);
            addOrUpdateServiceAddress(providerInfo.getServiceName(), providerInfo.getAddress());
        }

        private void addOrUpdateServiceAddress(String methodStr, Pair<String, Integer> address) {
            SERVICE_ADDRESS_MAP.computeIfAbsent(methodStr, _ -> new CopyOnWriteArraySet<>())
                    .add(address);
        }

    }

    public static class Server {
        private static final Map<Class<?>, Object> INTERFACE_PROVIDER_MAP = new ConcurrentHashMap<>();

        public <T> void registry(T instance, int port) {
            final Class<?> interfaceClz = getInterface(instance);
            registryCenter.registerService(interfaceClz, port);
            INTERFACE_PROVIDER_MAP.put(interfaceClz, instance);
        }

        public Object getService(String interfaceName) {
            Class<?> serviceClass;
            try {
                serviceClass = Class.forName(interfaceName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("接口名错误");
            }

            return serviceClass.cast(INTERFACE_PROVIDER_MAP.get(serviceClass));
        }

        @SuppressWarnings("unchecked")
        private static <T> Class<? extends T> getInterface(T instance) {
            final var interfaces = instance.getClass().getInterfaces();
            if (interfaces.length == 0) {
                throw new RuntimeException("没有实现接口");
            }
            return (Class<? extends T>) interfaces[0];
        }

    }
}
