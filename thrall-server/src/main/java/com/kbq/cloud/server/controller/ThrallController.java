package com.kbq.cloud.server.controller;

import com.alibaba.fastjson.JSON;
import com.kbq.cloud.common.Constant;
import com.kbq.cloud.core.request.ExecuteQueryRequest;
import com.kbq.cloud.core.request.HostAddRequest;
import com.kbq.cloud.core.request.ThrallAddRequest;
import com.kbq.cloud.core.request.ThrallQueryRequest;
import com.kbq.cloud.core.response.BaseResponse;
import com.kbq.cloud.server.service.ThrallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ThrallController {

    @Autowired
    ThrallService thrallService;

    @PostMapping(Constant.REQUEST_THRALL_URL)
    public Mono<BaseResponse> addThrall(@RequestBody ThrallAddRequest thrallAddRequest) {
        return thrallService.addThrall(thrallAddRequest.getThrallVo()).log();
    }

    @PostMapping(Constant.REQUEST_HOST_URL)
    public Mono<BaseResponse> addHost(@RequestBody HostAddRequest hostAddRequest) {
        return thrallService.addHost(hostAddRequest);
    }


    @GetMapping(value = "/thrall/sse/{connectionId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse(@PathVariable String connectionId) {
        return thrallService.sseListener(connectionId).map(JSON::toJSONString);
    }


    @PostMapping("/thrall/page")
    public Mono<BaseResponse> findThrallPage(@RequestBody ThrallQueryRequest request) {
        return thrallService.findThrallPage(request);
    }

    @GetMapping("/thrall/micro-service")
    public Mono<BaseResponse> findMicroService(){
        return thrallService.findMicroService();
    }

    @PostMapping("/thrall/execute")
    public Mono<BaseResponse> execute(@RequestBody ExecuteQueryRequest executeQueryRequest) {
        return  thrallService.execute(executeQueryRequest);
    }
}
