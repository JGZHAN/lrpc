package cn.jgzhan.lrpc.example.client;

import cn.jgzhan.lrpc.example.common.dto.Message;
import cn.jgzhan.lrpc.example.common.dto.Pair;
import cn.jgzhan.lrpc.example.common.dto.RpcRequestMessage;
import cn.jgzhan.lrpc.example.common.handler.RpcRespHandler;
import cn.jgzhan.lrpc.example.registry.ServiceManager;
import com.alibaba.fastjson2.JSON;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class Comsumer {

    private static final Logger log = LoggerFactory.getLogger(Comsumer.class);

    private final ConsumerManager consumerManager;

    public Comsumer(ServiceManager.Client serviceManager) {
        this.consumerManager = new ConsumerManager(serviceManager);
    }

    /**
     * 获取代理
     */
    public <T> T getProxy(Class<T> clazz) {
        return getProxy(clazz, null);
    }

    /**
     * 获取代理
     */
    public <T> T getProxy(Class<T> clazz, Set<Pair<String, Integer>> serviceAddress) {
        final var proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            RpcRequestMessage msg = buildRpcRequestMessage(clazz, method, args);
            final var resultPromise = consumerManager.send(msg, method, serviceAddress);
            return getResult(resultPromise, msg.getMessageId());
        });
        return clazz.cast(proxyInstance);
    }

    private static <T> RpcRequestMessage buildRpcRequestMessage(Class<T> clazz, Method method, Object[] args) {
        final var msg = new RpcRequestMessage();
        msg.setInterfaceName(clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParameterTypes(method.getParameterTypes());
        msg.setParameterValues(args);
        msg.setReturnType(method.getReturnType());
        msg.setMessageId(UUID.randomUUID().hashCode());
        return msg;
    }

    private static Object getResult(Promise<Object> promise, Integer messageId) {
        try {
            // 超时等待
            promise.await(5, TimeUnit.SECONDS);
            if (promise.isSuccess()) {
                return promise.getNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 防止异常情况下，promise没有被移除
            RpcRespHandler.removePromise(messageId);
        }
        throw new RuntimeException(promise.cause());
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
}
