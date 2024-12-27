package cn.jgzhan.lrpc.example.client.loadbalance;

import cn.jgzhan.lrpc.example.common.dto.Pair;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/16
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final Map<Method, AtomicInteger> INDEX_MAP;
    public RoundRobinLoadBalancer(){
        INDEX_MAP = new ConcurrentHashMap<>();
    }

    @Override
    public LoadBalancerType getLoadBalancerType() {
        return LoadBalancerType.ROUND_ROBIN;
    }
    @Override
    public Pair<String, Integer> selectServiceAddress(Method service, Set<Pair<String, Integer>> addressSet) {
        if (addressSet == null || addressSet.isEmpty()) {
            throw new RuntimeException("无可用地址");
        }
        final var nowIndex = INDEX_MAP.computeIfAbsent(service, k -> new AtomicInteger(0)).getAndIncrement();
        return select(addressSet, nowIndex);
    }

    private static Pair<String, Integer> select(Set<Pair<String, Integer>> serviceAddress, int nowIndex) {
        nowIndex = nowIndex % serviceAddress.size();
        return serviceAddress.stream()
                .skip(nowIndex)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("获取地址失败"));
    }
}
