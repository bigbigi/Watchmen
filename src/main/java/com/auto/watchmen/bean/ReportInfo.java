package com.auto.watchmen.bean;

/**
 * Created by dage on 2017/7/11.
 */
public class ReportInfo {
    private String name;
    private int boll;
    private int macd;
    private int weekBoll;
    private int weekMacd;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getBoll() {
        return boll;
    }

    public int getMacd() {
        return macd;
    }

    public int getWeekBoll() {
        return weekBoll;
    }

    public int getWeekMacd() {
        return weekMacd;
    }
}
