package com.kbq.cloud.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MicroServiceVo {

    private String id;
    /**
     * 微服务名称
     */
    private String microServiceName;

    /**
     * 微服务类型  1-已注册的服务  2-未注册的服务
     */
    private Integer microServiceType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
