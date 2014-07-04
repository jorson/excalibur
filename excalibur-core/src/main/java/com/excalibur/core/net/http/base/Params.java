package com.excalibur.core.net.http.base;

import android.text.TextUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 14-5-21
 */

public class Params extends ArrayList<NameValuePair> {

    private static final long serialVersionUID = -5300300632230989213L;

    public static String appendToUrl(Params params, String url) {
        return null == params ? url : (url + "?" + params);
    }

    public Params put(String key, Object value) {
        if (!TextUtils.isEmpty(key) && null != value) {
            add(new BasicNameValuePair(key, String.valueOf(value)));
        }
        return this;
    }

    public void insert(int index, String key, Object value) {
        if (!TextUtils.isEmpty(key) && null != value) {
            add(index, new BasicNameValuePair(key, String.valueOf(value)));
        }
    }

    public int indexOfByKey(int startIndex, String key) {
        for (int i = startIndex; i < size(); i++) {
            NameValuePair pair = get(i);
            if (key.equals(pair.getName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @deprecated @see {@link #appendToUrl(Params, String)}
     */
    public String appendToUrl(String url) {
        return url + "?" + this;
    }

    @Override
    public String toString() {
        return genNameValuePairs(this);
    }

    public static String genNameValuePairs(List<? extends NameValuePair> params) {
        if (params.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (NameValuePair pair : params) {
            builder.append("&").append(pair.getName()).append("=").append(pair.getValue());
        }
        return builder.toString().substring(1);
    }

}