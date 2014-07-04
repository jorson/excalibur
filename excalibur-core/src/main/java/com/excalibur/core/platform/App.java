package com.excalibur.core.platform;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import com.excalibur.core.device.sdcard.SdCardStatus;
import com.excalibur.core.net.state.NetStateManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * 应用类
 * 增加Activity的注册和销毁
 */
public abstract class App extends Application {

    private static App sInstance;
    private        List<WeakReference<Activity>> activities = new LinkedList<WeakReference<Activity>>();
    private static boolean                       DEBUG_MODE = true;

    public static App getApplication() {
        return sInstance;
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    private static final String BASE_CACHE_DIR = ".HY_APP";

    private static String cachePath;

    public static String getCachePath() {
        return cachePath;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DEBUG_MODE = 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);

        cachePath = getCacheDir().getPath() + File.separator;
        SdCardStatus.init(this, genSdCardPath());
        NetStateManager.init(this);

        afterCreate();
    }

    private String genSdCardPath() {
        String packageName = getPackageName().replace('.', '_');
        return BASE_CACHE_DIR + File.separator + packageName;
    }

    /**
     * 退出应用
     * finish所有注册的Activity并退出
     */
    public void exit() {
        beforeExit();
        sInstance.finishActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    protected abstract void afterCreate();

    protected abstract void beforeExit();

    public void registerActivity(Activity activity) {
        activities.add(new WeakReference<Activity>(activity));
    }

    protected void finishActivities() {
        for (WeakReference<Activity> aRef : activities) {
            Activity a = aRef.get();
            if (null != a && !a.isFinishing()) a.finish();
        }
    }


}