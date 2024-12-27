package cn.jgzhan.lrpc.client.net;

import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Data
public class ChannelManager {


    private static final Logger log = LoggerFactory.getLogger(ChannelManager.class);


    public Promise<Object> executeWithChannelPool(ChannelPool channelPool, Function<Channel, Promise<Object>> function) throws LRPCTimeOutException {
        // 1. 从连接池中获取连接，等待超市时间，未获取连接则抛出异常
        final Future<Channel> future = channelPool.acquire();
        Channel channel;
        try {
            final boolean acquired = future.await(3, TimeUnit.SECONDS);
            if (!acquired) {
                log.error("获取连接超时");
                throw new LRPCTimeOutException("获取连接超时");
            }
            channel = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        final var result = function.apply(channel);
        channelPool.release(channel);
        return result;
    }

}
