package com.excalibur.core.net.http.base;

import com.excalibur.core.base.Constants;
import com.excalibur.core.net.http.exception.ConnectionException;
import com.excalibur.core.util.IOUtils;
import com.excalibur.core.util.Ln;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;


public class HTTPInputStream extends BufferedInputStream {

    public static HTTPInputStream getInstance(HttpURLConnection conn,
                                              boolean useErrorStream) throws ConnectionException {
        try {
            InputStream is = useErrorStream ? conn.getErrorStream() : conn
                    .getInputStream();
            is = getUnpackedInputStream(conn.getContentEncoding(), is);
            return new HTTPInputStream(is, conn, null);
        } catch (Exception e) {
            throw new ConnectionException(e);
        }
    }

    public static HTTPInputStream getInstance(HttpResponse resp)
            throws ConnectionException {
        HttpEntity entity = resp.getEntity();
        try {
            InputStream is = entity.getContent();
            Header ce = entity.getContentEncoding();
            is = getUnpackedInputStream(ce != null ? ce.getValue() : null, is);
            return new HTTPInputStream(is, null, entity);
        } catch (Exception e) {
            throw new ConnectionException(e);
        }
    }

    private static InputStream getUnpackedInputStream(String contentEncoding,
                                                      InputStream is) throws IOException {
        Ln.d("Content-Encoding: %s.", contentEncoding);
        if (StringUtils.isNotEmpty(contentEncoding)) {
            contentEncoding = contentEncoding.toLowerCase();
            if (contentEncoding.contains("gzip")) {
                return new GZIPInputStream(is);
            } else if (contentEncoding.contains("deflate")) {
                return new InflaterInputStream(is);
            }
        }
        return is;
    }

    private final HttpURLConnection conn;
    private final HttpEntity        entity;

    private HTTPInputStream(InputStream is, HttpURLConnection conn,
                            HttpEntity entity) throws ConnectionException {
        super(is, Constants.BUFFER_SIZE);
        this.conn = conn;
        this.entity = entity;
    }

    public String readAndClose() throws ConnectionException {
        try {
            return IOUtils.readToString(this);
        } catch (Exception e) {
            throw new ConnectionException(e);
        } finally {
            IOUtils.silentlyClose(this);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (conn != null) {
            conn.disconnect();
        } else if (entity != null) {
            entity.consumeContent();
        }
    }

}