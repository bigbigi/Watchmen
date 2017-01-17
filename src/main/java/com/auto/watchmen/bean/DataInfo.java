package com.auto.watchmen.bean;

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
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name).append(",time=").append(String.valueOf(time)).append(",begin=").
                append(begin).append(",max=").append(max).append(",min=").append(min).append(",end=").append(end);
        return sb.toString();
    }
}
