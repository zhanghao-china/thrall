package com.kbq.cloud.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "urlWebClient")
    public WebClient.Builder loadUrlWebClientBuilder() {
        return WebClient.builder();
    }

}
