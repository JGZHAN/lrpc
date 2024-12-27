package cn.jgzhan.lrpc.common.config;

import cn.jgzhan.lrpc.common.handler.RpcReqHandler;
import cn.jgzhan.lrpc.common.handler.RpcRespHandler;
import cn.jgzhan.lrpc.common.protocol.LRPCDecoder;
import cn.jgzhan.lrpc.registry.ServiceManager;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/10
 */
public class HandlerConfig {

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

    private static final ChannelHandler RPC_RESP_HANDLER = new RpcRespHandler();

    /**
     * 该处理器不可共享
     * @return
     */
    public static ChannelHandler getStickyPackHalfPackHandler() {
        return new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0);
    }

    public static ChannelHandler getLoggingHandler() {
        return LOGGING_HANDLER;
    }

    public static ChannelHandler getRpcRespHandler() {
        return RPC_RESP_HANDLER;
    }


    public static ChannelHandler getRpcDecoder() {
        return new LRPCDecoder();
    }

    public static ChannelHandler getRpcReqHandler(ServiceManager.Server serviceManager) {
        return new RpcReqHandler(serviceManager);
    }



}
