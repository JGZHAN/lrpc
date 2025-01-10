package cn.jgzhan.lrpc.client.net;

import cn.jgzhan.lrpc.common.dto.RpcRequestMessage;
import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import cn.jgzhan.lrpc.common.handler.RpcRespHandler;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Data
public class ChannelManager {


    private static final Logger log = LoggerFactory.getLogger(ChannelManager.class);


    public Object executeWithChannelPool(ChannelPool channelPool,
                                                  BiFunction<Channel, RpcRequestMessage, Promise<Object>> function,
                                                  RpcRequestMessage msg) throws LRPCTimeOutException {
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
        final var promise = function.apply(channel, msg);
        try {
            return getResult(promise, msg.getMessageId());
        } finally {
            // 这里的释放需要放在拿到结果之后，否则会导臃连接被释放
            channelPool.release(channel);
        }
    }

    private static Object getResult(Promise<Object> promise, Integer messageId) throws LRPCTimeOutException {
        try {
            // 超时等待
            if (promise.await(5, TimeUnit.SECONDS)) {
                if (promise.isSuccess()) {
                    return promise.getNow();
                } else {
                    throw new RuntimeException(promise.cause());
                }
            } else {
                throw new LRPCTimeOutException("请求超时");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("操作被中断", e);
        } finally {
            // 确保 promise 被移除
            RpcRespHandler.removePromise(messageId);
        }
    }

}
