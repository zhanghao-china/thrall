package com.kbq.cloud.server.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.listener.Topic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public abstract class BaseService<T> {


    @Autowired
    @Qualifier(value = "redisTemplate")
    private ReactiveRedisTemplate<String,T> redisTemplate;

//    @Autowired
//    ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer;

    /**
     * 新增入redis
     * @param key
     * @param value
     * @return
     */
    public Mono<Boolean> addToRedis(String key, T value, Duration timeout) {
        return Objects.isNull(timeout) ? redisTemplate.opsForValue().set(key, value)
                : redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 新增入redis
     * @param key
     * @param value
     * @return
     */
    public Mono<Long> addListToRedis(String key, List<T> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }



    /**
     * 新增入redis,
     * 如果key不存在则添加，否则不做操作。等同于redis的setNX
     * @param key
     * @param value
     * @return
     */
    protected Mono<Boolean> addToRedisForIfAbsent(String key, T value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 根据key查询reids
     * @param key
     * @return
     * @throws Exception
     */
    public Mono<T> getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据key查询reids
     * @param key
     * @return
     * @throws Exception
     */
    public Flux<T> getListFromRedis(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 根据key判断值是否存在
     * @param key
     * @return
     * @throws Exception
     */
    protected Mono<Boolean> hasKey(String key){
        return redisTemplate.hasKey(key);
    }


    /**
     * 根据key删除
     * @param key
     * @throws Exception
     */
    public Mono<Long> remove(String key){
        return redisTemplate.delete(key);
    }
    /**
     * 查询匹配的Key
     * @param pattern
     * @return
     */
    public Flux<String> getKeys(String pattern) {
        return redisTemplate.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match("*".concat(pattern)).build());
    }

    /**
     * 发消息
     * @param topicName
     * @param t
     * @return
     */
    protected Mono<Long> pub(String topicName, T t) {
         return redisTemplate.convertAndSend(topicName, t);
    }

    /**
     * 监听
     * @param topics
     * @return
     */
    protected Flux<T> sub(Topic... topics) {
        return redisTemplate.listenTo(topics).map(ReactiveSubscription.Message::getMessage);
    }

    /**
     * 监听
     * @param topics
     * @return
     */
//    protected Flux<T> sub(List<ChannelTopic> topics) {
//        return reactiveRedisMessageListenerContainer
//                .receive(topics, redisTemplate.getSerializationContext().getStringSerializationPair(),
//                        redisTemplate.getSerializationContext().getValueSerializationPair())
//                .map(ReactiveSubscription.Message::getMessage);
//    }


}
