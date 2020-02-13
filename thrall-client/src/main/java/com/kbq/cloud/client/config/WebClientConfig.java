package com.kbq.cloud.client.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "balancedWebClient")
    @LoadBalanced
    @Qualifier("balancedWebClient")
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }


    @Bean(name = "urlWebClient")
    @Qualifier("urlWebClient")
    public WebClient.Builder loadUrlWebClientBuilder() {
        return WebClient.builder();
    }


    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
