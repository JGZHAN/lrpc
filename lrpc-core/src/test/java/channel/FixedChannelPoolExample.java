package channel;

import cn.jgzhan.lrpc.common.config.HandlerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class FixedChannelPoolExample {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // Add your handlers here
                        pipeline.addLast(HandlerConfig.getStickyPackHalfPackHandler());
                        pipeline.addLast(HandlerConfig.getLoggingHandler());
//                        pipeline.addLast(HandlerConfig.getRpcDecoder());
                        pipeline.addLast(HandlerConfig.getRpcRespHandler());
                    }
                });

        FixedChannelPool pool = new FixedChannelPool(
                bootstrap.remoteAddress("localhost", 8080),
                new ChannelPoolHandler() {
                    @Override
                    public void channelReleased(Channel ch) throws Exception {
                        // Called when a channel is released back to the pool
                        System.out.println("Channel released");
                    }

                    @Override
                    public void channelAcquired(Channel ch) throws Exception {
                        // Called when a channel is acquired from the pool
                        System.out.println("Channel acquired");
                    }

                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        // Called when a new channel is created
                        System.out.println("Channel created");
                    }
                },
                10 // Maximum number of connections in the pool
        );

        // Acquire a channel from the pool
        Future<Channel> future = pool.acquire();
        future.addListener((GenericFutureListener<Future<Channel>>) f -> {
            if (f.isSuccess()) {
                final Channel channel = f.getNow();
                // Use the channel
                System.out.println("Channel acquired successfully");
                channel.writeAndFlush("Hello, World!").sync();
                // Release the channel back to the pool
                pool.release(channel);
            } else {
                System.err.println("Failed to acquire channel");
            }
        });

    }
}
