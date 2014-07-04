package com.excalibur.demo.view.base;

import android.os.Bundle;
import com.excalibur.frame.base.Request;
import com.excalibur.frame.view.RequestProxy;
import com.excalibur.frame.view.RequestResultListener;
import com.excalibur.frame.view.SweetActivity;
import com.excalibur.demo.service.AppRequestManager;

/**
 * @version 14-5-20
 */
public abstract class BaseActivity extends SweetActivity implements RequestResultListener {

    @Override
    protected RequestProxy createRequestProxy() {
        return new RequestProxy(AppRequestManager.from(this), this);
    }

    @Override
    public void onRequestFailure(Request request, Bundle resultData) {
        // do nothing
    }

    @Override
    public void onRequestSuccess(Request request, Bundle resultData) {
        // do nothing
    }

    @Override
    public void afterRequest(Request request) {
        // do nothing
    }
}
