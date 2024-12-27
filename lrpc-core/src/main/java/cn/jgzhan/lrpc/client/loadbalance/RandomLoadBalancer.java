package cn.jgzhan.lrpc.example.client.loadbalance;

import cn.jgzhan.lrpc.example.common.dto.Pair;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/16
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public LoadBalancerType getLoadBalancerType() {
        return LoadBalancerType.RANDOM;
    }

    @Override
    public Pair<String, Integer> selectServiceAddress(Method service, Set<Pair<String, Integer>> addressSet) {

        assert addressSet != null && !addressSet.isEmpty();

        final var skipNum = ThreadLocalRandom.current().nextInt(0, addressSet.size());

        return addressSet.stream()
                .skip(skipNum)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("获取地址失败"));
    }

}
