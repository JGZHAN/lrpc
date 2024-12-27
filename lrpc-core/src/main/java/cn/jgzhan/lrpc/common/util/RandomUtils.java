package cn.jgzhan.lrpc.common.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/10
 */
public class RandomUtils {
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
