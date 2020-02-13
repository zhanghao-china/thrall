package com.kbq.cloud.core.request;

import com.kbq.cloud.core.pojo.HostMessageVo;
import com.kbq.cloud.core.pojo.MicroServiceVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostAddRequest {

    private HostMessageVo hostMessageVo;

    private MicroServiceVo microServiceVo;
}
