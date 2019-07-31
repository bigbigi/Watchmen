package com.auto.watchmen.strategy;

/**
 * Created by big on 2019/7/25.
 */

public interface Strategy {
    int getTrend();

    boolean needAct();

    float getSuggestInPrice();

    float getSuggestOutPrice();

    float getProbability(int profitRate);//成功概率

    float getTopPrice();

    float getBottomPrice();

    int getRelationShip();

    void calculate();
}
