package com.excalibur.demo.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class City implements Serializable{

    @JsonProperty("weaid")
    private String weatherId;
    @JsonProperty("citynm")
    private String cityName;
    @JsonProperty("cityno")
    private String cityNo;
    @JsonProperty("cityid")
    private String cityId;

    public String getWeatherId() {
        return weatherId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityNo() {
        return cityNo;
    }

    public String getCityId() {
        return cityId;
    }
}
