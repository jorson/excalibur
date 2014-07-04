package com.excalibur.core.net.http.base;

import java.util.List;
import java.util.Map;

/**
 * The result of a webservice call.
 * <p/>
 * Contains the headers and the body of the response as an unparsed <code>String</code>.
 *
 * @author Foxykeep
 */
public final class ConnectionResult {

    public final Map<String, List<String>> headerMap;
    public final String                    body;
    public final byte[]                    data;

    public ConnectionResult(Map<String, List<String>> headerMap, String body) {
        this(headerMap, body, null);
    }

    public ConnectionResult(Map<String, List<String>> headerMap, String body, byte[] data) {
        this.headerMap = headerMap;
        this.body = body;
        this.data = data;
    }
}
