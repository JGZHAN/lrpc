package cn.jgzhan.lrpc.example.service.impl;

import cn.jgzhan.lrpc.common.annotation.LrpcService;
import cn.jgzhan.lrpc.example.service.HelloService;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/17
 */
@LrpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
