package cn.jgzhan.lrpc.example.service.impl;

import cn.jgzhan.lrpc.springboot.annotation.LrpcReference;
import cn.jgzhan.lrpc.example.service.ExampleService;
import cn.jgzhan.lrpc.example.service.HelloService;
import org.springframework.stereotype.Component;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/18
 */
@Component
public class ExampleServiceImpl implements ExampleService {

//    @LrpcReference(addressArr = "127.0.0.1:23923")
    @LrpcReference
    private HelloService helloService;

    @Override
    public String call(String name) {
        return helloService.sayHello(name);
    }
}
