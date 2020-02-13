package com.kbq.cloud.server.config;


import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {


    @Bean(name = "redisTemplate")
    @Qualifier(value = "redisTemplate")
    public ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        ReactiveRedisTemplate reactiveRedisTemplate = new ReactiveRedisTemplate<>(factory,
                RedisSerializationContext.fromSerializer(fastJsonRedisSerializer));
        return reactiveRedisTemplate;
    }

    @Bean
    public ParserConfig parserConfig() {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.setAutoTypeSupport(true);
        return parserConfig;
    }

//    @Bean
//    public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer(ReactiveRedisConnectionFactory factory){
//        return  new ReactiveRedisMessageListenerContainer(factory);
//    }



}