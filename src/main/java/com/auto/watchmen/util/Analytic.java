package com.auto.watchmen.util;

import android.util.Log;

import com.auto.watchmen.bean.BollInfo;
import com.auto.watchmen.bean.DataInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dage on 2017/7/17.
 */
public class Analytic {
    private static final int TREND_UP = 1;
    private static final int TREND_DOWN = -1;
    private static final int TREND_NO = 0;

    public static ArrayList<BollInfo> getBollInfo(int params, List<DataInfo> dataList) {
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

    public static void getMacdInfo(int param1, int param2, int param3, List<DataInfo> dataList) {
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

    public static float getEma(int param, float lastEma, float end) {
        return lastEma * (param - 1) / (param + 1) + end * 2 / (param + 1);
    }

    public static List<DataInfo> getWeekDataInfo(List<DataInfo> dataList) {
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

    public static boolean check(List<DataInfo> dateList, List<DataInfo> weekList) {
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

    public static int checkBollTrend(List<DataInfo> dataList, HashMap<Long, BollInfo> bollMap) {
        int trend = TREND_NO;
        DataInfo lastDate = dataList.get(dataList.size() - 1);
        DataInfo lastSecondDate = dataList.get(dataList.size() - 2);
        BollInfo lastBoll = bollMap.get(lastDate.getTime());
        BollInfo lastSecondBoll = bollMap.get(lastSecondDate.getTime());
        boolean bollUp = lastBoll.getMiddle() > lastSecondBoll.getMiddle();
        boolean dataAboveBoll = lastDate.getEnd() > lastBoll.getMiddle();
        if (bollUp && dataAboveBoll) {
            trend = TREND_UP;
        } else if (!bollUp && !dataAboveBoll) {
            trend = TREND_DOWN;
        }
        return trend;
    }

    public static int checkMacd(DataInfo lastDate, DataInfo lastSecondDate) {
        int trend = TREND_NO;
        if (lastDate.getMacd() >= 0 && lastSecondDate.getMacd() <= 0) {
            trend = TREND_UP;
        } else if (lastDate.getMacd() <= 0 && lastSecondDate.getMacd() >= 0) {
            trend = TREND_DOWN;
        }
        return trend;
    }
}
