package com.auto.watchmen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.auto.watchmen.util.HomeBiz;

/**
 * Created by dage on 2017/7/17.
 */
public class RefreshReceiver extends BroadcastReceiver {
    public static final String ACTION_REFRESH = "com.auto.action.refresh";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("big", "onReceive");
        HomeBiz.getInstance(context).request();
    }
}
