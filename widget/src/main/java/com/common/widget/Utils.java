package com.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zzq on 2016/10/3.
 */
public class Utils {
    public static float dpToPx(Resources res, float dp) {
        return dp * getDisplayMetrics(res).density;
    }

    public static float pxToDp(Resources res, float px) {
        return px / getDisplayMetrics(res).density;
    }

    public static DisplayMetrics getDisplayMetrics(Resources res) {
        return res.getDisplayMetrics();
    }

    public static boolean hasInternet(Context context) {
        if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null)
            return true;
        else
            return false;
    }

    public static void showSoftKeyboard(View view) {
        if (view == null) return;
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        if (!view.isFocused()) view.requestFocus();
        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, 0);
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
        InputMethodManager manager = (InputMethodManager) mFocusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mFocusView.getWindowToken(), 0);
    }
}