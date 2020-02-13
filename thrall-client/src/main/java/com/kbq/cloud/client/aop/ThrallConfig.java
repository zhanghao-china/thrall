package com.kbq.cloud.client.aop;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ThrallConfig {

    private String serviceName;

    private String baseUrl;

    private Boolean enable;

    private String type;

    private String applicationName;

}
