package cn.jgzhan.lrpc.example.common.protocol;

import cn.jgzhan.lrpc.example.common.config.LrpcProperties;
import cn.jgzhan.lrpc.example.common.dto.Message;
import cn.jgzhan.lrpc.example.common.serializer.Serializer;
import cn.jgzhan.lrpc.example.common.serializer.SerializerEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.jgzhan.lrpc.example.common.config.LrpcPropertiesUtils.PROPERTIES_THREAD_LOCAL;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class LRPCDecoder extends ByteToMessageCodec<Message> {
    private static final Logger log = LoggerFactory.getLogger(LRPCDecoder.class);

    private static final byte[] PROTOCOL_MAGIC = new byte[]{'L', 'R', 'P', 'C'};
    private static final byte PROTOCOL_VERSION = 1;

    private final LrpcProperties.Protocol protocol;

    public LRPCDecoder() {
        this.protocol = PROPERTIES_THREAD_LOCAL.get().getProtocol();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 写入魔数，代表协议， 4字节
        out.writeBytes(PROTOCOL_MAGIC);
        // 2. 写入版本号，代表协议版本，1字节
        out.writeByte(PROTOCOL_VERSION);
        // 3. 写入序列化算法， 0: json, 1个字节
        out.writeByte(protocol.getSerializer().ordinal());
        // 4. 序列号，4字节
        out.writeInt(msg.getMessageId());
        // 5. 消息类型，1字节
        out.writeByte(msg.getType());
        // 5.1. 填充补齐字节，1字节
        out.writeByte(0);

        final var outContent = protocol.getSerializer().serialize(msg);
        // 6. 消息长度， 4字节
        out.writeInt(outContent.length);
        // 7. 消息内容， n字节
        out.writeBytes(outContent);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 0. 标记当前读取位置
        in.markReaderIndex();
        // 1. 读取魔数
        for (byte b : PROTOCOL_MAGIC) {
            if (in.readByte() != b) {
                log.debug("非LRPC协议，跳过");
                in.resetReaderIndex();
                return;
            }
        }
        // 2. 读取版本号
        if (in.readByte() != PROTOCOL_VERSION) {
            return;
        }
        // 3. 读取序列化算法
        final Serializer serializer = SerializerEnum.values()[in.readByte()];
        // 4. 读取序列号
        int messageId = in.readInt();
        // 5. 读取消息类型
        byte type = in.readByte();
        // 5.1. 读取补齐字节
        in.readByte();
        // 6. 读取消息长度
        int length = in.readInt();
        // 7. 读取消息内容
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Message message = serializer.deserialize(bytes, Message.getMessageClass(type));
        message.setMessageId(messageId);
        out.add(message);
    }
}
