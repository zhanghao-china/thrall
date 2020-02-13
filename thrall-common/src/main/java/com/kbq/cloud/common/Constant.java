package com.kbq.cloud.common;

public class Constant {
    private Constant(){}

    public static final String HTTP = "http://";

    public static final String REQUEST_THRALL_URL = "/thrall";

    public static final String REQUEST_HOST_URL = "/thrall/host";

    public static final String MICRO_SERVICE = "micro-service";

    public static final String COMMON_URL = "common-url";

    public static final String UNKNOWN = "Unknown";

    public static final String LISTENER_TOPICS = "REDIS:TOPICS:";

    public static final String HOST_SUCCESS_MSG = "服务注册成功！！！";

    public static final String HOST_ALREADY_MSG = "该服务已经注册！！！";

    public static final String THRALL_EXCEPTION_PUSH_SUCCESS  = "异常上报成功,并成功推送";

    public static final String THRALL_EXCEPTION_PUSH_NOTOPIC  = "异常上报成功,并成功推送";

    public static final String INTERNAL_SERVER_ERROR_MSG = "服务器出错啦！！";

    public static String getListenerKey(String url) {
        return LISTENER_TOPICS.concat(url);
    }

    public static String getFullPath(String ip, int port) {
        return ip.concat(":").concat(String.valueOf(port));
    }
}
