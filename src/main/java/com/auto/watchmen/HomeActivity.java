package com.auto.watchmen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.auto.watchmen.bean.BollInfo;
import com.auto.watchmen.bean.DataInfo;
import com.auto.watchmen.db.BollDb;
import com.auto.watchmen.db.DataInfoDb;
import com.auto.watchmen.util.DataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
      /*  DataInfo info = new DataInfo();
        info.setName("test");
        info.setBegin(1);
        info.setEnd(2);
        info.setMax(3);
        info.setMin(-1);
        info.setTime(10000);
        DataInfoDb.getInstance(this).addRecord(info);*/
//        Log.d("big","dataInfo-->"+DataInfoDb.getInstance(this).getRecord());


       /* BollInfo bollInfo = new BollInfo();
        bollInfo.setName("test Boll");
        bollInfo.setTop(3);
        bollInfo.setBottom(1);
        bollInfo.setMiddle(2);
        bollInfo.setTime(10000);
        BollDb.getInstance(this).addRecord(bollInfo);*/
//        Log.d("big", "dataInfo-->" + BollDb.getInstance(this).getRecord());
        new Thread() {
            @Override
            public void run() {
                String ret = DataUtils.readFile(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/watch", "test.txt");
                addDataInfo(ret);
                getBollInfo(2, DataInfoDb.getInstance(HomeActivity.this).getRecord());
//                Log.d("big", "dataInfo-->" + DataInfoDb.getInstance(HomeActivity.this).getRecord());
            }
        }.start();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        Date date = new Date(Long.parseLong("1482826288494"));
        Log.d("big", "test-->" + format.format(date));
    }

    private void addDataInfo(String ret) {
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
            DataInfoDb.getInstance(HomeActivity.this).addRecord(info);
        }
    }

    private ArrayList<BollInfo> getBollInfo(int params, List<DataInfo> dataList) {
//        ArrayList<DataInfo> dataList = DataInfoDb.getInstance(HomeActivity.this).getRecord();
        ArrayList<BollInfo> bollList = new ArrayList<>();
        float currentTotal = 0;
        for (int i = 0; i < dataList.size(); i++) {
            currentTotal += dataList.get(i).getEnd();
            if (i < params - 1) continue;
            if (i > params - 1) {
                currentTotal -= dataList.get(i - params).getEnd();
            }
            BollInfo bollInfo = new BollInfo();
            bollList.add(bollInfo);
            float MA = currentTotal / params;
            float sum = 0;
            for (int j = i + 1 - params; j <= i; j++) {
                sum += (dataList.get(j).getEnd() - MA) * (dataList.get(j).getEnd() - MA);
            }
            float MD = (float) Math.sqrt(sum / params);
            int k = 2;
            bollInfo.setName(dataList.get(i).getName());
            bollInfo.setTime(dataList.get(i).getTime());
            bollInfo.setMiddle(MA);
            bollInfo.setTop(MA + MD * k);
            bollInfo.setBottom(MA - MD * k);
            Log.d("big", "i==" + i + ",boll-->" + bollInfo);
        }
        return bollList;
    }
    private void getMacdInfo(){

    }
}
