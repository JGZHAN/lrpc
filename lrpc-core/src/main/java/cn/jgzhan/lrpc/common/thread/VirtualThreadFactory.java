package cn.jgzhan.lrpc.common.thread;

import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/6
 */
public class VirtualThreadFactory extends DefaultThreadFactory {
    private final LrpcProperties properties;
    public VirtualThreadFactory(Class<?> poolType) {
        super(poolType);
        this.properties = LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL.get();
    }

    @Override
    protected Thread newThread(Runnable r, String name) {
        LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL.set(properties);
        return new PoolVirtualThread(threadGroup, r, name);
    }
}
