package com.auto.watchmen.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.auto.watchmen.R;
import com.auto.watchmen.RefreshReceiver;
import com.auto.watchmen.bean.DataInfo;
import com.auto.watchmen.db.DataInfoDb;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by dage on 2017/7/10.
 */
public class HomeBiz {
    private static final String TAG = "HomeBiz";
    private final static long MINUTE_UNIT = 60 * 1000;
    private final static long HOUR_UNIT = 60 * MINUTE_UNIT;
    private final static long DATE_UNIT = 24 * HOUR_UNIT;
    private static final String BASE_URL = "http://hq.sinajs.cn/rn=1482826291863&list=%s";//hf_XAU
    private static Handler mTaskHandler;
    private final OkHttpClient mHttpClient = new OkHttpClient();
    private Context mContext;

    static {
        HandlerThread thread = new HandlerThread("HomeBiz");
        thread.start();
        mTaskHandler = new Handler(thread.getLooper());
    }

    private static volatile HomeBiz mInstance;

    public synchronized static HomeBiz getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HomeBiz.class) {
                if (mInstance == null) {
                    mInstance = new HomeBiz(context);
                }
            }
        }
        return mInstance;
    }

    private HomeBiz(Context context) {
        mContext = context.getApplicationContext();
    }

    private void postNextRequest() {
        //16:00;
        long currentTime = System.currentTimeMillis();
        long delayTime = DATE_UNIT - (currentTime + 8 * HOUR_UNIT + 16 * HOUR_UNIT) % DATE_UNIT;
        long nextTime = currentTime + delayTime;
        final AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(RefreshReceiver.ACTION_REFRESH);
        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, (int) nextTime, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, nextTime + 1, pIntent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, nextTime + 1, pIntent);
        }
    }

    public void start() {
        request();
    }

    public void request() {
        postNextRequest();
//        mTaskHandler.post(mXAU);
        String[] goodsArray = mContext.getResources().getStringArray(R.array.goodsName);
        for (String name : goodsArray) {
            mTaskHandler.post(new DataRunnable(String.format(BASE_URL, name)));
        }
    }

    private DataRunnable mXAU = new DataRunnable(String.format(BASE_URL, "hf_XAU"));

    private class DataRunnable implements Runnable {

        private String mUrl;

        public DataRunnable(String url) {
            this.mUrl = url;
        }

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url(mUrl)
                    .build();
            try {
                Response response = mHttpClient.newCall(request).execute();
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                String ret = response.body().string();
                Log.d(TAG, "ret:" + ret);
                if (TextUtils.isEmpty(ret)) return;
                String[] params = ret.split("\"")[1].split(",");
                DataInfo info = new DataInfo();
                info.setEnd(Utils.parseFloat(params[0]));
                info.setMax(Utils.parseFloat(params[4]));
                info.setMin(Utils.parseFloat(params[5]));
                info.setBegin(Utils.parseFloat(params[8]));
                Log.d(TAG, "info:" + info);
                DataInfoDb.getInstance(mContext).addRecord(info);//add to db;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
