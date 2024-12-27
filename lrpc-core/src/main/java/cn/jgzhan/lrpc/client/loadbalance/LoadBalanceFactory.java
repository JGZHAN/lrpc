package cn.jgzhan.lrpc.client.loadbalance;

import lombok.NonNull;

import java.util.*;

public class LoadBalanceFactory {

    private static Map<LoadBalancerType, LoadBalancer> TYPE_LOAD_BALANCER_MAP;

    static {
        init();
    }
    public static void init() {
        TYPE_LOAD_BALANCER_MAP = new HashMap<>();
        final var allBalancers = getAllBalancers();
        Optional.of(allBalancers)
                .ifPresent(loadBalancers ->
                        loadBalancers.forEach(loadBalancer -> TYPE_LOAD_BALANCER_MAP.put(loadBalancer.getLoadBalancerType(), loadBalancer)));
    }

    @NonNull
    private static Set<LoadBalancer> getAllBalancers() {
        //1. 通过spi机制获取所有的负载均衡器
        final ServiceLoader<LoadBalancer> loadBalancers = ServiceLoader.load(LoadBalancer.class);
        final Set<LoadBalancer> result = new HashSet<>();
        loadBalancers.forEach(result::add);
        return result;
    }

    /**
     * By type load balancer.
     *
     * @param type the type
     * @return the load balancer
     */
    public static LoadBalancer byType(LoadBalancerType type) {
        return TYPE_LOAD_BALANCER_MAP.get(type);
    }
}
