package com.auto.watchmen.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by big on 2019/7/30.
 */

public class MaStratery implements Strategy {

    @Override
    public int getTrend() {
        return 0;
    }

    @Override
    public boolean needAct() {
        return false;
    }

    @Override
    public float getSuggestInPrice() {
        return 0;
    }

    @Override
    public float getSuggestOutPrice() {
        return 0;
    }

    @Override
    public float getProbability(int profitRate) {
        return 0;
    }

    @Override
    public float getTopPrice() {
        return 0;
    }

    @Override
    public float getBottomPrice() {
        return 0;
    }

    @Override
    public int getRelationShip() {
        return 0;
    }

    @Override
    public void calculate() {
        //just be used for index,because fuquan
        List<Float> mTestList = new ArrayList<>();
        int index = mTestList.size();
        float MA60 = 0;
        float today = mTestList.get(mTestList.size() - 1);
        float curValue = today;
        float maxValue = curValue;
        float maxIndex = index;
        if (curValue < MA60) {
            while (curValue < MA60) {
                index--;
                curValue = mTestList.get(index);
                if (curValue > maxValue) {
                    maxValue = curValue;
                    maxIndex = index;//可以取到对应时间
                }
            }
        }
        if (maxValue == today) {
            //todo alert tuppo
        } else if (mTestList.size() - maxIndex < 7) {
            if ((maxValue - today) / maxIndex < 0.05) {
                //todo alert
            }
        }
    }
}
