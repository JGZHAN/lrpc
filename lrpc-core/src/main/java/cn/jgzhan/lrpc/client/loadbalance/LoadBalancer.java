package cn.jgzhan.lrpc.client.loadbalance;

import cn.jgzhan.lrpc.common.dto.Pair;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/16
 */
public interface LoadBalancer {

    /**
     * Get load balancer type load balancer type.
     *
     * @return the load balancer type
     */
    LoadBalancerType getLoadBalancerType();

    /**
     * Select service address string.
     *
     * @param service    the service name
     * @param addressSet
     * @return the string
     */
    Pair<String, Integer> selectServiceAddress(Method service, Set<Pair<String, Integer>> addressSet);

}
