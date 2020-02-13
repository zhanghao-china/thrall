package com.kbq.cloud.server.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kbq.cloud.common.Constant;
import com.kbq.cloud.common.CustomLocalDateTimeDeserializer;
import com.kbq.cloud.common.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HostMessageDto {

    @Id
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
     * 微服务的服务id
     */
    private String microServiceId;
    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    public String getUrl() {
        if (StringUtils.isEmpty(host) || Objects.isNull(port))
            return null;
        return host.concat(":").concat(String.valueOf(port));
    }

    public String getTopicKey() {
        if (StringUtils.isNotEmpty(getUrl())) {
            return Constant.getListenerKey(getUrl());
        }
        throw new RuntimeException("参数缺失!");
    }
}
