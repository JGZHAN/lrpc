package cn.jgzhan.lrpc.common.handler;

import cn.jgzhan.lrpc.common.dto.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/4
 */
@ChannelHandler.Sharable
public class RpcRespHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    private static final Logger log = LoggerFactory.getLogger(RpcRespHandler.class);


    private static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    public static void addPromise(Integer sequenceId, Promise<Object> promise) {
        PROMISES.put(sequenceId, promise);
    }

    public static Promise<Object> removePromise(Integer sequenceId) {
        return PROMISES.remove(sequenceId);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {

        final var sequenceId = msg.getMessageId();
        final var promise = removePromise(sequenceId);
        if (promise == null) {
            return;
        }

        if (msg.getExceptionValue() != null) {
            promise.setFailure(msg.getExceptionValue());
        }
        promise.setSuccess(msg.getReturnValue());

    }

}
