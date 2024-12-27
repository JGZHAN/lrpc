package cn.jgzhan.lrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/4
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RpcRequestMessage extends Message {
    {
        setType((byte) 1);
    }
    // 接口名称
    private String interfaceName;

    // 方法名称
    private String methodName;

    // 返回参数类型
    private Class<?> returnType;

    // 参数类型数组
    private Class<?>[] parameterTypes;

    // 参数值数组
    private Object[] parameterValues;

}
