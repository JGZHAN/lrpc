package server;

import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.example.impl.TestServiceImpl;
import cn.jgzhan.lrpc.spring.ReferenceBeanPostProcessor;
import org.junit.Test;

import java.io.IOException;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/10
 */
public class ServiceTest {


    @Test
    public void testStartServer() throws InterruptedException, IOException {

        final var lrpcProperties = new LrpcProperties("application.properties");
        final var referenceBeanPostProcessor = new ReferenceBeanPostProcessor(lrpcProperties);
        referenceBeanPostProcessor.postProcessBeforeInitialization(new TestServiceImpl(), "testService");
        System.in.read();
    }


}
