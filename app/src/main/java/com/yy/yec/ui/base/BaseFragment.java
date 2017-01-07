package com.yy.yec.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by zzq on 2016/10/3.
 */
public abstract class BaseFragment extends Fragment {
    private View mRoot;

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoot = getLayoutInflater(savedInstanceState).inflate(setContentView(), null);//加载根布局
        ButterKnife.bind(this, mRoot);
        readInstanceState(savedInstanceState);//用于系统kill掉activity之后的状态恢复
        if (!getBundle(getArguments())) return;//获取给Fragment传递的bundle
        init(mRoot);//初始化机会
    }

    protected boolean getBundle(Bundle arguments) {
        return true;
    }

    protected void readInstanceState(Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRoot;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initComplete(view, savedInstanceState);//每次加载Fragment都会执行
    }

    @Override
    public void onDestroy() {//它和onCreate都只执行一次，剩下的只是从新执行了FragmentView的生命周期部分
        super.onDestroy();
        ButterKnife.unbind(this);
        mRoot = null;
    }

    protected void initComplete(View view, @Nullable Bundle savedInstanceState) {
    }

    protected void init(View contentView) {
    }

    @LayoutRes
    protected abstract int setContentView();
}