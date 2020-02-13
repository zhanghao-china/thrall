package com.kbq.cloud.server.dao;

import com.kbq.cloud.server.bean.MicroServiceDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MicroServiceDao extends ReactiveMongoRepository<MicroServiceDto,String> {

    @Query
    Mono<MicroServiceDto> findByMicroServiceName(String microServiceName);


}

