package cn.jgzhan.lrpc.client;

import cn.jgzhan.lrpc.common.dto.Message;
import cn.jgzhan.lrpc.common.dto.Pair;
import cn.jgzhan.lrpc.common.dto.RpcRequestMessage;
import cn.jgzhan.lrpc.common.exception.LRPCTimeOutException;
import cn.jgzhan.lrpc.common.handler.RpcRespHandler;
import cn.jgzhan.lrpc.registry.ServiceManager;
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

    /**
     * 该方法只用于测试，不建议使用
     * 实际使用中，应该使用有参构造函数
     * 与生产者共用一个serviceManager，后面一起关闭
     */
    @Deprecated
    public Comsumer() {
        final var serviceManager = new ServiceManager();
        serviceManager.start();
        // 调用有参构造函数
        this.consumerManager = new ConsumerManager(serviceManager.getClient());
    }

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
            return consumerManager.send(msg, method, serviceAddress);
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
