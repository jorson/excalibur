/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.excalibur.core.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.nd.hy.android.core.base.Constants;
import com.nd.hy.android.core.util.Ln;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * AndroidUtil
 *
 * @author Yangz imported.
 */
public class AndroidUtil {

    private static int tableCheckState = 0;

    /**
     * 获取应用名称
     *
     * @param ctx
     * @return
     */
    public static String getAppName(Context ctx) {
        return ctx.getString(ctx.getApplicationInfo().labelRes);
    }

    /**
     * 获取系统版本编码(level)
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Ln.e("Cannot find package and its version info.");
            return -1;
        }
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            Ln.e("Cannot find package and its version info.");
            return "no version name";
        }
    }

    /**
     * 获取DeviceId
     *
     * @param context
     * @return 当获取到的TelephonyManager为null时，将返回"null"
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "null";
        } else {
            String id = tm.getDeviceId();
            return id == null ? "null" : id;
        }
    }

    /**
     * 显示或隐藏IME
     *
     * @param context
     * @param bHide
     */
    public static void hideIME(Activity context, boolean bHide) {
        if (bHide) {
            try {
                ((InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(context.getCurrentFocus()
                                .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else { // show IME
            try {
                ((InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(context.getCurrentFocus(),
                                InputMethodManager.SHOW_IMPLICIT);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }

    /**
     * 在dialog开启前确定需要开启后跳出IME
     *
     * @param dialog
     */
    public static void showIMEonDialog(AlertDialog dialog) {
        try {
            Window window = dialog.getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } catch (Exception e) {
            Ln.e(e.toString());
        }
    }

    /**
     * 判断一个apk是否安装
     *
     * @param ctx
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isGooglePlayInstalled(Context ctx) {
        return isAndroidMarketInstalled(ctx);
    }

    /**
     * @param ctx
     * @return
     * @deprecated use isGooglePlayInstalled(Context ctx) instead
     */
    public static boolean isAndroidMarketInstalled(Context ctx) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=foo"));
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 安装某个apk文件
     *
     * @param apkFile
     * @param context
     */
    public static void installApkWithPrompt(File apkFile, Context context) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        promptInstall.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        context.startActivity(promptInstall);
    }

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Constants.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Note: Make sure isDownloadManagerAvailable return is true before use this method.
     *
     * @param apkName    Apk File Name
     * @param fullApkUrl url of full
     * @param context    Context
     */
    @TargetApi(Constants.VERSION_CODES.GINGERBREAD)
    public static void downloadApkByDownloadManager(String apkName, String fullApkUrl, Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullApkUrl));
        request.setDescription(fullApkUrl);
        request.setTitle(apkName);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Constants.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/vnd.android.package-archive");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public static boolean networkStatusOK(final Context context) {
        boolean netStatus = false;

        try {
            ConnectivityManager connectManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
                    netStatus = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return netStatus;
    }

    /**
     * 获取屏幕大小
     *
     * @param context
     * @return
     */
    public static int[] getScreenDimention(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int[] coordinate = new int[2];
        if (Build.VERSION.SDK_INT >= Constants.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            coordinate[0] = size.x;
            coordinate[1] = size.y;
        } else {
            coordinate[0] = display.getWidth();
            coordinate[1] = display.getHeight();
        }
        return coordinate;
    }

    /**
     * 是否为横屏
     *
     * @param context
     * @return is land
     */
    public static boolean isLandscapeOrientation(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 当前设备是否为APad
     *
     * @return is pad
     * @see #isTabletDevice(android.app.Activity)
     * @deprecated
     */
    @Deprecated
    @TargetApi(Constants.VERSION_CODES.HONEYCOMB_MR2)
    public static boolean isTabletDevice(Context ctx) {
        if (tableCheckState != 0) {
            return tableCheckState == 1;
        }
        return Build.VERSION.SDK_INT >= Constants.VERSION_CODES.HONEYCOMB_MR2
                && ctx.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_TV;
    }

    /**
     * 当前设备是否为APad
     *
     * @param activity
     * @return is pad
     */
    public static boolean isTabletDevice(Activity activity) {
        if (tableCheckState != 0) {
            return tableCheckState == 1;
        }
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        double screenSize = diagonalPixels / (160 * dm.density);
        if (screenSize >= 6) {
            tableCheckState = 1;
            return true;
        }
        tableCheckState = -1;
        return false;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return status bar height
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

}
