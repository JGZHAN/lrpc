package cn.jgzhan.lrpc.common.serializer;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public interface Serializer {
    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
