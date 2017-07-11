package com.auto.watchmen.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.auto.watchmen.base.TaskDetailContract;
import com.auto.watchmen.bean.DataInfo;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by dage on 2017/7/10.
 */
public class HomePresenter implements TaskDetailContract.Presenter {
    private static final String TAG = "HomePresenter";
    private static final String BASE_URL = "http://hq.sinajs.cn/rn=1482826291863&list=%s";//hf_XAU
    private static Handler mTaskHandler;
    private final OkHttpClient mHttpClient = new OkHttpClient();

    static {
        HandlerThread thread = new HandlerThread("home_loop");
        thread.start();
        mTaskHandler = new Handler(thread.getLooper());
    }

    private TaskDetailContract.View mTaskView;

    public HomePresenter(TaskDetailContract.View taskView) {
        mTaskView = taskView;
        mTaskView.setPresenter(this);
    }

    @Override
    public void check() {

    }

    @Override
    public void start() {
        mTaskHandler.post(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(String.format(BASE_URL, "hf_XAU"))
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
