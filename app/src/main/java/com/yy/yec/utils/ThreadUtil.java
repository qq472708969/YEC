package com.yy.yec.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zzq on 2016/10/5.
 */
public class ThreadUtil {
    public static ExecutorService getFixedThreadPool(int numMax) {
        return Executors.newFixedThreadPool(numMax);
    }
}
