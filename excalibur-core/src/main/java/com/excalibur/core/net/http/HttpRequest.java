package com.excalibur.core.net.http;

import android.content.Context;
import com.nd.hy.android.core.base.HermesException;
import com.nd.hy.android.core.base.SafeAsyncTask;
import com.nd.hy.android.core.net.http.base.ConnectionResult;
import com.nd.hy.android.core.net.http.base.Method;
import com.nd.hy.android.core.net.http.base.Params;
import com.nd.hy.android.core.net.http.exception.ConnectionException;

import java.util.HashMap;

/**
 * @author Yangz
 * @version 14-5-21
 */
public class HttpRequest {

    private Method                  method;
    private String                  url;
    private Params                  params;
    private HashMap<String, String> headers;

    public static HttpRequest get(String url) {
        HttpRequest request = new HttpRequest();
        request.url = url;
        request.method = Method.GET;
        return request;
    }

    public static HttpRequest post(String url) {
        HttpRequest request = new HttpRequest();
        request.url = url;
        request.method = Method.POST;
        return request;
    }

    public HttpRequest addParam(String key, Object value) {
        if (params == null) {
            params = new Params();
        }
        params.put(key, value);
        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    private NetworkConnection genNetworkConnection(Context ctx) {
        NetworkConnection connection = new NetworkConnection(ctx, url);
        connection.setHeaderList(headers);
        connection.setParameters(params);
        connection.setMethod(method);
        return connection;
    }

    public ConnectionResult send(Context ctx) throws ConnectionException {
        NetworkConnection connection = genNetworkConnection(ctx);
        return connection.execute();
    }

    public void asyncSend(Context ctx, final RequestListener<ConnectionResult> listener) {
        final NetworkConnection connection = genNetworkConnection(ctx);
        new SafeAsyncTask<ConnectionResult>() {
            @Override
            public ConnectionResult call() throws Exception {
                return connection.execute();
            }

            @Override
            protected void onSuccess(ConnectionResult connectionResult) throws Exception {
                super.onSuccess(connectionResult);
                listener.onSuccess(connectionResult);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof HermesException) {
                    listener.onFailure((HermesException) e);
                }
            }
        }.execute();
    }

    public static interface RequestListener<T> {
        void onSuccess(T result);

        void onFailure(HermesException he);
    }

}
