package cn.jgzhan.lrpc.common.thread;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/6
 */
public class PoolVirtualThread extends FastThreadLocalThread {
    private static final Logger log = LoggerFactory.getLogger(PoolVirtualThread.class);

    private static final Builder.OfVirtual VIRTUAL_THREAD_BUILDER = createDefaultScheduler();

    /**
     * 因为ThreadBuilders.VirtualThreadBuilder的scheduler属性是私有的，且没有提供set方法
     * 所以通过反射强制修改属性scheduler
     */
    private static Builder.OfVirtual createDefaultScheduler() {
        final Builder.OfVirtual virtualThreadBuilder = Thread.ofVirtual();
        try {
            final var schedulerField = virtualThreadBuilder.getClass().getDeclaredField("scheduler");
            schedulerField.setAccessible(true);
            final var serverWorkerMax = PROPERTIES_THREAD_LOCAL.get().getServer().getWorkerMax();
            boolean asyncMode = true;// FIFO
            final var forkJoinPool = new ForkJoinPool(serverWorkerMax,
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    null,
                    asyncMode,
                    0,
                    serverWorkerMax,
                    0,
                    pool -> true,
                    30, SECONDS);

            schedulerField.set(virtualThreadBuilder, forkJoinPool);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("修改属性scheduler失败", e);
        }
        return virtualThreadBuilder;
    }


    public PoolVirtualThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    @Override
    public void start() {
        final var unstarted = VIRTUAL_THREAD_BUILDER.unstarted(this);
        unstarted.setName(this.getName());
        unstarted.start();
    }
}
