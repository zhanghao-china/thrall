package com.kbq.cloud.server.dao;

import com.kbq.cloud.server.bean.ThrallDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrallDao extends ReactiveMongoRepository<ThrallDto,String> {



}
