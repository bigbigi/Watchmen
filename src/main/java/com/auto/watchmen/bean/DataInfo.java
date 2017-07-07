package com.auto.watchmen.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dage on 2016/12/30.
 */
public class DataInfo {
    //算法：
    //time：
    //begin
    //end
    //max
    //min
    private String name;
    private long time;
    private float begin;
    private float end;
    private float max;
    private float min;
    private float ma;
    private float emaSlow;
    private float emaFast;
    private float dif;
    private float dea;
    private float macd;
    private String date;

    private SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    public DataInfo() {
    }

    public DataInfo(float begin, float end, float max, float min, String name, long time) {
        this.begin = begin;
        this.end = end;
        this.max = max;
        this.min = min;
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBegin() {
        return begin;
    }

    public void setBegin(float begin) {
        this.begin = begin;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        date = DATA_FORMAT.format(new Date(time));
    }


    public float getDea() {
        return dea;
    }

    public void setDea(float dea) {
        this.dea = dea;
    }

    public float getDif() {
        return dif;
    }

    public void setDif(float dif) {
        this.dif = dif;
    }

    public float getEmaFast() {
        return emaFast;
    }

    public void setEmaFast(float emaFast) {
        this.emaFast = emaFast;
    }

    public float getEmaSlow() {
        return emaSlow;
    }

    public void setEmaSlow(float emaSlow) {
        this.emaSlow = emaSlow;
    }

    public float getMa() {
        return ma;
    }

    public void setMa(float ma) {
        this.ma = ma;
    }

    public float getMacd() {
        return macd;
    }

    public void setMacd(float macd) {
        this.macd = macd;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name).append(",time=").append(String.valueOf(time)).append(",begin=").
                append(begin).append(",max=").append(max).append(",min=").append(min).append(",end=").append(end)
                .append(",date=").append(date);
        return sb.toString();
    }
}
