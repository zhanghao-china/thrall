package com.kbq.cloud.starter;

import com.kbq.cloud.client.aop.ThrallConfig;
import lombok.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.kbq.cloud"})
@Configuration
@EnableConfigurationProperties(ThrallProperties.class)
@RefreshScope
public class ThrallAutoConfiguration {


    @Bean
    public ThrallConfig getConfig(@NonNull ThrallProperties thrallProperties) {
        return ThrallConfig.builder()
                .baseUrl(thrallProperties.getBaseUrl())
                .serviceName(thrallProperties.getServiceName())
                .type(thrallProperties.getType())
                .enable(thrallProperties.getEnable())
                .applicationName(thrallProperties.getApplicationName())
                .build();
    }


}
