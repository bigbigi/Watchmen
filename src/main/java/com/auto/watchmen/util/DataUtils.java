package com.auto.watchmen.util;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by dage on 2017/1/3.
 */
public class DataUtils {

    public static String readFile(String dir, String name) {
        FileInputStream fis = null;
        InputStreamReader isReader = null;
        BufferedReader bufferReader = null;
        String ret = "";
        try {
            fis = new FileInputStream(new File(dir + "/" + name));
            String line;
            StringBuffer sb = new StringBuffer();
            isReader = new InputStreamReader(fis);
            bufferReader = new BufferedReader(isReader);
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            ret = sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferReader);
            closeIO(isReader);
            closeIO(fis);
        }
        return ret;
    }

    public static void closeIO(Closeable ioStream) {
        if (ioStream != null) {
            try {
                ioStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
