package com.yy.yec.global;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zzq on 2016/10/12.
 */
public class AppThreadPool {
    private static ExecutorService _this;

    public static Executor getInstance() {
        if (_this == null)
            synchronized (AppThreadPool.class) {
                _this = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > 0 ? Runtime.getRuntime().availableProcessors() : 3);
            }
        return _this;
    }

    public static void execute(Runnable runnable) {
        getInstance().execute(runnable);
    }
}
