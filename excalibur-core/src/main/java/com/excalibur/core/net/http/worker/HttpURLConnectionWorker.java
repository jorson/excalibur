package com.excalibur.core.net.http.worker;

import android.content.Context;
import com.excalibur.core.device.UserAgentUtils;
import com.excalibur.core.net.http.base.ConnectionResult;
import com.excalibur.core.net.http.base.Method;
import com.excalibur.core.net.http.exception.ConnectionException;
import com.excalibur.core.util.Ln;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Implementation of the network connection.
 *
 * @author Foxykeep
 */
public class HttpURLConnectionWorker extends HttpWorker {

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
        HttpURLConnection connection = null;
        try {
            // Prepare the request information
            boolean isSendBytes = streamData != null;
            if (streamData != null) {
                // bytes data
                method = Method.POST;
                parameterList = null;
                postText = null;
            }
            if (userAgent == null) {
                userAgent = UserAgentUtils.get(context);
            }
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

            StringBuilder paramBuilder = builderParameters(parameterList);

            // Log the request
            logRequest(urlValue, method, parameterList, headerMap, postText, paramBuilder.toString());

            // Create the connection object
            URL url = null;
            String outputText = null;
            switch (method) {
                case GET:
                case DELETE:
                    String fullUrlValue = urlValue;
                    if (paramBuilder.length() > 0) {
                        fullUrlValue += "?" + paramBuilder.toString();
                    }
                    url = new URL(fullUrlValue);
                    connection = createConnectionWithAPN(context, url);
//                    connection = (HttpURLConnection) url.openConnection();
                    break;
                case PUT:
                case POST:
                    url = new URL(urlValue);
                    connection = createConnectionWithAPN(context, url);
                    //connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);

                    if (paramBuilder.length() > 0) {
                        outputText = paramBuilder.toString();
                        headerMap.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
                        headerMap.put(HTTP.CONTENT_LEN,
                                String.valueOf(outputText.getBytes().length));
                    } else if (postText != null) {
                        outputText = postText;
                        headerMap.put(HTTP.CONTENT_TYPE, postContentType);
                        headerMap.put(HTTP.CONTENT_LEN, String.valueOf(outputText.getBytes().length));
                    } else if (streamData != null) {
                        headerMap.put(HTTP.CONTENT_LEN, String.valueOf(streamData.length));
                    } else {
                        method = Method.GET;
                    }
                    break;
            }

            // Set the request method
            connection.setRequestMethod(method.toString());

            // If it's an HTTPS request and the SSL Validation is disabled
            if (url.getProtocol().equals("https")) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                httpsConnection.setSSLSocketFactory(getAllHostsValidSocketFactory());
                httpsConnection.setHostnameVerifier(getAllHostsValidVerifier());
            }

            // Add the headers
            if (!headerMap.isEmpty()) {
                for (Entry<String, String> header : headerMap.entrySet()) {
                    connection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            // Set the connection and read timeout
            connection.setConnectTimeout(OPERATION_TIMEOUT);
            connection.setReadTimeout(OPERATION_TIMEOUT);

            // Set the outputStream content for POST and PUT requests
            byte[] bytes = (streamData != null) ? streamData : (outputText != null ? outputText.getBytes() : null);
            if ((method == Method.POST || method == Method.PUT) && bytes != null) {
                OutputStream output = null;
                try {
                    output = connection.getOutputStream();
//                    output.write(outputText.getBytes());
                    output.write(bytes);
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            // Already catching the first IOException so nothing to do here.
                        }
                    }
                }
            }

            String contentEncoding = connection.getHeaderField(HTTP.CONTENT_ENCODING);

            int responseCode = connection.getResponseCode();
            boolean isGzip = contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip");
            Ln.d("Response code: " + responseCode);

            if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                String redirectionUrl = connection.getHeaderField(LOCATION_HEADER);
                throw new ConnectionException("New location : " + redirectionUrl,
                        redirectionUrl);
            }

//            InputStream errorStream = connection.getErrorStream();
//            if (errorStream != null) {
//                String error = convertStreamToString(errorStream, isGzip);
//                throw new ConnectionException(error, responseCode);
//            }

            String body = null;
            byte[] responseData = null;
            if (isSendBytes) {
                responseData = convertStreamToBytes(connection.getInputStream());
            } else {
                body = convertStreamToString(connection.getInputStream(), isGzip);
            }

            if (Ln.DEBUG) {
                if (body != null) {
                    Ln.v("Response body: ");

                    int pos = 0;
                    int bodyLength = body.length();
                    while (pos < bodyLength) {
                        Ln.v(body.substring(pos, Math.min(bodyLength - 1, pos + 200)));
                        pos = pos + 200;
                    }
                } else if (responseData != null) {
                    Ln.v("Response data size: " + responseData.length);
                }
            }

            return new ConnectionResult(connection.getHeaderFields(), body, responseData);
        } catch (IOException e) {
            Ln.e(e, "IOException");
            throw new ConnectionException(e);
        } catch (KeyManagementException e) {
            Ln.e(e, "KeyManagementException");
            throw new ConnectionException(e);
        } catch (NoSuchAlgorithmException e) {
            Ln.e(e, "NoSuchAlgorithmException");
            throw new ConnectionException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
