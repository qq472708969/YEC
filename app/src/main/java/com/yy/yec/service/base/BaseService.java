package com.yy.yec.service.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/9/27.
 */
public abstract class BaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//bindservice不能使用这个方法，否则不糊跟随activity的生命周期消亡
        executeMainThreadTask();

        return super.onStartCommand(intent, flags, startId);
    }

    protected void executeFinish() {
        stopSelf();
    }

    public abstract void executeMainThreadTask();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
