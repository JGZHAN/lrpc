package cn.jgzhan.lrpc.service.impl;

import cn.jgzhan.lrpc.common.annotation.LrpcReference;
import cn.jgzhan.lrpc.service.ExampleService;
import cn.jgzhan.lrpc.service.HelloService;
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
