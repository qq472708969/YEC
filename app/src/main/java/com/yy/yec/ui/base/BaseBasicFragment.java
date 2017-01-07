package com.yy.yec.ui.base;

import android.view.View;

import com.yy.yec.R;

/**
 * Created by zzq on 2016/10/3.
 */
public abstract class BaseBasicFragment extends BaseFragment {
    @Override
    protected void init(View contentView) {
        super.init(contentView);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_basic;
    }
}