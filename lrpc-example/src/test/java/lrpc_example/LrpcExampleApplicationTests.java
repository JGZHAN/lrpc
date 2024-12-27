package cn.jgzhan.lrpc_example;

import cn.jgzhan.lrpc.common.annotation.LrpcReference;
import cn.jgzhan.lrpc.example.api.TestService;
import cn.jgzhan.lrpc_example.properties.LrpcPropertiesCopy;
import cn.jgzhan.lrpc_example.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LrpcExampleApplicationTests {

    //	@Autowired
    @LrpcReference
    private HelloService helloService;
    @LrpcReference
    private TestService testService;
    @Autowired
    private LrpcPropertiesCopy lrpcPropertiesCopy;

    @Test
    void contextLoads() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            Thread.sleep(1000);
//            System.out.println(exampleService.call("jgzhan" + i));
//            System.out.println(helloService.sayHello("jgzhan" + i));
            System.out.println(testService.hello("jgzhan" + i));
        }
    }


}
