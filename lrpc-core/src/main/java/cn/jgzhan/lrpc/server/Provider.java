package cn.jgzhan.lrpc.server;

import cn.jgzhan.lrpc.registry.ServiceManager;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/23
 */
public class Provider {

    private final LrpcServerBootstrap lrpcServerBootstrap;

    private final ServiceManager.Server serviceManager;


    /**
     * 该方法只用于测试，不建议使用
     * 实际使用中，应该使用有参构造函数
     * 与消费者共用一个serviceManager，后面一起关闭
     */
    @Deprecated
    public Provider() {
        final var newServiceManager = new ServiceManager();
        newServiceManager.start();
        // 调用有参数的构造方法
        this.serviceManager = newServiceManager.getServer();
        this.lrpcServerBootstrap = new LrpcServerBootstrap(serviceManager);
    }

    public Provider(ServiceManager.Server serviceManager) {
        this.serviceManager = serviceManager;
        this.lrpcServerBootstrap = new LrpcServerBootstrap(serviceManager);
    }

    public void start() {
        lrpcServerBootstrap.start();
    }

    public void registry(Object instance, int port) {
        serviceManager.registry(instance, port);
    }

    public int getPort() {
        return lrpcServerBootstrap.getPort();
    }

    public void close() {
        lrpcServerBootstrap.stop();
    }


}
