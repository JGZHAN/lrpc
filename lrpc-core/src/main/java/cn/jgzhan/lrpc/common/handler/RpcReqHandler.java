package cn.jgzhan.lrpc.common.handler;

import cn.jgzhan.lrpc.common.dto.RpcRequestMessage;
import cn.jgzhan.lrpc.common.dto.RpcResponseMessage;
import cn.jgzhan.lrpc.registry.ServiceManager;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/4
 */
@ChannelHandler.Sharable
public class RpcReqHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(RpcReqHandler.class);
    private final ServiceManager.Server serviceManager;

    public RpcReqHandler(ServiceManager.Server serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {

        log.info("接收到消息 {}", JSON.toJSON(msg));
        final var interfaceName = msg.getInterfaceName();
        final var methodName = msg.getMethodName();

        final var service = serviceManager.getService(interfaceName);

        final var response = new RpcResponseMessage();
        response.setMessageId(msg.getMessageId());
        try {
            final Class<?> aClass = service.getClass();
            final var method = aClass.getMethod(methodName, msg.getParameterTypes());
            final var result = method.invoke(service, msg.getParameterValues());
            response.setReturnValue(result);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("e : ", e);
            response.setExceptionValue(new Error(e.getCause().getMessage()));
        }
        ctx.writeAndFlush(response).addListener(future -> {
            if (future.isSuccess()) {
                log.info("消息响应成功 {}", JSON.toJSON(msg));
                return;
            }
            log.error("发送消息时有错误发生: ", future.cause());
        });
    }

}
