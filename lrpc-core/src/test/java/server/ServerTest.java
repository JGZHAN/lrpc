package server;

import cn.jgzhan.lrpc.common.config.LrpcPropertiesCore;
import cn.jgzhan.lrpc.example.impl.TestServiceImpl;
import cn.jgzhan.lrpc.server.Provider;
import org.junit.Test;

import java.io.IOException;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/26
 */
public class ServerTest {


    @Test
    public void testStartServer() throws IOException {
        // 给threadLocal赋值
        final var properties = new LrpcPropertiesCore();
        PROPERTIES_THREAD_LOCAL.set(properties);

        // 启动提供者
        final var provider = new Provider();
        provider.start();

        // 注册服务
        final var testService = new TestServiceImpl();
        provider.registry(testService, properties.getServer().getPort());
        System.out.println("服务启动成功");

        System.in.read();
    }

}
