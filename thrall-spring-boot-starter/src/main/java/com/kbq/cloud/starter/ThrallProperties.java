package com.kbq.cloud.starter;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.cloud.thrall")
@RefreshScope
public class ThrallProperties {

    @Value(value = "${spring.cloud.thrall.serviceName:thrall-exception-center}")
    private String serviceName;

    @Value(value = "${spring.cloud.thrall.baseUrl:127.0.0.1:9880}")
    private String baseUrl;

    @Value(value = "${spring.cloud.thrall.enable:false}")
    private Boolean enable;

    @Value(value = "${spring.cloud.thrall.type:micro-service}")
    private String type;

    @Value(value="${spring.application.name:unknown}")
    @Deprecated
    private String applicationName;
}
