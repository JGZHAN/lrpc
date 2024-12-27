package cn.jgzhan.lrpc.example.common.thread;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

import static cn.jgzhan.lrpc.example.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/6
 */
public class PoolVirtualThread extends FastThreadLocalThread {
    private static final Logger log = LoggerFactory.getLogger(PoolVirtualThread.class);

    private final Builder.OfVirtual VIRTUAL_THREAD_BUILDER;

    public PoolVirtualThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        /**
         * 因为ThreadBuilders.VirtualThreadBuilder的scheduler属性是私有的，且没有提供set方法
         * 所以通过反射强制修改属性scheduler
         */
        VIRTUAL_THREAD_BUILDER = Thread.ofVirtual();
        try {
            final var schedulerField = VIRTUAL_THREAD_BUILDER.getClass().getDeclaredField("scheduler");
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

            schedulerField.set(VIRTUAL_THREAD_BUILDER, forkJoinPool);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("修改属性scheduler失败", e);
        }
    }

    @Override
    public void start() {
        final var unstarted = VIRTUAL_THREAD_BUILDER.unstarted(this);
        unstarted.setName(this.getName());
        unstarted.start();
    }
}
