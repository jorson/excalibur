package com.excalibur.core.net.http.worker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import com.excalibur.core.base.Constants;
import com.excalibur.core.net.http.base.ConnectionResult;
import com.excalibur.core.net.http.base.Method;
import com.excalibur.core.net.http.exception.ConnectionException;
import com.excalibur.core.util.IOUtils;
import com.excalibur.core.util.Ln;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * Implementation of the network connection.
 *
 * @author Foxykeep
 */
public abstract class HttpWorker {

    protected static final String ACCEPT_CHARSET_HEADER  = "Accept-Charset";
    protected static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    protected static final String AUTHORIZATION_HEADER   = "Authorization";
    protected static final String LOCATION_HEADER        = "Location";
    protected static final String CHARSET                = Constants.UTF8;

    // Default connection and socket timeout of 60 seconds. Tweak to taste.
    protected static final int OPERATION_TIMEOUT = 60 * 1000;

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
    public abstract ConnectionResult execute(Context context, String urlValue, Method method, byte[] streamData,
                                             List<? extends NameValuePair> parameterList, HashMap<String, String> headerMap,
                                             boolean isGzipEnabled, String userAgent, String postText, String postContentType,
                                             UsernamePasswordCredentials credentials) throws
            ConnectionException;

    protected static boolean isErrorResponseCode(int responseCode) {
        return responseCode >= 400;
    }

    protected static HttpURLConnection createConnectionWithAPN(Context ctx, URL url) throws IOException {
        NetworkInfo networkInfo = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        // 如果是使用的运营商网络
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // 获取默认代理主机ip
            String host = android.net.Proxy.getDefaultHost();
            // 获取端口
            int port = android.net.Proxy.getDefaultPort();
            if (host != null && port != -1) {
//                String line = "host[" + host + "] port[" + port + "] ";
                // 封装代理连接主机IP与端口号。
                InetSocketAddress inetAddress = new InetSocketAddress(host, port);
                // 根据URL链接获取代理类型，本链接适用于TYPE.HTTP
                java.net.Proxy.Type proxyType = java.net.Proxy.Type.valueOf(url
                        .getProtocol().toUpperCase());
                java.net.Proxy javaProxy = new java.net.Proxy(proxyType, inetAddress);
                return (HttpURLConnection) url.openConnection(javaProxy);
            }
        }
        return (HttpURLConnection) url.openConnection();
    }

    public static StringBuilder builderParameters(List<? extends NameValuePair> parameterList) throws UnsupportedEncodingException {
        StringBuilder paramBuilder = new StringBuilder();
        if (parameterList != null && !parameterList.isEmpty()) {
            for (int i = 0, size = parameterList.size(); i < size; i++) {
                NameValuePair parameter = parameterList.get(i);
                String name = parameter.getName();
                String value = parameter.getValue();
                if (TextUtils.isEmpty(name)) {
                    // Empty parameter name. Check the next one.
                    continue;
                }
                if (value == null) {
                    value = "";
                }
                paramBuilder.append(URLEncoder.encode(name, CHARSET));
                paramBuilder.append("=");
                paramBuilder.append(URLEncoder.encode(value, CHARSET));
                paramBuilder.append("&");
            }
        }
        return paramBuilder;
    }

    protected static void logRequest(String urlValue, Method method, List<? extends NameValuePair> parameterList,
                                     HashMap<String, String> headerMap, String postText, String parameters) {
        if (Ln.DEBUG) {
            Ln.d("Request url: " + urlValue);
            Ln.d("Method: " + method.toString());

            if (parameterList != null && !parameterList.isEmpty()) {
                Ln.d("Parameters:");
                for (int i = 0, size = parameterList.size(); i < size; i++) {
                    NameValuePair parameter = parameterList.get(i);
                    String message = "- \"" + parameter.getName() + "\" = \""
                            + parameter.getValue() + "\"";
                    Ln.d(message);
                }

                Ln.d("Parameters String: \"" + parameters + "\"");
            }

            if (postText != null) {
                Ln.d("Post data: " + postText);
            }

            if (headerMap != null && !headerMap.isEmpty()) {
                Ln.d("Headers:");
                for (Entry<String, String> header : headerMap.entrySet()) {
                    Ln.d("- " + header.getKey() + " = " + header.getValue());
                }
            }
        }
    }

    protected static String createAuthenticationHeader(UsernamePasswordCredentials credentials) {
        StringBuilder sb = new StringBuilder();
        sb.append(credentials.getUserName()).append(":").append(credentials.getPassword());
        return "Basic " + Base64.encodeToString(sb.toString().getBytes(), Base64.NO_WRAP);
    }

    private static SSLSocketFactory sAllHostsValidSocketFactory;

    protected static SSLSocketFactory getAllHostsValidSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException {
        if (sAllHostsValidSocketFactory == null) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sAllHostsValidSocketFactory = sc.getSocketFactory();
        }

        return sAllHostsValidSocketFactory;
    }

    private static HostnameVerifier sAllHostsValidVerifier;

    protected static HostnameVerifier getAllHostsValidVerifier() {
        if (sAllHostsValidVerifier == null) {
            sAllHostsValidVerifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }

        return sAllHostsValidVerifier;
    }

    protected static byte[] convertStreamToBytes(InputStream is) {
        ByteArrayOutputStream baos = null;
        try {
            int totalSize = 0;
            int size = 1024;
            baos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];

            while (size == 1024) {
                size = is.read(data);
                baos.write(data, 0, size);
                totalSize += size;
            }

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.silentlyClose(baos);
        }

        return null;
    }

    protected static String convertStreamToString(InputStream is, boolean isGzipEnabled)
            throws IOException {
        InputStream cleanedIs = is;
        if (isGzipEnabled) {
            cleanedIs = new GZIPInputStream(is);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(cleanedIs, CHARSET));
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }

            cleanedIs.close();

            if (isGzipEnabled) {
                is.close();
            }
        }
    }
}
