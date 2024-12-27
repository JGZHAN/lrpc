package cn.jgzhan.lrpc.client.net;

import cn.jgzhan.lrpc.common.config.HandlerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/11
 */
public class LrpcChannelPoolFactory {

    private static final Logger log = LoggerFactory.getLogger(LrpcChannelPoolFactory.class);

    private static final AtomicInteger ID = new AtomicInteger(0);


    /**
     * 创建连接池
     * @param host 主机
     * @param port 端口
     * @return 连接池
     */
    public static FixedChannelPool createFixedChannelPool(String host, int port, int maxConnection) {
        final var properties = PROPERTIES_THREAD_LOCAL.get();
        return new LrpcChannelPool(
                ClientBootstrap.newBootstrap().remoteAddress(host, port),
                new ChannelPoolHandler() {
                    @Override
                    public void channelReleased(Channel ch) throws Exception {
                        // 当通道被释放回池中时调用
                        log.debug("通道被释放回池中");
                    }

                    @Override
                    public void channelAcquired(Channel ch) throws Exception {
                        // 当从池中获取通道时调用
                        log.debug("从池中获取通道");
                    }

                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        // 当新创建一个通道时调用
                        log.info("新创建一个通道，添加pipeline处理器 : {}", ID.incrementAndGet());
                        PROPERTIES_THREAD_LOCAL.set(properties);
                        ClientBootstrap.addHandler(ch);
                    }
                },
                maxConnection // 连接池的最大大小
        );
    }


    /**
     * @author jgzhan
     * @version 1.0
     */
    public static class ClientBootstrap {

        public static Bootstrap newBootstrap() {
            return init();
        }

        private static Bootstrap init() {
            var bootstrap = new Bootstrap();
            final var group = new NioEventLoopGroup();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            return bootstrap;
        }

        public static void addHandler(Channel ch) {
            final var pipeline = ch.pipeline();
            pipeline.addLast(HandlerConfig.getStickyPackHalfPackHandler());
//            pipeline.addLast(HandlerConfig.getLoggingHandler());
            pipeline.addLast(HandlerConfig.getRpcDecoder());
            pipeline.addLast(HandlerConfig.getRpcRespHandler());
        }

    }

    /**
     * @author jgzhan
     * @version 1.0
     * @date 2024/12/12
     */
    public static class LrpcChannelPool extends FixedChannelPool {
        public LrpcChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections) {
            super(bootstrap, handler, maxConnections);
        }
    
        public LrpcChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections, int maxPendingAcquires) {
            super(bootstrap, handler, maxConnections, maxPendingAcquires);
        }
    
        public LrpcChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires) {
            super(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires);
        }
    
        public LrpcChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck) {
            super(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, releaseHealthCheck);
        }
    
        public LrpcChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck, boolean lastRecentUsed) {
            super(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, releaseHealthCheck, lastRecentUsed);
        }
    
        @Override
        public void close() {
            super.close();
            super.bootstrap().config().group().shutdownGracefully();
        }
    }
}
