package com.kbq.cloud.core.request;


import com.kbq.cloud.core.pojo.ThrallVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThrallAddRequest {

    private ThrallVo thrallVo;
}
