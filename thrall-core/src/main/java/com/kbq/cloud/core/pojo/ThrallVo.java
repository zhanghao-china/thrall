package com.kbq.cloud.core.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ThrallVo {

    private String id;

    /**
     * 请求的服务路径
     */
    private String reqUrl;

    /**
     * 获取请求参数
     */
    private String reqParam;


    /**
     * 获取请求类型
     */
    private String reqType;

    /**
     * 获取请求头信息
     */
    private Map<String,String> reqHead;

    /**
     * 异常信息
     */
    private String thrallExceptionMsg;


    private String exceptionClassName;


    private String exceptionMethodName;


    private HostMessageVo hostMessage;


    private MicroServiceVo microService;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private String startTime;

    private String endTime;
}
