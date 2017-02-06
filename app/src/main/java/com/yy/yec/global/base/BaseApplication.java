package com.yy.yec.global.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDex;

import com.yy.yec.global.manager.AppActivityManager;

import java.io.File;

/**
 * Created by zzq on 2016/9/24.
 */
public abstract class BaseApplication extends Application {//dex方法数超过65536问题
    protected static Context mContext;
    protected static Resources mResource;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);//加载多dex
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mResource = getResources();
        onCreateApp();
    }

    protected abstract void onCreateApp();

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