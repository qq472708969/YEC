package com.yy.yec.ui.activity.detail.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;

import com.yy.yec.ui.base.BaseActivity;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.UIUtil;

/**
 * Created by zzq on 2016/10/25.
 */
public abstract class BaseBackActivity extends BaseActivity {
    private ActionBar actionBar;
    private String actionBarTitle;
    private boolean showBackBtn = true;
    private ProgressDialog pd;

    @Override
    protected final void init(Bundle savedInstanceState) {
        actionBar = getSupportActionBar();
        if (actionBar == null) return;
        if (showBackBtn) {
            actionBar.setDisplayUseLogoEnabled(true);//启用自定义图标
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回箭头图标并让其可用
        }
        initWidget();
        initData(savedInstanceState);
        actionBar.setTitle(actionBarTitle);//给子类设置标题的机会
    }

    @StringRes
    protected final void setActionBarTitle(int title) {
        actionBarTitle = getString(title);
    }

    protected final void setActionBarTitle(String title) {
        actionBarTitle = title;
    }

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initWidget();

    public void setShowBackBtn(boolean show) {
        showBackBtn = show;
    }

    //-----view tool
    public void showWaitProgressDialog(String msg) {
        if (StringUtils.isEmpty(msg)) return;
        if (pd == null) {
            pd = UIUtil.getProgressDialog(this);
            pd.setCancelable(false);//不允许触碰取消
        }
        pd.setMessage(msg);
        pd.show();
    }

    public void hideWaitProgressDialog() {
        if (pd != null) pd.hide();
    }

    //----------
    @Override
    public final boolean onSupportNavigateUp() {//返回键退出activity
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        pd = null;
        actionBar = null;
        actionBarTitle = null;
        super.onDestroy();
    }
}