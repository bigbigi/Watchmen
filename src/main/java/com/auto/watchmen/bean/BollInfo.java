package com.auto.watchmen.bean;

/**
 * Created by dage on 2016/12/30.
 */
public class BollInfo {
    private String name;
    private long time;
    private float top;
    private float bottom;
    private float middle;

    //middle<top&&middle>bottom//TODO

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getMiddle() {
        return middle;
    }

    public void setMiddle(float middle) {
        this.middle = middle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name).append(",time=").append(String.valueOf(time)).append(",top=").
                append(top).append(",bottom=").append(bottom).append(",middle=").append(middle);
        return sb.toString();
    }
}
