package com.excalibur.demo.biz.base;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class Protocol {

    public static final String WEATHER_API_URL = "http://api.k780.com:88";
    public static final String APP_KEY         = "10003";
    public static final String SIGN            = "b59bc3ef6191eb9f747dd4e83c99f2a4";
    public static final String DATA_FORMAT     = "json";

    public static class Field {
        public static final String APP     = "app";
        public static final String WEA_ID  = "weaid";
        public static final String APP_KEY = "appkey";
        public static final String SIGN    = "sign";
        public static final String FORMAT  = "format";
    }

    public static class Command {
        public static final String WEATHER_CITY   = "weather.city";
        public static final String WEATHER_FUTURE = "weather.future";
    }

}
