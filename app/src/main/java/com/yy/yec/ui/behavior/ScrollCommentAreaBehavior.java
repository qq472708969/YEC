package com.yy.yec.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.yy.yec.utils.UIUtil;

/**
 * 滚动时控制隐藏显示的Behavior
 * Created by zzq on 16/2/17.
 */
public class ScrollCommentAreaBehavior extends CoordinatorLayout.Behavior<View> {
    private boolean mIsAnimatingOut;
    private boolean mIsScrollToBottom;

    public ScrollCommentAreaBehavior() {
        super();
    }

    public ScrollCommentAreaBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, final View child, View dependency) {
        if (child != null && dependency != null && dependency instanceof NestedScrollView) {
            NestedScrollView s = (NestedScrollView) dependency;
            if (s.getChildCount() > 0) {
                View view = s.getChildAt(s.getChildCount() - 1);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom() + child.getHeight());
            }

            s.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v.getChildCount() > 0) {
                        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
                        View view = v.getChildAt(v.getChildCount() - 1);
                        // Calculate the scrolldiff
                        int diff = (view.getBottom() - (v.getHeight() + scrollY));
                        // if diff is zero, then the bottom has been reached
                        if (diff == 0) {
                            // notify that we have reached the bottom
                            UIUtil.animateBottomIn(child);
                            mIsScrollToBottom = true;
                        } else {
                            mIsScrollToBottom = false;
                        }
                    }
                }
            });
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (mIsScrollToBottom) return;
        float mPreTranslationY = dy + child.getTranslationY();
        if (mPreTranslationY <= 0) {
            child.setTranslationY(0);
            mIsAnimatingOut = true;
        }
        if (mPreTranslationY >= child.getHeight()) {
            child.setTranslationY(child.getHeight());
            mIsAnimatingOut = false;
        }
        if (mPreTranslationY > 0 && mPreTranslationY < child.getHeight()) {
            child.setTranslationY(mPreTranslationY);
            mIsAnimatingOut = dy > 0;
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        //隐藏软键盘
        UIUtil.hideSoftKeyboard(coordinatorLayout);
        //保证纵向滑动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (child.getTranslationY() == 0 || child.getTranslationY() == child.getHeight()) return;

        if (mIsAnimatingOut)
            UIUtil.animateBottomOut(child);
        else
            UIUtil.animateBottomIn(child);
    }
}