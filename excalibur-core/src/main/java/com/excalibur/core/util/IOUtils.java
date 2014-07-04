package com.excalibur.core.util;

import static com.nd.hy.android.core.base.Constants.*;
import static com.nd.hy.android.core.base.Constants.BUFFER_SIZE;
import static com.nd.hy.android.core.base.Constants.UTF8;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yangz
 * @version 14-5-19
 */
public class IOUtils {

    public static void silentlyClose(Closeable... closeables) {
        for (Closeable cl : closeables) {
            try {
                if (cl != null) {
                    cl.close();
                }
            } catch (Exception e) {
                Ln.wtf(e);
            }
        }
    }

    public static String readToString(InputStream is) throws IOException {
        byte[] data = readToByteArray(is);
        return new String(data, UTF8);
    }

    public static byte[] readToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

}
