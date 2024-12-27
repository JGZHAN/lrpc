package cn.jgzhan.lrpc.common.util;

import cn.jgzhan.lrpc.common.dto.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/18
 */
public class AddressUtils {

    public static Set<Pair<String, Integer>> toAddressPair(String[] addressArr) {
        return Optional.ofNullable(addressArr)
                .map(addresses -> Arrays.stream(addresses)
                        .map(AddressUtils::toAddressPair)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    public static Pair<String, Integer> toAddressPair(String address) {
        final String[] split = address.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("地址格式错误, 格式为: ip:port");
        }
        return new Pair<>(split[0], Integer.parseInt(split[1]));
    }
}
