package cn.jgzhan.lrpc.client;

import cn.jgzhan.lrpc.client.loadbalance.LoadBalanceFactory;
import cn.jgzhan.lrpc.client.loadbalance.LoadBalancer;
import cn.jgzhan.lrpc.client.net.ChannelManager;
import cn.jgzhan.lrpc.client.net.LrpcChannelPoolFactory;
import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.common.dto.Message;
import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.dto.RpcRequestMessage;
import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import cn.jgzhan.lrpc.common.handler.RpcRespHandler;
import cn.jgzhan.lrpc.registry.ServiceManager;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

import static cn.jgzhan.lrpc.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class ConsumerManager {

    private static final Logger log = LoggerFactory.getLogger(ConsumerManager.class);

    private final LrpcProperties lrpcProperties;

    private final LoadBalancer loadBalancer;

    private final ChannelManager channelManager;

    private final ServiceManager.Client serviceManager;


    public ConsumerManager(ServiceManager.Client serviceManager) {
        this.lrpcProperties = PROPERTIES_THREAD_LOCAL.get();
        this.loadBalancer = LoadBalanceFactory.byType(lrpcProperties.getClient().getLoadBalance());
        this.channelManager = new ChannelManager();
        this.serviceManager = serviceManager;
    }


    // 发送请求
    public Promise<Object> send(RpcRequestMessage msg, Method method, Set<Pair<String, Integer>> addressSet) throws LRPCTimeOutException {
        final Function<Channel, Promise<Object>> channelExeFunction = channelExeFunction(msg);
        // 负载均衡选择服务地址
        final var address = clazzToAddress(method, addressSet);
        // 获取连接池
        final var channelPool = getChannelPool(address);
        // 在连接池中执行请求
        return channelManager.executeWithChannelPool(channelPool, channelExeFunction);
    }


    private static GenericFutureListener<Future<? super Void>> processAftermath(DefaultPromise<Object> promise, Message msg) {
        return future -> {
            log.info("发送请求结束 {}", JSON.toJSON(msg));
            if (future.isSuccess()) {
                return;
            }
            log.error("发送请求失败", future.cause());
            promise.setFailure(future.cause());
        };
    }

    // 选择服务地址，负载均衡
    private Pair<String, Integer> clazzToAddress(Method method, Set<Pair<String, Integer>> addressSet) {
        if (addressSet != null && !addressSet.isEmpty()) {
            // 若指定了服务地址，则在指定的服务地址中选择
            return loadBalancer.selectServiceAddress(method, addressSet);
        }
        addressSet = serviceManager.getServiceAddress(method);
        // 若未指定服务地址，则在注册中心的服务地址中选择
        return loadBalancer.selectServiceAddress(method, addressSet);
    }

    private FixedChannelPool getChannelPool(Pair<String, Integer> address) {
        final var host = address.left;
        final var port = address.right;
        return serviceManager.getChannelPool(address,
                _ -> LrpcChannelPoolFactory.createFixedChannelPool(host, port, lrpcProperties.getClient().getAddressMaxConnection()));
    }

    private static Function<Channel, Promise<Object>> channelExeFunction(RpcRequestMessage msg) {
        // 发送请求，且处理写失败
        return channel -> {
            final var promise = new DefaultPromise<>(channel.eventLoop());
            RpcRespHandler.addPromise(msg.getMessageId(), promise);
            // 发送请求，且处理写失败
            final var channelFuture = channel.writeAndFlush(msg);
            channelFuture.addListener(processAftermath(promise, msg));
            return promise;
        };
    }

}
