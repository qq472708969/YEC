package com.yy.yec.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 适配PictureView的使用
 * zzq  16/10/25.
 */
public class PictureViewPager extends ViewPager {
    public PictureViewPager(Context context) {
        this(context, null);
    }

    public PictureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) //只有在多点触摸的时候才会发生越界，所以多点触摸的时候始终拦截ViewPager的事件处理
            requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }
}