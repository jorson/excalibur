package com.excalibur.demo.view.base;

import android.os.Bundle;
import com.nd.hy.android.frame.base.Request;
import com.nd.hy.android.frame.view.RequestProxy;
import com.nd.hy.android.frame.view.RequestResultListener;
import com.nd.hy.android.frame.view.SweetActivity;
import com.nd.hy.android.hermes.demo.service.AppRequestManager;

/**
 * @author Yangz
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
