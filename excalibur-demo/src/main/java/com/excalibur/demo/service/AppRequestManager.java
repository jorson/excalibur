package com.excalibur.demo.service;

import android.content.Context;
import com.excalibur.frame.manager.RequestManager;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 */
public final class AppRequestManager extends RequestManager {

    // Singleton management
    private static AppRequestManager sInstance;

    public synchronized static AppRequestManager from(Context context) {
        if (sInstance == null) {
            sInstance = new AppRequestManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private AppRequestManager(Context context) {
        super(context, AppRequestService.class);
    }
}
