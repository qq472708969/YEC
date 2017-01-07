package com.yy.yec.ui.widget;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

/**
 * Created by zzq on 2016/9/27.
 */
public class SimpleFragmentTabHost extends FragmentTabHost {

    private String mCurrentTag;

    private String mNoTabChangedTag;

    public SimpleFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTabChanged(String tag) {
        if (tag.equals(mNoTabChangedTag)) {
            setCurrentTabByTag(mCurrentTag);
        } else {
            super.onTabChanged(tag);
            mCurrentTag = tag;
        }
    }

    public void setNoTabChangedTag(String tag) {
        this.mNoTabChangedTag = tag;//中间的特殊按钮
    }
}