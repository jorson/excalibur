package com.excalibur.core.device;

import android.content.Context;
import android.util.TypedValue;
import com.excalibur.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * ResourceUtils
 *
 */
public final class ResourceUtils {

    /**
     * dpToPx simple
     *
     * @param ctx
     * @param val
     * @return
     */
    public static int dpToPx(Context ctx, float val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, ctx.getResources().getDisplayMetrics());
    }

    /**
     * 从key-value类的StringArray中得到与key对应的value
     *
     * @param ctx
     * @param keysArrId
     * @param valuesArrId
     * @param key
     * @return
     */
    public static String valueForKey(Context ctx, int keysArrId,
                                     int valuesArrId, String key) {
        String[] keysArr = ctx.getResources().getStringArray(keysArrId);
        String[] valuesArr = ctx.getResources().getStringArray(valuesArrId);
        int idx = Arrays.asList(keysArr).indexOf(key);
        return (idx != -1) ? valuesArr[idx] : null;
    }

    /**
     * 从raw中读取文本文件
     *
     * @param ctx
     * @param resId
     * @return
     * @throws IllegalArgumentException
     */
    public static String readRawResource(Context ctx, int resId)
            throws IllegalArgumentException {
        InputStream is = null;
        try {
            is = ctx.getResources().openRawResource(resId);
            return IOUtils.readToString(is);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.silentlyClose(is);
        }
    }

    /**
     * 通过资源名称获得资源id
     *
     * @param ctx
     * @param resourceName
     * @return
     */
    public static int getResourceId(Context ctx, String resourceName) {
        return getId(ctx, "id", resourceName);
    }

    /**
     * 通过字符串资源获得字符串资源id
     *
     * @param ctx
     * @param stringName
     * @return
     */
    public static int getStringId(Context ctx, String stringName) {
        return getId(ctx, "string", stringName);
    }

    private static int getId(Context ctx, String type, String name) {
        return ctx.getResources().getIdentifier(name, type,
                ctx.getPackageName());
    }

}
