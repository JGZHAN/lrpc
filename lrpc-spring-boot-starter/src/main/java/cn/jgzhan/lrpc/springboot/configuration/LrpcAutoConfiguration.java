package cn.jgzhan.lrpc.springboot.configuration;

import cn.jgzhan.lrpc.common.config.LrpcProperties;
import cn.jgzhan.lrpc.springboot.properties.LrpcPropertiesSpringBoot;import cn.jgzhan.lrpc.springboot.spring.ReferenceBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/17
 */
@ConditionalOnClass({LrpcPropertiesSpringBoot.class})
@EnableConfigurationProperties({LrpcPropertiesSpringBoot.class})
@AutoConfiguration
public class LrpcAutoConfiguration {

    @Autowired
    private LrpcProperties lrpcPropertiesSpringBoot;

    @Bean
    public ReferenceBeanPostProcessor referenceBeanPostProcessor() {
        return new ReferenceBeanPostProcessor(lrpcPropertiesSpringBoot);
    }


}
