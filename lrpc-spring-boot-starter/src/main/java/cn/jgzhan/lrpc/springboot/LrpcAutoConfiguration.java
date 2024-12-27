package cn.jgzhan.spring;

import cn.jgzhan.lrpc.spring.ReferenceBeanPostProcessor;
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
@ConditionalOnClass({LrpcProperties.class})
@EnableConfigurationProperties({LrpcProperties.class})
@AutoConfiguration
public class LrpcAutoConfiguration {

    @Autowired
    private LrpcProperties lrpcProperties;

    @Bean
    public ReferenceBeanPostProcessor referenceBeanPostProcessor() {
        return new ReferenceBeanPostProcessor(lrpcProperties);
    }


}
