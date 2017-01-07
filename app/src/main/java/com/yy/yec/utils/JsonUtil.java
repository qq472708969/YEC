package com.yy.yec.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by zzq on 2016/10/16.
 */
public class JsonUtil {
    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }
}
