package cn.jgzhan.lrpc.server;

import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.config.HandlerConfig;
import cn.jgzhan.lrpc.common.group.VirtualThreadNioEventLoopGroup;
import cn.jgzhan.lrpc.registry.ServiceManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.BindException;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/10
 */
public class LrpcServerBootstrap {
    private static final Logger log = LoggerFactory.getLogger(LrpcServerBootstrap.class);

    private static ServerBootstrap bootstrap;

    private final LrpcProperties lrpcProperties;

    private final ServiceManager.Server serviceManager;

    @Getter
    private int port;

    // 剩余尝试绑定端口次数
    private int tryBindPortCount = 5;

    public LrpcServerBootstrap(ServiceManager.Server serviceManager) {
        this.lrpcProperties = PROPERTIES_THREAD_LOCAL.get();
        this.serviceManager = serviceManager;
    }

    public void start() {
        // 1. 创建一个服务端对象
        bootstrap = new ServerBootstrap();
        final var boss = new NioEventLoopGroup(1);
        final var worker = new VirtualThreadNioEventLoopGroup(lrpcProperties.getServer().getWorkerMax());
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                PROPERTIES_THREAD_LOCAL.set(lrpcProperties);
                final var pipeline = ch.pipeline();
                pipeline.addLast(HandlerConfig.getStickyPackHalfPackHandler());
//                pipeline.addLast(HandlerConfig.getLoggingHandler());
                pipeline.addLast(HandlerConfig.getRpcDecoder());
                pipeline.addLast(HandlerConfig.getRpcReqHandler(serviceManager));
            }
        });
        try {
            bindPort(lrpcProperties.getServer().getPort());
        } catch (Exception e) {
            log.error("服务启动失败");
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private void bindPort(int port) {
        if (--tryBindPortCount < 0) {
            throw new RuntimeException("端口绑定失败");
        }
        // 2. 启动服务端
        final ChannelFuture future;
        try {
            future = bootstrap.bind(port);
        } catch (Exception e) {
            log.error("服务启动失败", e);
            throw new RuntimeException(e);
        }
        // 3. 服务端开始监听
        try {
            future.sync();
            this.port = port;
            log.info("服务启动成功, 端口: {}", port);
        } catch (Exception e) {
            if (e instanceof BindException) {
                log.error("{} 端口已被占用, 尝试绑定端口: {}", port, port + 1);
                bindPort(port + 1);
            } else {
                log.error("服务启动失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        if (bootstrap == null) {
            log.error("服务未启动");
            return;
        }
        bootstrap.config().group().shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.info("Lrpc服务端关闭成功");
            } else {
                log.error("Lrpc服务端关闭失败", future.cause());
            }
        });
    }

}
