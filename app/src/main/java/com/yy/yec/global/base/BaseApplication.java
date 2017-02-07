package com.yy.yec.global.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDex;

import com.yy.yec.global.LoadDexActivity;
import com.yy.yec.global.manager.AppActivityManager;
import com.yy.yec.utils.TLog;

import java.io.File;

/**
 * Created by zzq on 2016/9/24.
 */
public abstract class BaseApplication extends Application {
    protected static Context mContext;
    protected static Resources mResource;
    private boolean splashShow = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mainProcess() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//app运行后主线程先启动，api21以上使用multiDex
            if (firstLoadDex()) {
                execDexOpt();
                splashShow = false;//splash页面不启动
            }
            MultiDex.install(this);//只有首次加载时时间消耗巨大
            TLog.error("attachBaseContext 中快速执行 MultiDex-install ");
        }
    }

    private String getCurrProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid)
                return appProcess.processName;
        }
        return null;
    }

    /**
     * @return true 为首次加载dex
     */
    public boolean firstLoadDex() {
        SharedPreferences sp = getSharedPreferences("firstLoadDex", MODE_MULTI_PROCESS);
        String saveValue = sp.getString("firstLoadDex", "false");
        return saveValue == "false";
    }

    /**
     * 首次加载完成
     */
    public void firstLoadDexFinish() {
        SharedPreferences sp = getSharedPreferences("firstLoadDex", MODE_MULTI_PROCESS);
        sp.edit().putString("firstLoadDex", "true").commit();
    }

    /**
     * 判断是否是主进程
     *
     * @return
     */
    public boolean mainProcess() {
        return getCurrProcessName().indexOf(":loadDex") < 0;
    }

    /**
     * 开启一个额外进程的activity加载dex。等待中
     */
    private void execDexOpt() {
        Intent intent = new Intent();//ComponentName启动组件
        ComponentName componentName = new ComponentName("com.yy.yec", LoadDexActivity.class.getName());
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        long startWait = System.currentTimeMillis();
        TLog.error(":loadDex进程LoadDexActivity已经启动");
        while (firstLoadDex()) {
            if (System.currentTimeMillis() - startWait > 39 * 1000) {//大于39秒直接退出
                TLog.error("LoadDexTask任务监测超时！");
                System.exit(0);
                return;
            }
            TLog.error("LoadDexTask任务监测中。。。");
        }
        TLog.error("监测:loadDex进程activity中的异步任务MultiDex-install 执行完成！！！");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (firstLoadDex()) return;
        TLog.error("onCreate方法正常执行");
        mContext = getApplicationContext();
        mResource = getResources();
        onCreateApp();
    }

    protected abstract void onCreateApp();

    /**
     * splash
     *
     * @return
     */
    public boolean getSplashShow() {
        return splashShow;
    }

    public static synchronized BaseApplication context() {
        return (BaseApplication) mContext;
    }

    public static Resources resources() {
        return mResource;
    }

    public static File cacheDir() {
        return mContext.getCacheDir();
    }

    /**
     * 创建 MODE_PRIVATE 的目录
     *
     * @param dirName
     * @return
     */
    public static File packgeDir(String dirName) {
        return mContext.getDir(dirName, Context.MODE_PRIVATE);
    }

    public static File filesDir() {
        return mContext.getFilesDir();
    }

    public static String string(@StringRes int resId) {
        return mContext.getString(resId);
    }

    @NonNull
    public static String string(@StringRes int resId, Object... formatArgs) {
        return mContext.getString(resId, formatArgs);
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            AppActivityManager.getInstance().finishAllActivity();
            System.exit(0);
        } catch (Exception e) {
        }
    }
}