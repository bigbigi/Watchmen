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
                float current = 1;
                long time = Long.parseLong("1482826288494");
                for (int i = 0; i < 240; i++) {
                    DataInfo info = new DataInfo();
                    info.setName("test");
                    info.setBegin(current);
                    current += 10 /*+ Math.random()*/;
                    info.setEnd(current);
                    info.setMax(Math.max((float) (current + Math.random()), current));
                    info.setMin((float) (current - Math.random()));
                    info.setTime(time);
                    time += 60 * 60 * 1000;
                    DataInfoDb.getInstance(HomeActivity.this).addRecord(info);
                }
                ArrayList<DataInfo> dateInfos = DataInfoDb.getInstance(HomeActivity.this).getDateRecord();
                for (DataInfo info : dateInfos) {
                    Log.d("big", "info:" + info.toString());
                }
            }
        }.start();

       /* new Thread() {
            @Override
            public void run() {
                String ret = DataUtils.readFile(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/watch", "test.txt");
                addDataInfo(ret);
                List<DataInfo> list = DataInfoDb.getInstance(HomeActivity.this).getRecord();
                List<DataInfo> weekList = getWeekDataInfo(list);
                getBollInfo(99, weekList);
                getMacdInfo(1, 1, 1, weekList);
                getBollInfo(99, list);
                getMacdInfo(1, 1, 1, list);
            }
        }.start();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        Date date = new Date(Long.parseLong("1482826288494"));
        Log.d("big", "test-->" + format.format(date));*/
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

    private void getMacdInfo(int param1, int param2, int param3, List<DataInfo> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            DataInfo dataInfo = dataList.get(0);
            if (i == 0) {
                dataInfo.setEmaFast(dataInfo.getEnd());
                dataInfo.setEmaSlow(dataInfo.getEnd());
                continue;
            }
            float emaFast = getEma(param1, dataList.get(i - 1).getEmaFast(), dataInfo.getEnd());
            float emaSlow = getEma(param2, dataList.get(i - 1).getEmaSlow(), dataInfo.getEnd());
            float dea = getEma(param3, dataList.get(i - 1).getDea(), dataInfo.getDif());
            dataInfo.setEmaFast(emaFast);
            dataInfo.setEmaSlow(emaSlow);
            dataInfo.setDif(emaFast - emaSlow);
            dataInfo.setDea(dea);
            dataInfo.setMacd(2 * (dataInfo.getDif() - dataInfo.getDea()));
        }
    }

    public float getEma(int param, float lastEma, float end) {
        return lastEma * (param - 1) / (param + 1) + end * 2 / (param + 1);
    }

    private List<DataInfo> getWeekDataInfo(List<DataInfo> dataList) {
        List<DataInfo> weekList = new ArrayList<DataInfo>();
        Calendar calendar = Calendar.getInstance();
        float min = 0, max = 0, start = 0;
        for (int i = 0; i < dataList.size(); i++) {
            DataInfo info = dataList.get(i);
            min = Math.min(min, info.getEnd());
            max = Math.max(max, info.getEnd());
            calendar.setTime(new Date(info.getTime()));
            if (start == 0) {
                start = info.getBegin();
            }
            if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
                DataInfo weekInfo = new DataInfo();
                weekInfo.setTime(info.getTime());
                weekInfo.setEnd(info.getEnd());
                weekInfo.setBegin(start);
                weekInfo.setMin(min);
                weekInfo.setMax(max);
                start = min = max = 0;
            }
        }
        return weekList;

    }

    private boolean check(List<DataInfo> dateList, List<DataInfo> weekList) {
        boolean result = false;
        DataInfo lastWeek = weekList.get(weekList.size() - 1);
        DataInfo lastDate = dateList.get(dateList.size() - 1);
        boolean isWeekAbove = lastWeek.getEnd() > lastWeek.getMa();
        boolean isDateAbove = lastDate.getEnd() > lastDate.getMa();
        boolean isWeekArise = lastWeek.getMa() > weekList.get(weekList.size() - 2).getMa();
        boolean isDateArise = lastDate.getMa() > dateList.get(dateList.size() - 2).getMa();
        if (isWeekAbove == isWeekArise && isDateAbove == isDateArise && isWeekAbove == isDateAbove) {
            if (isWeekArise) {
                if (lastDate.getMacd() >= 0 && dateList.get(dateList.size() - 2).getMacd() <= 0) {
                    result = true;
                }
            } else {
                if (lastDate.getMacd() <= 0 && dateList.get(dateList.size() - 2).getMacd() >= 0) {
                    result = true;
                }
            }
        }
        return result;
    }
}
