package com.excalibur.frame.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.excalibur.core.bus.EventBus;
import com.excalibur.core.data.RestoreUtil;
import com.excalibur.core.util.Ln;
import com.excalibur.core.view.inject.ViewInjectUtils;
import com.excalibur.frame.base.Request;

/**
 * SweetFragment
 *
 * @author Yangz
 *         Date: 13-5-15
 */
public abstract class SweetFragment extends Fragment {

    protected RequestProxy mRequestProxy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRequestProxy == null) {
            mRequestProxy = createRequestProxy();
        }
        if (mRequestProxy != null) {
            mRequestProxy.loadInstanceState(savedInstanceState);
        }
        RestoreUtil.loadState(savedInstanceState, this);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = onBaseCreateView(inflater, container, savedInstanceState);
        ViewInjectUtils.inject(view, this, this);
        bindView(view, savedInstanceState);
        return view;
    }

    protected abstract View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void bindView(View view, Bundle savedInstanceState);

    protected abstract RequestProxy createRequestProxy();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRequestProxy != null) {
            mRequestProxy.saveInstanceState(outState);
        }
        RestoreUtil.saveState(outState, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestProxy != null) {
            mRequestProxy.loadRequestList();
        }
        EventBus.registerAnnotatedReceiver(this);
    }

    @Override
    public void onPause() {
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
