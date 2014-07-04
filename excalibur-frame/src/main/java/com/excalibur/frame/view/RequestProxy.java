package com.excalibur.frame.view;

import android.os.Bundle;
import com.excalibur.frame.base.Request;
import com.excalibur.frame.base.RequestConstants;
import com.excalibur.frame.manager.RequestManager;

import java.util.ArrayList;

/**
 *         Date: 13-6-4
 */
public class RequestProxy implements RequestManager.RequestListener {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";

    protected RequestManager        mRequestManager;
    protected ArrayList<Request>    mRequestList;
    private   RequestResultListener mResultListener;

    public RequestProxy(RequestManager rm, RequestResultListener listener) {
        mRequestManager = rm;
        mResultListener = listener;
    }

    protected void saveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    protected void loadInstanceState(Bundle bundle) {
        if (null != bundle) {
            mRequestList = bundle.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
        }
        if (mRequestList == null) {
            mRequestList = new ArrayList<Request>();
        }
    }

    protected void loadRequestList() {
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);
            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
            } else {
                if (request.isMemoryCacheEnabled()) {
                    mRequestManager.callListenerWithCachedData(this, request);
                }
                mRequestList.remove(request);
                i--;
            }
        }
    }

    protected void removeRequestsLister() {
        if (!mRequestList.isEmpty()) {
            mRequestManager.removeRequestListener(this);
        }
    }

    public void sendRequest(Request request) {
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    public void sendRequest(Request request, RequestManager.RequestListener listener) {
        mRequestManager.execute(request, listener);
        mRequestList.add(request);
    }

    @Override
    public final void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
            mResultListener.afterRequest(request);
            if (mResultListener != null) {
                mResultListener.onRequestSuccess(request, resultData);
            }
        }
    }

    private Bundle generateResultData(String message) {
        Bundle data = new Bundle();
        data.putInt(RequestConstants.BUNDLE_KEY_CODE, -1);
        data.putString(RequestConstants.BUNDLE_KEY_MESSAGE, message);
        return data;
    }

    @Override
    public final void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
            mResultListener.afterRequest(request);
            if (mResultListener != null) {
                mResultListener.onRequestFailure(request, generateResultData(RequestConstants.MSG_CONNECT_ERROR));
            }
        }
    }

    @Override
    public final void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
            mResultListener.afterRequest(request);
            if (mResultListener != null) {
                mResultListener.onRequestFailure(request, generateResultData(RequestConstants.MSG_DATA_FORMAT_ERROR));
            }
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
            mResultListener.afterRequest(request);
            if (mResultListener != null) {
                mResultListener.onRequestFailure(request, resultData);
            }
        }
    }
}
