package com.excalibur.demo.biz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class IntentUtil {

    public static void startActivity(Activity activity, Class<? extends Activity> target, Bundle data) {
        startActivity(activity, target, data, false);
    }


    public static void startActivity(Activity activity, Class<? extends Activity> target, Bundle data, boolean isFinishSelf) {
        Intent intent = new Intent(activity, target);
        if (data != null) {
            intent.putExtras(data);
        }
        activity.startActivity(intent);
        if (isFinishSelf) {
            activity.finish();
        }
    }

}
