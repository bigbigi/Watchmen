package com.auto.watchmen.util;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.auto.watchmen.db.DataInfoDb;
import com.auto.watchmen.info.DataInfo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by dage on 2017/1/3.
 */
public class ReadUtils {

    public static String readFile(String dir, String name) {
        FileInputStream fis = null;
        InputStreamReader isReader = null;
        BufferedReader bufferReader = null;
        String ret = "";
        try {
            fis = new FileInputStream(new File(dir + "/" + name));
            String line;
            StringBuffer sb = new StringBuffer();
            isReader = new InputStreamReader(fis);
            bufferReader = new BufferedReader(isReader);
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            ret = sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferReader);
            closeIO(isReader);
            closeIO(fis);
        }
        return ret;
    }

    public static void closeIO(Closeable ioStream) {
        if (ioStream != null) {
            try {
                ioStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void pushData(final Context context) {
        new Thread() {
            @Override
            public void run() {
                String ret = readFile(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/watch", "test.txt");
                addDataInfo(context.getApplicationContext(), ret);
//                List<DataInfo> list = DataInfoDb.getInstance(HomeActivity.this).getRecord();
//                List<DataInfo> weekList = getWeekDataInfo(list);
//                getBollInfo(99, weekList);
//                getMacdInfo(1, 1, 1, weekList);
//                getBollInfo(99, list);
//                getMacdInfo(1, 1, 1, list);
            }
        }.start();
    }

    public static void addDataInfo(Context context, String ret) {
        String[] row = ret.split("\\n");
        Log.d("big", "row-->" + row.length);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String name = "";
        for (String current : row) {
            //Log.d("big", "text-->" + current);
            current = current.trim();
            String[] array = current.split("\\s+");
            DataInfo info = new DataInfo();
            if (TextUtils.isEmpty(name)) {
                name = current;
                continue;
            }
            info.setName(name);
            String time = array[0].trim();
            String[] timeArray = time.split("/");
            // Log.d("big","time:"+time+"length:"+timeArray.length);
            if (timeArray.length < 2) continue;
            try {
                info.setTime(format.parse(time).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            info.setBegin(Float.parseFloat(array[1].trim()));
            info.setMax(Float.parseFloat(array[2].trim()));
            info.setMin(Float.parseFloat(array[3].trim()));
            info.setEnd(Float.parseFloat(array[4].trim()));

            Log.d("big", "current-->" + info);
            DataInfoDb.getInstance(context).addRecord(info);
        }
    }
}
