package com.kbq.cloud.server.dao;

import com.kbq.cloud.server.bean.HostMessageDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostDao extends ReactiveMongoRepository<HostMessageDto,String> {
}
