package com.yy.yec.global;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.yy.yec.R;

import com.yy.yec.utils.TLog;

/**
 * Created by zzq on 2016/11/7.
 */
public class LoadDexActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TLog.error("LoadDexActivity创建成功");
        setContentView(R.layout.activity_start);
        new LoadDexTask().execute();
    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
//                Thread.sleep(15500);主进程不会ANR
                TLog.error("LoadDexTask任务开始执行");
                MultiDex.install(getApplication());
                TLog.error("LoadDexTask任务执行完成");
                ((App) getApplication()).firstLoadDexFinish();//存储首次加载完成标记
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            TLog.error("LoadDexActivity即将销毁   :loadDex进程即将退出");
            killSelf();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void killSelf() {
        finish();
        System.exit(0);//退出进程
    }
}