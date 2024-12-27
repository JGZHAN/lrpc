package cn.jgzhan.lrpc.common.group;

import cn.jgzhan.lrpc.common.thread.VirtualThreadFactory;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ThreadFactory;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/6
 */
public class VirtualThreadNioEventLoopGroup extends NioEventLoopGroup {


    public VirtualThreadNioEventLoopGroup(int i) {
        super(i);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        return new VirtualThreadFactory(getClass());
    }
}
