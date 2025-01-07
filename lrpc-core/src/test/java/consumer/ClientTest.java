package consumer;

import cn.jgzhan.lrpc.client.Comsumer;
import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.config.LrpcPropertiesCore;
import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import cn.jgzhan.lrpc.example.api.TestService;
import cn.jgzhan.lrpc.registry.ServiceManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/11
 */
public class ClientTest {
    private static final Logger log = LoggerFactory.getLogger(ClientTest.class);


    public static void main(String[] args) {
        final var clientTest = new ClientTest();
        try {
            clientTest.testClient();
        } catch (LRPCTimeOutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClient() throws LRPCTimeOutException {
        // 给threadLocal赋值
//        final var lrpcProperties = new LrpcPropertiesCore("application.properties");
        final var lrpcProperties = new LrpcPropertiesCore();
        PROPERTIES_THREAD_LOCAL.set(lrpcProperties);

        // 启动服务
        Comsumer comsumer = new Comsumer();

        // 获取代理
        final var service = comsumer.getProxy(TestService.class, Set.of(Pair.of("127.0.0.1", lrpcProperties.getServer().getPort())));
//        final var service = comsumer.getProxy(TestService.class);

        // 调用方法
        final var result = service.hello("张三");
        log.info("测试结束, 结果: {}", result);
    }


    @Test
    public void testClientBatch() throws LRPCTimeOutException, IOException, InterruptedException {
        // 给threadLocal赋值
//        final var lrpcProperties = new LrpcPropertiesCore("application.properties");
        final var executor = Executors.newVirtualThreadPerTaskExecutor();

//        for (int i = 0; i < 10; i++) {
//        int finalI = i;
        final var atomicInteger = new AtomicInteger(0);
        for (; ; ) {
            int finalI = atomicInteger.getAndIncrement();

            executor.submit(() -> {
                final var lrpcProperties = new LrpcPropertiesCore();
                PROPERTIES_THREAD_LOCAL.set(lrpcProperties);

                // 启动服务
                Comsumer comsumer = new Comsumer();
                // 获取代理
                final var service = comsumer.getProxy(TestService.class, Set.of(Pair.of("127.0.0.1", lrpcProperties.getServer().getPort())));

                try {
                    // 调用方法
                    final var result = service.hello("张三" + finalI);
                    log.info("测试结束, 结果: {}", result);
                } catch (Exception e) {
                    log.error("测试失败", e);
                }
            });

            Thread.sleep(10);

        }
//        System.in.read();
    }
}
