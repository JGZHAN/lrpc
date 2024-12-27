package cn.jgzhan.lrpc.example.common.serializer;

import com.alibaba.fastjson2.JSONReader;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public enum SerializerEnum implements Serializer {
    JSON {
        @Override
        public byte[] serialize(Object object) {
            return com.alibaba.fastjson2.JSON.toJSONBytes(object);
        }

        @Override
        public <T> T deserialize(byte[] bytes, Class<T> clazz) {

            JSONReader.Feature[] features = new JSONReader.Feature[]{JSONReader.Feature.SupportClassForName};
            return com.alibaba.fastjson2.JSON.parseObject(bytes, clazz, features);
        }
    }
}
