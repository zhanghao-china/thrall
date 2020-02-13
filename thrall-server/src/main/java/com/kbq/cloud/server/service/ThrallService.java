package com.kbq.cloud.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kbq.cloud.common.Constant;
import com.kbq.cloud.core.pojo.MicroServiceVo;
import com.kbq.cloud.core.pojo.RequestMethod;
import com.kbq.cloud.core.pojo.ThrallVo;
import com.kbq.cloud.core.request.ExecuteQueryRequest;
import com.kbq.cloud.core.request.HostAddRequest;
import com.kbq.cloud.core.request.ThrallQueryRequest;
import com.kbq.cloud.core.response.BaseResponse;
import com.kbq.cloud.server.bean.HostMessageDto;
import com.kbq.cloud.server.bean.MicroServiceDto;
import com.kbq.cloud.server.bean.ThrallDto;
import com.kbq.cloud.server.dao.HostDao;
import com.kbq.cloud.server.dao.MicroServiceDao;
import com.kbq.cloud.server.dao.ThrallDao;
import com.kbq.cloud.server.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ThrallService extends BaseService<ThrallDto> {

    @Autowired
    ThrallDao thrallDao;

    @Autowired
    MicroServiceDao microServiceDao;

    @Autowired
    HostDao hostDao;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    RestTemplate restTemplate;




    public Mono<BaseResponse> addThrall(ThrallVo thrallVo){
        Mono<ThrallDto> thrallDtoMono = Mono.just(thrallVo).map(vo -> {
            String jsonString = JSON.toJSONString(vo);
            return JSON.parseObject(jsonString, ThrallDto.class);
        });

        Mono<List<String>> topics = getKeys(Constant.LISTENER_TOPICS.concat("*")).distinct().collectList();
        Mono<MicroServiceDto> microServiceDtoMono = thrallDtoMono
                .flatMap(dto -> microServiceDao.findByMicroServiceName(dto.getMicroService().getMicroServiceName()));
        Mono<HostMessageDto> hostMessageDtoMono =
                thrallDtoMono.flatMap(dto -> hostDao.findOne(Example.of(dto.getHostMessage())));
        return Mono.zip(thrallDtoMono,microServiceDtoMono,hostMessageDtoMono)
                .map(tuple3 -> {
                    ThrallDto thrall = tuple3.getT1();
                    thrall.setMicroService(tuple3.getT2());
                    thrall.setHostMessage(tuple3.getT3());
                    return thrall;
                })
                .flatMap(dto -> thrallDao.save(dto))
                .zipWith(topics)
                .doOnNext(tuple2 ->
                    tuple2.getT2().parallelStream().forEach(topic -> pub(topic,tuple2.getT1()).subscribe())
                ).map(tuple2 -> BaseResponse.builder().code(HttpStatus.OK.value()).msg(Constant.THRALL_EXCEPTION_PUSH_SUCCESS).build());
    }


    public Flux<ThrallVo> sseListener(String connectionId){
        return addToRedis(Constant.LISTENER_TOPICS.concat(connectionId),ThrallDto.builder().id(connectionId).build(),
                Duration.ofHours(BigDecimal.ROUND_DOWN))
        .map(bool ->  bool ? ChannelTopic.of(Constant.LISTENER_TOPICS.concat(connectionId))
                : null)
        .flatMapMany(this::sub).map(dto -> {
                    String jsonString = JSON.toJSONString(dto);
                    return JSON.parseObject(jsonString, ThrallVo.class);
        }).mergeWith(Flux.interval(Duration.ofSeconds(BigDecimal.ROUND_HALF_DOWN))
                        .map(aLong -> ThrallVo.builder().createTime(LocalDateTime.now()).build()))
                .retryBackoff(BigDecimal.ROUND_FLOOR,Duration.ofSeconds(BigDecimal.ROUND_FLOOR))
                .doFinally(vo -> remove(Constant.LISTENER_TOPICS.concat(connectionId)).retry(BigDecimal.ROUND_FLOOR).subscribe());
    }

    public Mono<BaseResponse> addHost(HostAddRequest addRequest) {
        Mono<MicroServiceDto> microServiceDtoMono = Mono.just(addRequest).map(req -> {
            String jsonString = JSON.toJSONString(req.getMicroServiceVo());
            return JSON.parseObject(jsonString, MicroServiceDto.class);
        });

        Mono<HostMessageDto> hostMessageDtoMono = Mono.just(addRequest).map(req -> {
            String jsonString = JSON.toJSONString(req.getHostMessageVo());
            return JSON.parseObject(jsonString, HostMessageDto.class);
        });

        Mono<Boolean> hostExist = hostMessageDtoMono.flatMap(hostDto -> hostDao.count(Example.of(hostDto)))
                .map(aLong -> aLong > BigDecimal.ROUND_UP);

        return microServiceDtoMono
                .flatMap(microDto ->
                        microServiceDao.exists(Example.of(microDto)))
                .zipWith(microServiceDtoMono)
                .flatMap(tuple2 -> {
                    if (!tuple2.getT1()) {
                        MicroServiceDto microServiceDto = tuple2.getT2();
                        microServiceDto.setCreateTime(LocalDateTime.now());
                        return microServiceDao.save(microServiceDto);
                    }
                    return microServiceDao.findByMicroServiceName(tuple2.getT2().getMicroServiceName());
                }).zipWith(hostMessageDtoMono)
                .map(tuple2 -> {
                    String microId = tuple2.getT1().getId();
                    HostMessageDto hostMessageDto = tuple2.getT2();
                    hostMessageDto.setMicroServiceId(microId);
                    return hostMessageDto;
                }).zipWith(hostExist)
                .flatMap(tuple2 -> {
                    if (!tuple2.getT2()) {
                        HostMessageDto hostMessageDto = tuple2.getT1();
                        hostMessageDto.setCreateTime(LocalDateTime.now());
                        return hostDao.save(hostMessageDto);
                    }
                    return Mono.just(HostMessageDto.builder().build());
                }).map(dto -> {
                    if (StringUtils.isEmpty(dto.getId())) {
                        return BaseResponse.builder().code(HttpStatus.OK.value()).msg(Constant.HOST_ALREADY_MSG).build();
                    }
                    return BaseResponse.builder().code(HttpStatus.OK.value()).msg(Constant.HOST_SUCCESS_MSG).build();
                });
    }


    public Mono<BaseResponse> findThrallPage(ThrallQueryRequest request) {
        Mono<PageRequest> pageRequest = Mono.just(PageRequest.of(request.getPageNow(),
                request.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
        Mono<ThrallDto> dtoMono =
                Mono.justOrEmpty(request.getThrall()).defaultIfEmpty(ThrallVo.builder().build()).map(vo -> {
            String jsonString = JSON.toJSONString(vo);
            return JSON.parseObject(jsonString, ThrallDto.class);
        });
        Mono<Long> total = dtoMono.flatMap(dto -> reactiveMongoTemplate.count(Query.query(dto.getWhereCriteria()),
                ThrallDto.class));
        Mono<List<ThrallDto>> listMono = dtoMono.zipWith(pageRequest)
                .flatMapMany(tuple2 ->
                        reactiveMongoTemplate.find(Query.query(tuple2.getT1().getWhereCriteria()).with(tuple2.getT2()),
                                ThrallDto.class))
                .collectList();
        return Mono.zip(listMono,pageRequest,total).map(tuple3 -> BaseResponse.builder().context(
                new PageImpl(tuple3.getT1(), tuple3.getT2(), tuple3.getT3())).code(HttpStatus.OK.value()).build());
    }


    public Mono<BaseResponse> findMicroService() {
        return microServiceDao.findAll().map(dto -> {
            String jsonString = JSON.toJSONString(dto);
            return JSON.parseObject(jsonString, MicroServiceVo.class);
        }).collectList().map(list ->
             BaseResponse.builder().context(list).code(HttpStatus.OK.value()).build()
        );
    }

    public Mono<BaseResponse> execute(ExecuteQueryRequest executeQueryRequest) {
        return Mono.fromSupplier(() -> {
            MultiValueMap<String,String> httpHeaders = new HttpHeaders();
            Map<String, String> headers = JSON.parseObject(executeQueryRequest.getReqHead(), Map.class);
            if (!ObjectUtils.isEmpty(headers)) {
                headers.keySet().parallelStream().forEach(key -> httpHeaders.addIfAbsent(key, headers.get(key)));
            }
            return httpHeaders;
        }).map(headers -> {
            RequestMethod requestMethod = RequestMethod.getRequestMethod(executeQueryRequest.getReqType());
            JSONArray jsonArray = JSON.parseArray(executeQueryRequest.getReqParam());
            String param = jsonArray.size() > BigDecimal.ROUND_UP ? jsonArray.getString(BigDecimal.ROUND_UP) : null;
            HttpEntity<String> httpEntity = new HttpEntity<>(param,headers);
            ResponseEntity<Object> responseEntity;
            switch (requestMethod) {
                case GET:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.GET,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case HEAD:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.HEAD,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case POST:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.POST,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case PUT:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.PUT,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case PATCH:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.PATCH,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case DELETE:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.DELETE,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case OPTIONS:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.OPTIONS,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                case TRACE:
                    responseEntity = restTemplate.exchange(executeQueryRequest.getUrl(),
                            HttpMethod.OPTIONS,
                            httpEntity,
                            Object.class);
                    return BaseResponse.success(responseEntity.getBody());
                default:
                   return null;
            }
        });
    }
}
