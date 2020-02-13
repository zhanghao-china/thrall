package com.kbq.cloud.server.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kbq.cloud.common.CustomLocalDateTimeDeserializer;
import com.kbq.cloud.common.CustomLocalDateTimeSerializer;
import com.kbq.cloud.core.pojo.HostMessageVo;
import com.kbq.cloud.core.pojo.MicroServiceVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ThrallDto {

    @Id
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


    private HostMessageDto hostMessage;


    private MicroServiceDto microService;

    @Transient
    private String startTime;

    @Transient
    private String endTime;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    public Criteria getWhereCriteria(){
        List<Criteria> criteriaList = this.getCommonCriteria();
        if(CollectionUtils.isEmpty(criteriaList)){
            return new Criteria();
        }
        return new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    private List<Criteria> getCommonCriteria() {
        List<Criteria> criteriaList = new ArrayList<>();
        if (Objects.nonNull(microService) && StringUtils.isNotEmpty(microService.getId())) {
            criteriaList.add(Criteria.where("microService.id").is(microService.getId()));
        }
        if (StringUtils.isNotEmpty(startTime)) {
            criteriaList.add(Criteria.where("createTime").gte(LocalDate.parse(startTime,
                    DateTimeFormatter.ISO_LOCAL_DATE)));
        }
        if (StringUtils.isNotEmpty(endTime)) {
            criteriaList.add(Criteria.where("createTime").lt(LocalDate.parse(endTime,
                    DateTimeFormatter.ISO_LOCAL_DATE).plusDays(BigDecimal.ROUND_DOWN)));
        }
        return criteriaList;
    }

}
