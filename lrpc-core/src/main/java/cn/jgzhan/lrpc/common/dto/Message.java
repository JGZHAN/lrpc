package cn.jgzhan.lrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer messageId;

    private byte type;

    public static Class<? extends Message> getMessageClass(byte type) {
        return MESSAGE_MAP.get(type);
    }

    private static final HashMap<Byte, Class<? extends Message>> MESSAGE_MAP = new HashMap<>();

    static {
        MESSAGE_MAP.put((byte) 0, Message.class);
        MESSAGE_MAP.put((byte) 1, RpcRequestMessage.class);
        MESSAGE_MAP.put((byte) 2, RpcResponseMessage.class);
    }


}
