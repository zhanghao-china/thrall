//package com.kbq.cloud.server.filter;
//
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//
//@Component
//public class ThrallFilter implements WebFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//            ServerHttpResponse response = exchange.getResponse();
//            System.out.println(response.getStatusCode());
//        }));
//    }
//
//}
