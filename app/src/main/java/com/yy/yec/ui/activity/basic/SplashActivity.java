package com.yy.yec.ui.activity.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yy.yec.R;
import com.yy.yec.global.App;
import com.yy.yec.global.manager.AppActivityManager;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.service.UploadLogService;
import com.yy.yec.utils.UIUtil;

import org.kymjs.kjframe.http.KJAsyncTask;

import java.io.File;

public class SplashActivity extends Activity {
    @Override
    public void onBackPressed() {
        System.exit(0);//退出进程
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!App.context().getSplashShow()) { //应用首次启动不展示splash页面，以loadDex进程中的activity作为启动页面
            redirectTo();
            return;
        }
        Activity main = AppActivityManager.getInstance().getActivity(MainActivity.class);
        if (main != null && !main.isFinishing()) {
            finish();//防止main多实例，从新启动应用
        }

        View view = UIUtil.inflate(this, R.layout.activity_start);
        setContentView(view);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 900);
        //Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f  View的中心
        // ScaleAnimation sa = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // sa.setDuration(3800);
        //AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        //aa.setDuration(2800);

//        AnimationSet as = new AnimationSet(true);
//        as.addAnimation(sa);
//        as.addAnimation(aa);
//        view.startAnimation(as);
//        as.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                redirectTo();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearImageCache();
    }

    private void clearImageCache() {
        final File imageCacheDir = App.packgeDir(AppVariableConstants.imageCacheDir);
        File[] files = imageCacheDir.listFiles();//获得文件的数组

        if (files != null && files.length > 0)
            KJAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (imageCacheDir.isDirectory()) {
                        for (File file : imageCacheDir.listFiles()) {
                            file.delete();//清除
                        }
                    }
                }
            });
    }

    private void redirectTo() {
        Intent uploadLog = new Intent(this, UploadLogService.class);//post服务器，上传日志
        startService(uploadLog);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}