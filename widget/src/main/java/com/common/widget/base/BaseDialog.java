package com.common.widget.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;

import butterknife.ButterKnife;

/**
 * used ButterKnife register
 *
 * Created by zzq on 2016/9/29.
 */
public abstract class BaseDialog extends Dialog {
    private final View mRoot;
    private boolean enableButterKnife = true;

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        mRoot = View.inflate(context, setContentView(), null);
        if (mRoot != null) setContentView(mRoot);
        if (enableButterKnife)
            ButterKnife.bind(this, mRoot);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);//按返回键可以退出
        setCanceledOnTouchOutside(true);//设置点击dialog之外退出
        init(savedInstanceState, getWindow());
    }

    protected abstract void init(Bundle savedInstanceState, Window currWin);

    @Override
    public void dismiss() {
        super.dismiss();
        if (enableButterKnife)
            ButterKnife.unbind(mRoot);
    }

    protected void enableButterKnife(boolean enable) {
        enableButterKnife = enable;
    }

    @LayoutRes
    protected abstract int setContentView();
}