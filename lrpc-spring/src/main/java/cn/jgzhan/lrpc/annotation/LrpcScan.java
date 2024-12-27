package cn.jgzhan.lrpc.annotation;

import cn.jgzhan.lrpc.spring.ServiceScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(ServiceScannerRegistrar.class)
@Target(ElementType.TYPE)
public @interface LrpcScan {
    String[] basePackages() default {};
}
