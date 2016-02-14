package com.find.util;

import android.os.Environment;

/**
 * Created by maqiang on 2015/2/3.
 * SDCard 相关工具类
 */
public class SDCard {

    private SDCard(){}

    public static String SD_ROOT () {
        try {
            return Environment.getExternalStorageDirectory().getCanonicalPath()+"/Find";
        } catch(Exception e) {
            return null;
        }
    }

    //判断SDCard是否存在
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
