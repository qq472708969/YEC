package com.yy.yec.ui.fragment.detail.base;

import android.view.View;

import com.yy.yec.ui.base.BaseFragment;

/**
 * Created by zzq on 2016/10/5.
 */
public abstract class BaseDetailFragment extends BaseFragment {
    @Override
    protected void init(View contentView) {//Adapter缓存之后不再执行onCreate和onDestroy，只从onCreateView开始重复执行
        super.init(contentView);
        initWidget(contentView);
        initData();
    }

    protected abstract void initData();

    protected abstract void initWidget(View view);
}