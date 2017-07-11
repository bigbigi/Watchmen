package com.auto.watchmen.util;

import java.io.Closeable;

/**
 * Created by dage on 2017/7/10.
 */
public class Utils {
    public static void closeIO(Closeable ioStream) {
        if (ioStream != null) {
            try {
                ioStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static float parseFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
