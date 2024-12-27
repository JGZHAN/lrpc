package consumer;

import cn.jgzhan.lrpc.client.Comsumer;
import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import cn.jgzhan.lrpc.example.api.TestService;
import cn.jgzhan.lrpc.registry.ServiceManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;

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
        final var lrpcProperties = new LrpcProperties("application.properties");
        PROPERTIES_THREAD_LOCAL.set(lrpcProperties);
        final var serviceManager = new ServiceManager();
        Comsumer comsumer = new Comsumer(serviceManager.getClient());
        final var service = comsumer.getProxy(TestService.class, Set.of(Pair.of("127.0.0.1", lrpcProperties.getServer().getPort())));
//        final var service = comsumer.getProxy(TestService.class);
        final var result = service.hello("张三");
        log.info("测试结束, 结果: {}", result);
    }
}
