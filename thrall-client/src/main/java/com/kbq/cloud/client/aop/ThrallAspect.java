package com.kbq.cloud.client.aop;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.kbq.cloud.common.Constant;
import com.kbq.cloud.common.ExceptionUtil;
import com.kbq.cloud.core.pojo.HostMessageVo;
import com.kbq.cloud.core.pojo.MicroServiceVo;
import com.kbq.cloud.core.pojo.ThrallVo;
import com.kbq.cloud.core.request.HostAddRequest;
import com.kbq.cloud.core.request.ThrallAddRequest;
import com.kbq.cloud.core.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Component
@Aspect
@Order(BigDecimal.ROUND_UP)
@Slf4j
public class ThrallAspect {


    @Autowired
    @Qualifier("balancedWebClient")
    private WebClient.Builder balancedWebClient;

    @Autowired
    @Qualifier("urlWebClient")
    private WebClient.Builder urlWebClient;

    @Autowired
    private ThrallConfig thrallConfig;


    @Value(value = "${server.port}")
    private Integer port;

    @Value(value = "${spring.cloud.nacos.config.server-addr:127.0.0.1:8848}")
    private String serverAddr;

    //对全局异常的切面无法获取post参数 尝试对RestController进行aop
    //@Pointcut(value="@annotation(com.kbq.cloud.client.annotation.Thrall)")
    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)")
    public void thrallPointcut() {
    }


    @AfterThrowing(value = "thrallPointcut()", throwing = "e")
    public void afterThrall(JoinPoint joinPoint, Throwable e) {
        //获取request，只有在当前线程获取，stream中获取会NullPointerException
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        Mono.just(thrallConfig).flatMap(config -> {
            if (config.getEnable()) {
                return isBalanced(config).map(bool -> bool ? balancedWebClient : urlWebClient)
                        .zipWith(getThrallUrl(config, Constant.REQUEST_THRALL_URL))
                        .flatMap(tuple2 ->
                                tuple2.getT1().build().post().uri(tuple2.getT2()).body(
                                        Mono.create(sink -> {
                                            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                                            Method targetMethod = methodSignature.getMethod();
                                            ThrallAddRequest addRequest = ThrallAddRequest.builder().thrallVo(
                                                    ThrallVo.builder()
                                                            .reqUrl(request.getRequestURI())
                                                            .reqParam(JSONObject.toJSONString(joinPoint.getArgs()))
                                                            .reqType(request.getMethod())
                                                            .reqHead(getHeadersInfo(request))
                                                            .thrallExceptionMsg(ExceptionUtil.getExceptionSprintStackTrace(e))
                                                            .exceptionClassName(targetMethod.getDeclaringClass().getSimpleName())
                                                            .exceptionMethodName(targetMethod.getName())
                                                            .createTime(LocalDateTime.now())
                                                            .hostMessage(HostMessageVo.builder().host(getHostString()).port(port).build())
                                                            .microService(MicroServiceVo.builder()
                                                                    .microServiceName(config.getApplicationName())
                                                                    .microServiceType(Constant.MICRO_SERVICE.equals(config.getType()) ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_CEILING)
                                                                    .build())
                                                            .build()
                                            ).build();
                                            sink.success(addRequest);
                                        }),
                                        ThrallAddRequest.class)
                                        .retrieve()
                                        .bodyToMono(BaseResponse.class)
                                        .retryBackoff(BigDecimal.ROUND_FLOOR,
                                                Duration.ofSeconds(BigDecimal.ROUND_FLOOR)));
            } else {
                return Mono.empty();
            }
        }).subscribe(baseResponse -> log.info("thrall exception center：{}", JSON.toJSONString(baseResponse)));
    }


    @PostConstruct
    public void register() {
        isBalanced(thrallConfig).flatMap(bool -> {
            if (thrallConfig.getEnable()) {
                if (bool) {
                    return Mono.fromSupplier(() -> {
                        Instance instance = null;
                        try {
                            NamingService namingService = NamingFactory.createNamingService(serverAddr);
                            instance = namingService.selectOneHealthyInstance(thrallConfig.getServiceName());
                        } catch (NacosException e) {
                            e.printStackTrace();
                        }
                        return instance;
                    }).flatMap(instance -> getThrallUrl(ThrallConfig.builder()
                            .type(Constant.COMMON_URL)
                            .baseUrl(Constant.getFullPath(instance.getIp(), instance.getPort()))
                            .build(), Constant.REQUEST_HOST_URL)
                    );
                } else {
                    return getThrallUrl(thrallConfig, Constant.REQUEST_HOST_URL);
                }
            } else {
                return Mono.empty();
            }
        }).flatMap(url ->
                urlWebClient.build().post().uri(url).body(hostBuilder(thrallConfig),
                        HostAddRequest.class).retrieve().bodyToMono(BaseResponse.class)
                        .retryBackoff(BigDecimal.ROUND_FLOOR, Duration.ofSeconds(BigDecimal.ROUND_FLOOR))
        ).subscribe(baseResponse -> log.info("thrall exception center：{}", JSON.toJSONString(baseResponse)));
    }

    private Mono<String> getThrallUrl(ThrallConfig config, String url) {
        return isBalanced(config).map(bool -> {
            String baseUri = bool ? config.getServiceName() : config.getBaseUrl();
            return Constant.HTTP.concat(baseUri).concat(url);
        });
    }


    private Mono<Boolean> isBalanced(ThrallConfig config) {
        return Mono.just(Constant.MICRO_SERVICE.equals(config.getType()));
    }


    private Mono<HostAddRequest> hostBuilder(ThrallConfig config) {
        return isBalanced(config).zipWith(getHost()).map(tuple2 ->
                HostAddRequest.builder()
                        .microServiceVo(MicroServiceVo.builder()
                                .microServiceName(config.getApplicationName())
                                .microServiceType(tuple2.getT1() ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_CEILING)
                                .build())
                        .hostMessageVo(HostMessageVo.builder()
                                .host(tuple2.getT2())
                                .port(port)
                                .build())
                        .build()
        );
    }

    private Mono<String> getHost() {
        try {
            return Mono.just(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            return Mono.just(Constant.UNKNOWN);
        }
    }

    private String getHostString() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return Constant.UNKNOWN;
        }
    }


    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

}

