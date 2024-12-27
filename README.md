# 一个使用java新特性虚拟线程，结合netty，编写的一个基于自定义协议的RPC（远程过程调用）框架

## 模块说明
- rpc-core：rpc框架的核心代码，适用于一般的maven项目
- rpc-spring：rpc框架的spring集成，适用于spring项目
- rpc-spring-boot-starter：rpc框架的spring-boot集成，适用于spring-boot项目,提供了自动配置功能
- rpc-example：rpc框架的使用示例，这里的例子主要是基于spring-boot项目

### 以上的模块除spring-boot-starter外，在test目录下都有相应的测试用例，可以参考测试用例的使用方式

## rpc-core
- rpc框架的核心代码，适用于一般的maven项目,主要包括以下几个部分
    - client：rpc框架的客户端,主要包括了客户端的连接管理、请求发送、请求接收、负载均衡等功能；
    - server：rpc框架的服务端,主要包括了服务端的serverSocket监听、请求接收、请求处理、响应发送等功能；
    - registry：rpc框架的服务注册中心,主要包括了服务注册、服务发现等功能；
    - common：rpc框架的公共模块,主要包括了一些公共的工具类、协议编解码定义、注解定义；
    - example：rpc框架的用例的示例接口及实现类，供测试使用；
### rpc-core的使用方式
- 服务端
#### 用例链接：[ServerTest](lrpc-core/src/test/java/server/ServerTest.java)
```java

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

    // 阻塞主线程,不让服务停止
    System.in.read();
}
```

- 客户端
- 用例链接：[ClientTest](lrpc-core/src/test/java/consumer/ClientTest.java)
```java

@Test
public void testClient() throws LRPCTimeOutException {
    // 给threadLocal赋值
    final var lrpcProperties = new LrpcPropertiesCore();
    PROPERTIES_THREAD_LOCAL.set(lrpcProperties);

    // 启动服务
    Comsumer comsumer = new Comsumer();

    // 获取代理,注册中心使用本地注册时（相当于不使用注册中心），需要传入服务端的地址
    final var service = comsumer.getProxy(TestService.class, Set.of(Pair.of("127.0.0.1", lrpcProperties.getServer().getPort())));
    // 使用注册中心时，只需要传入服务名即可负载均衡的调用服务
    // final var service = comsumer.getProxy(TestService.class);

    // 调用方法
    final var result = service.hello("张三");
    log.info("测试结束, 结果: {}", result);
}
```