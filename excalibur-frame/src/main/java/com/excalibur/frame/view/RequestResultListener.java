package com.excalibur.frame.view;

import android.os.Bundle;
import com.excalibur.frame.base.Request;

/**
 * Date: 13-5-19
 */
public interface RequestResultListener {

    public void afterRequest(Request request);

    public void onRequestSuccess(Request request, Bundle resultData);

    public void onRequestFailure(Request request, Bundle resultData);

}
