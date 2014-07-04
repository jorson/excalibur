package com.excalibur.core.net.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetStateReceiver extends BroadcastReceiver {

    public NetStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetStateManager.getCurNetState(context);
    }

}