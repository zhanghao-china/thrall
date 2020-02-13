package com.kbq.cloud.core.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HostMessageVo {

    private String id;

    /**
     * 主机ip
     */
    private String host;


    /**
     * 访问端口
     */
    private Integer port;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;




}
