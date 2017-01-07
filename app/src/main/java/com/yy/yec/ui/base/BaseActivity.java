package com.yy.yec.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.yy.yec.global.manager.AppActivityManager;

import butterknife.ButterKnife;

/**
 * Created by zzq on 2016/9/28.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!initBundle(getIntent().getExtras())) {//启动Activity传递过来的数据为空直接关闭该activity
            finish();
            return;
        }
        int rootId = setContentView();
        if (rootId != 0) setContentView(rootId);
        AppActivityManager.getInstance().addActivity(this);
        ButterKnife.bind(this);
        init(savedInstanceState);
    }

    protected boolean initBundle(Bundle bundle) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppActivityManager.getInstance().finishActivity(this);
    }

    @LayoutRes
    protected abstract int setContentView();

    protected abstract void init(Bundle savedInstanceState);
}