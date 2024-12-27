package cn.jgzhan.lrpc.example.impl;

import cn.jgzhan.lrpc.common.annotation.LrpcService;
import cn.jgzhan.lrpc.example.api.TestService;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@LrpcService
public class TestServiceImpl implements TestService {
    @Override
    public String hello(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Hello, " + name;
    }

    @Override
    public String hi(String name) {
        return "Hi, " + name;
    }
}
