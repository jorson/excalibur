package com.excalibur.core.net.http.worker;

import android.content.Context;
import com.excalibur.core.base.Constants;
import com.excalibur.core.net.http.base.ConnectionResult;
import com.excalibur.core.net.http.base.HTTPInputStream;
import com.excalibur.core.net.http.base.Method;
import com.excalibur.core.net.http.exception.ConnectionException;
import com.excalibur.core.util.Ln;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY;

/**
 * Implementation of the network connection.
 *
 * @author Foxykeep
 */
public class HttpClientWorker extends HttpWorker {

    private final DefaultHttpClient httpClient;

    public HttpClientWorker() {
        super();
        httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
//        HttpProtocolParams.setUserAgent(params, userAgent);
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpClientParams.setRedirecting(params, false);
        HttpConnectionParams.setConnectionTimeout(params,
                OPERATION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, OPERATION_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, Constants.BUFFER_SIZE);
        HttpClientParams.setCookiePolicy(params, BROWSER_COMPATIBILITY);

        // TODO: https request handle
    }

    /**
     * Call the webservice using the given parameters to construct the request and return the
     * result.
     *
     * @param context       The context to use for this operation. Used to generate the user agent if
     *                      needed.
     * @param urlValue      The webservice URL.
     * @param method        The request method to use.
     * @param parameterList The parameters to add to the request.
     * @param headerMap     The headers to add to the request.
     * @param isGzipEnabled Whether the request will use gzip compression if available on the
     *                      server.
     * @param userAgent     The user agent to set in the request. If null, a default Android one will be
     *                      created.
     * @param postText      The POSTDATA text to add in the request.
     * @param credentials   The credentials to use for authentication.
     * @return The result of the webservice call.
     */
    public ConnectionResult execute(Context context, String urlValue, Method method, byte[] streamData,
                                    List<? extends NameValuePair> parameterList, HashMap<String, String> headerMap,
                                    boolean isGzipEnabled, String userAgent, String postText, String postContentType,
                                    UsernamePasswordCredentials credentials) throws
            ConnectionException {
        if (headerMap == null) {
            headerMap = new HashMap<String, String>();
        }
        headerMap.put(HTTP.USER_AGENT, userAgent);
        if (isGzipEnabled) {
            headerMap.put(ACCEPT_ENCODING_HEADER, "gzip");
        }
        headerMap.put(ACCEPT_CHARSET_HEADER, CHARSET);
        if (credentials != null) {
            headerMap.put(AUTHORIZATION_HEADER, createAuthenticationHeader(credentials));
        }

        switch (method) {
            case GET:
                HttpGet reqGet = new HttpGet(urlValue);
                return getResponse(reqGet, headerMap);
            case DELETE:
                HttpDelete reqDelete = new HttpDelete(urlValue);
                return getResponse(reqDelete, headerMap);
            case POST:
                HttpPost reqPost = new HttpPost(urlValue);
                return doRequest(reqPost, parameterList, headerMap, postText, postContentType, streamData);
            case PUT:
                HttpPut reqPut = new HttpPut(urlValue);
                return doRequest(reqPut, parameterList, headerMap, postText, postContentType, streamData);
            default:
                return null;
        }
    }

    private ConnectionResult doRequest(HttpEntityEnclosingRequestBase reqPost,
                                       List<? extends NameValuePair> parameterList,
                                       HashMap<String, String> headerMap,
                                       String postText, String postContentType,
                                       byte[] streamData) throws ConnectionException {
        String paramsBuilder = null;
        try {
            paramsBuilder = builderParameters(parameterList).toString();
            if (paramsBuilder != null && paramsBuilder.length() > 0) {
                headerMap.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
                headerMap.put(HTTP.CONTENT_LEN, String.valueOf(paramsBuilder.length()));
                reqPost.setEntity(buildStringEntity(postContentType, paramsBuilder));
            } else if (postText != null) {
                headerMap.put(HTTP.CONTENT_TYPE, postContentType);
                headerMap.put(HTTP.CONTENT_LEN, String.valueOf(postText.length()));
                reqPost.setEntity(buildStringEntity(postContentType, postText));
            } else if (streamData != null) {
                headerMap.put(HTTP.CONTENT_LEN, String.valueOf(streamData.length));
                reqPost.setEntity(buildByteArrayEntity(postContentType, streamData));
            }
            return getResponse(reqPost, headerMap);
        } catch (UnsupportedEncodingException e) {
            Ln.e(e);
            throw new ConnectionException();
        }
    }

    public ConnectionResult getResponse(HttpUriRequest req, HashMap<String, String> headerMap)
            throws ConnectionException {
        HttpResponse resp = getHttpResponse(headerMap, req);
        getResponseCodeOrThrow(resp);
        Map<String, List<String>> headers = getHeaders(resp);
        HTTPInputStream is = HTTPInputStream.getInstance(resp);
        String body = is.readAndClose();
        return new ConnectionResult(headers, body);
    }

    private static Map<String, List<String>> getHeaders(HttpResponse resp) {
        HashMap<String, List<String>> headers = new HashMap<String, List<String>>();
        for (Header header : resp.getAllHeaders()) {
            String name = header.getName();
            if (!headers.containsKey(name)) {
                headers.put(name, new ArrayList<String>());
            }
            headers.get(name).add(header.getValue());
        }
        return headers;
    }

    private HttpResponse getHttpResponse(HashMap<String, String> headers, HttpUriRequest req)
            throws ConnectionException {
        for (String key : headers.keySet()) {
            req.addHeader(key, headers.get(key));
        }
        try {
            return httpClient.execute(req);
        } catch (Exception e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    private static int getResponseCodeOrThrow(HttpResponse resp)
            throws ConnectionException {
        int respCode = resp.getStatusLine().getStatusCode();
        if (isErrorResponseCode(respCode)) {
            String respBody = HTTPInputStream.getInstance(resp).readAndClose();
            throw new ConnectionException(respBody, respCode);
        }
        return respCode;
    }

    public static StringEntity buildStringEntity(String contentType, String data)
            throws ConnectionException {
        try {
            StringEntity entity = new StringEntity(data, Constants.UTF8);
            entity.setContentType(contentType);
            return entity;
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionException(e);
        }
    }

    public static ByteArrayEntity buildByteArrayEntity(String contentType, byte[] data)
            throws ConnectionException {
        ByteArrayEntity entity = new ByteArrayEntity(data);
        entity.setContentType(contentType);
        return entity;
    }

    private static TrustManager truseAllManager = new X509TrustManager() {

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }

    };

}
