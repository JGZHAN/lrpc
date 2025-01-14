package cn.jgzhan.lrpc.springboot.annotation;

import java.lang.annotation.*;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LrpcService {
    String serviceName() default "";
}
