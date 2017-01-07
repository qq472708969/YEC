package com.yy.yec.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.entity.xml.Notice;
import com.yy.yec.global.App;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.global.AppVariableConstants;

/**
 * Created by zzq on 2016/9/24.
 */
public class UIUtil {
    private static Interpolator interpolator;

    public static View inflate(Context ctx, int res) {
        return View.inflate(ctx, res, null);
    }

    public static void copyText(String string) {
        if (TextUtils.isEmpty(string)) return;
        ClipboardManager clip = (ClipboardManager) App.context()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(string);
        ToastUtil.showToast(R.string.copy_success);
    }

    /***
     * 获取一个耗时等待对话框
     */
    public static ProgressDialog getProgressDialog(Context context) {
        return new ProgressDialog(context);
    }

    public static void animateBottomOut(final View v) {
        if (interpolator == null) {
            interpolator = new DecelerateInterpolator();
        }
        v.animate()
                .translationY(v.getHeight())
                .setInterpolator(interpolator)
                .setDuration(110)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setTranslationY(v.getHeight());
                    }
                });
    }

    public static void animateBottomIn(final View v) {
        if (interpolator == null) {
            interpolator = new DecelerateInterpolator();
        }
        v.animate()
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(110)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setTranslationY(0);
                    }
                });
    }

    public static void showSoftKeyboard(View view) {
        if (view == null) return;
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        if (!view.isFocused()) view.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    public static void hideSoftKeyboard(View view) {
        if (view == null) return;
        View mFocusView = view;

        Context context = view.getContext();
        if (context != null && context instanceof Activity) {
            Activity activity = ((Activity) context);
            mFocusView = activity.getCurrentFocus();
        }
        if (mFocusView == null) return;
        mFocusView.clearFocus();
        InputMethodManager manager = (InputMethodManager) mFocusView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mFocusView.getWindowToken(), 0);
    }

    /**
     * update textColor
     *
     * @param title   title
     * @param content content
     */
    public static void updateContentColor(@ColorRes int color, TextView title, TextView content) {
        if (title != null) {
            title.setTextColor(App.resources().getColor(color));
        }
        if (content != null) {
            content.setTextColor(App.resources().getColor(color));
        }
    }

    /**
     * 发送通知广播
     */
    public static void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        App.context().sendBroadcast(intent);
    }

    /**
     * 发送通知广播
     *
     * @param notice
     */
    public static void sendBroadCast(Notice notice) {
        App ctx = (App) App.context();
        if (!AppCurrentUser.getInstance().getLoginOK() || notice == null)
            return;
        Intent intent = new Intent(AppVariableConstants.INTENT_ACTION_NOTICE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice_bean", notice);
        intent.putExtras(bundle);
        ctx.sendBroadcast(intent);
    }

    public static void showConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.ok), onClickListener);
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.show();
    }
//    /**
//     * 判断是否为主线程
//     *
//     * @return boolean
//     */
//    public static boolean isUIThread() {
//        int myTid = android.os.Process.myTid();
//        if (myTid == App.getMainThreadId())
//            return true;
//        return false;
//    }
//
//    /**
//     * @param r 当前要执行的任务对象
//     */
//    public static void runUIThread(Runnable r) {
//        if (isUIThread())
//            r.run();
//        else
//            App.getHandler().post(r); // 如果是子线程, 借助handler让其运行在主线程
//    }
}
