package com.excalibur.frame.base;

/**
 * RequestConstants
 * User: Twinkle
 * Date: 13-9-22
 * Time: 下午6:32
 */
public interface RequestConstants {
    public static final String RECEIVER_EXTRA_ERROR_TYPE                   =
            "request.extra.error";
    public static final String RECEIVER_EXTRA_CONNECTION_ERROR_STATUS_CODE =
            "request.extra.connectionErrorStatusCode";

    public static final int ERROR_TYPE_CONNEXION = 1;
    public static final int ERROR_TYPE_DATA      = 2;
    public static final int ERROR_TYPE_CUSTOM    = 3;

    public static final String BUNDLE_KEY_CODE    = "code";
    public static final String BUNDLE_KEY_MESSAGE = "message";

    public static final String MSG_CONNECT_ERROR     = "网络连接失败，请重试";
    public static final String MSG_DATA_FORMAT_ERROR = "服务端返回数据解析失败，请重试";
}
