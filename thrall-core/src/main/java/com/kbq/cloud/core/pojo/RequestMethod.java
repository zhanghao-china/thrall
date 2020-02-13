package com.kbq.cloud.core.pojo;

import java.math.BigDecimal;

public enum RequestMethod {

    GET("GET", BigDecimal.ROUND_UP),

    HEAD("HEAD", BigDecimal.ROUND_DOWN),

    POST("POST",BigDecimal.ROUND_CEILING),

    PUT("PUT",BigDecimal.ROUND_FLOOR),

    PATCH("PATCH",BigDecimal.ROUND_HALF_UP),

    DELETE("DELETE",BigDecimal.ROUND_HALF_DOWN),

    OPTIONS("OPTIONS",BigDecimal.ROUND_HALF_EVEN),

    TRACE("TRACE",BigDecimal.ROUND_UNNECESSARY);

    RequestMethod(String name, int id) {
        _name = name;
        _id = id;
    }


    private String _name;

    private int _id;

    public String getName() {
        return _name;
    }

    public int getId() {
        return _id;
    }

    public static RequestMethod getRequestMethod(String name) {
        switch (name) {
            case "GET":
                return RequestMethod.GET;
            case "HEAD":
                return RequestMethod.HEAD;
            case "POST":
                return RequestMethod.POST;
            case "PUT":
                return RequestMethod.PUT;
            case "PATCH":
                return RequestMethod.PATCH;
            case "DELETE":
                return RequestMethod.DELETE;
            case "OPTIONS":
                return RequestMethod.OPTIONS;
            case "TRACE":
                return RequestMethod.TRACE;
            default:
                return null;
        }
    }
}
