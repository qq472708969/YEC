package com.common.widget.comment.brow;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zzq on 2016/12/3.
 */
public class BrowViewPager extends ViewPager {
    public BrowViewPager(Context context) {
        super(context);
    }

    public BrowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {//自己滑动的时候，父级ViewPager不滑动
        super.onTouchEvent(ev);
        return false;
    }
}