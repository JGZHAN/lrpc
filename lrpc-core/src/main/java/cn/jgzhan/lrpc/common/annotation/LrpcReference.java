package cn.jgzhan.lrpc.common.annotation;

import java.lang.annotation.*;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LrpcReference {


    // 格式：{ip:port, ip:port, ...}
    String[] addressArr() default {};
}
