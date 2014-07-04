package com.excalibur.core.device.sdcard;

import android.content.Context;
import android.os.Environment;
import com.nd.hy.android.core.util.Ln;

import java.io.File;

/**
 * SD卡状态以及路径判断
 * <p/>
 * （由于现在可以在运行时拔插sd卡（尽责的qa同学验证）每次获取sd卡的状态都要运行一遍系统代码，
 * 以及现在各项目分支以及不能唯一使用91Education这个目录）
 */
public class SdCardStatus {
    private static String CACHE_FOLDER_NAME;
    private static String NONE_SD_CARD_PROMPT = "您的手机中sd卡不存在";

    public static void init(Context context, String cacheFolderName) {
        CACHE_FOLDER_NAME = cacheFolderName;
        hasSdCard();
    }

    public static boolean hasSdCard() {
        String sdCardPath = null;
        sdCardPath = getSDPath();
        if (null == sdCardPath) {
            Ln.e(NONE_SD_CARD_PROMPT);
            return false;
        }
        return true;
    }

    public static String getDefaulstCacheDirInSdCard() throws IllegalStateException {
        String sdCardPath = null;
        sdCardPath = getSDPath();
        if (null == sdCardPath) {
            throw new IllegalStateException(NONE_SD_CARD_PROMPT);
        }
        return sdCardPath + File.separator + CACHE_FOLDER_NAME;

    }

    /**
     * when not exist sd card,return null.
     *
     * @return
     */
    public static String getSDPath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            DevMountInfo dev = DevMountInfo.getInstance();
            DevInfo info = dev.getExternalInfo();
            if (null == info) {
                return null;
            }
            String sd2Path = info.getPath(); // SD 卡路径
            if (sd2Path != null && sd2Path.length() > 0) {
                return sd2Path;
            } else {
                return null;
            }
        }
    }
}
