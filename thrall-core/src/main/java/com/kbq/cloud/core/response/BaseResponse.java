package com.kbq.cloud.core.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {


    private T context;

    private Integer code;

    private String msg;


    public static BaseResponse successful(){
        return BaseResponse.builder().code(200).build();
    }
    public static BaseResponse errorful(){
        return BaseResponse.builder().code(500).build();
    }

    public static BaseResponse success(Object object) {
        return BaseResponse.builder().code(200).context(object).build();
    }

}
