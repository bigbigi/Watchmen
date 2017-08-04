package com.auto.watchmen.util;


import android.content.Context;
import android.util.Log;

import com.auto.watchmen.bean.DataInfo;
import com.auto.watchmen.db.DataInfoDb;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dage on 2017/1/3.
 */
public class DataUtils {

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
                String ret = DataUtils.readFile(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/watch", "test.txt");
                addDataInfo(context.getApplicationContext(), ret);
//                List<DataInfo> list = DataInfoDb.getInstance(HomeActivity.this).getRecord();
//                List<DataInfo> weekList = getWeekDataInfo(list);
//                getBollInfo(99, weekList);
//                getMacdInfo(1, 1, 1, weekList);
//                getBollInfo(99, list);
//                getMacdInfo(1, 1, 1, list);
            }
        }.start();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        Date date = new Date(Long.parseLong("1482826288494"));
        Log.d("big", "test-->" + format.format(date));
    }

    public static void addDataInfo(Context context, String ret) {
        String[] row = ret.split("\\n");
        Log.d("big", "row-->" + row.length);
        Calendar calendar = Calendar.getInstance();
        for (String current : row) {
            Log.d("big", "current-->" + current);
            String[] array = current.split("\\s+");
            DataInfo info = new DataInfo();
            info.setName("test");
            String time = array[0].trim();
            String[] timeArray = time.split("/");
            calendar.set(Calendar.DAY_OF_YEAR, Integer.parseInt(timeArray[0]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timeArray[1]));
            calendar.set(Calendar.DATE, Integer.parseInt(timeArray[2]));
            info.setTime(calendar.getTimeInMillis());
            info.setBegin(Float.parseFloat(array[1].trim()));
            info.setMax(Float.parseFloat(array[2].trim()));
            info.setMin(Float.parseFloat(array[3].trim()));
            info.setEnd(Float.parseFloat(array[4].trim()));

            Log.d("big", "current-->" + info);
            DataInfoDb.getInstance(context).addRecord(info);
        }
    }
}
