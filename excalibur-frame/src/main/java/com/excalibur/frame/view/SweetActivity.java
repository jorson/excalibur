package com.excalibur.frame.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.excalibur.core.bus.EventBus;
import com.excalibur.core.data.RestoreUtil;
import com.excalibur.core.platform.App;
import com.excalibur.core.util.Ln;
import com.excalibur.core.view.inject.ViewInjectUtils;
import com.excalibur.frame.base.Request;

/**
 * SweetActivity
 *
 *         Date: 13-5-15
 */
public abstract class SweetActivity extends FragmentActivity {

    private RequestProxy mRequestProxy;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        app.registerActivity(this);

        onBaseCreate(savedInstanceState);
        initView(savedInstanceState);
        onBindView(savedInstanceState);
    }

    /**
     * 必须在此设置一个ContentView，除非它没有界面
     *
     * @param savedInstanceState
     */
    protected abstract void onBaseCreate(Bundle savedInstanceState);

    /**
     * 视图初始化
     * <p/>
     * 处理手势绑定、view和fragment的注入
     *
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {
        if (mRequestProxy == null) {
            mRequestProxy = createRequestProxy();
        }
        if (mRequestProxy != null) {
            mRequestProxy.loadInstanceState(savedInstanceState);
        }

        // inject views
        ViewInjectUtils.inject(this);
        RestoreUtil.loadState(savedInstanceState == null ?
                getIntent().getExtras() : savedInstanceState, this);
    }

    /**
     * 在此处理视图逻辑的绑定
     *
     * @param savedInstanceState
     */
    protected abstract void onBindView(Bundle savedInstanceState);

    /**
     * RequestProxy需要应用中的实际RequestManager去创建，这里做了延迟
     *
     * @return
     */
    protected abstract RequestProxy createRequestProxy();

    /**
     * 参考{@link RequestProxy}
     *
     * @return
     */
    protected RequestProxy getRequestProxy() {
        return mRequestProxy;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getViewById(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRequestProxy != null) {
            mRequestProxy.saveInstanceState(outState);
        }
        RestoreUtil.saveState(outState, this);
    }

    /**
     * 注册Even tBus
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestProxy != null) {
            mRequestProxy.loadRequestList();
        }
        EventBus.registerAnnotatedReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestProxy != null) {
            mRequestProxy.removeRequestsLister();
        }
        EventBus.unregisterAnnotatedReceiver(this);
    }

    protected void sendRequest(Request request) {
        if (mRequestProxy != null) {
            mRequestProxy.sendRequest(request);
        } else {
            Ln.e("no proxy to sendRequest!!!");
        }
    }

}
