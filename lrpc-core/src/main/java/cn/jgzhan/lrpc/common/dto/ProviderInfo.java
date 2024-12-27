package cn.jgzhan.lrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Provider {

    private Pair<String/*host*/, Integer/*port*/> address;

    private String serviceName;
}
