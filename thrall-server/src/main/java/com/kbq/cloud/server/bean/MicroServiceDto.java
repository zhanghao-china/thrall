package com.kbq.cloud.server.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kbq.cloud.common.CustomLocalDateTimeDeserializer;
import com.kbq.cloud.common.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MicroServiceDto {

    @Id
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
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

}
