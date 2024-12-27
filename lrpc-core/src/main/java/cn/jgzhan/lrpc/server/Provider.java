package cn.jgzhan.lrpc.example.server;

import cn.jgzhan.lrpc.example.registry.ServiceManager;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/23
 */
public class Provider {

    private final LrpcServerBootstrap lrpcServerBootstrap;

    private final ServiceManager.Server serviceManager;

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
