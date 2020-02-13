package com.kbq.cloud.core.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteQueryRequest {

    private String reqHead;

    private String url;

    private String reqType;

    private String reqParam;
}
