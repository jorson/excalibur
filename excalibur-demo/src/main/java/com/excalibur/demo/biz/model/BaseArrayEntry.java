package com.excalibur.demo.biz.model;

import java.util.List;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class BaseArrayEntry<T> {

    private String success;

    private List<T> result;

    public String getSuccess() {
        return success;
    }

    public List<T> getResult() {
        return result;
    }
}
