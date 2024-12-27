package cn.jgzhan.lrpc.registry.impl;

import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.dto.ProviderInfo;
import cn.jgzhan.lrpc.registry.enums.Change;
import cn.jgzhan.lrpc.registry.RegistryCenter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/17
 */
@Slf4j
public class LocalRegistryCenter implements RegistryCenter {

    @Override
    public void registerService(Method method, int port) {
        // do nothing
    }

    @Override
    public Set<Pair<String, Integer>> getService(Method service) {
        return Set.of();
    }

    @Override
    public void watch(BiConsumer<Change, ProviderInfo> changeListener) {
        // do nothing
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }
}
