package com.kbq.cloud.server.handler;

import com.kbq.cloud.common.Constant;
import com.kbq.cloud.core.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;


@ControllerAdvice
public class GlobalException {


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Mono<BaseResponse> exceptionHandler(ServerHttpRequest req, Exception ex) {
        ex.printStackTrace();
        return Mono.just(BaseResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).msg(Constant.INTERNAL_SERVER_ERROR_MSG).build());
    }
}
