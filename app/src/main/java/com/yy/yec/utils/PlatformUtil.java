package com.yy.yec.utils;

import android.view.View;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.global.App;

/**
 * Created by zzq on 16/10/21.
 */
public class PlatformUtil {
    public final static int CLIENT_MOBILE = 2;
    public final static int CLIENT_ANDROID = 3;
    public final static int CLIENT_IPHONE = 4;
    public final static int CLIENT_WINDOWS_PHONE = 5;
    public final static int CLIENT_WECHAT = 6;

    public static void setPlatFormString(TextView tv, int platform) {
        int resId = R.string.form_mobile;
        tv.setVisibility(View.VISIBLE);
        switch (platform) {
            case CLIENT_MOBILE:
                resId = R.string.form_mobile;
                break;
            case CLIENT_ANDROID:
                resId = R.string.form_android;
                break;
            case CLIENT_IPHONE:
                resId = R.string.form_iphone;
                break;
            case CLIENT_WINDOWS_PHONE:
                resId = R.string.form_windows_phone;
                break;
            case CLIENT_WECHAT:
                resId = R.string.form_wechat;
                break;
            default:
                tv.setVisibility(View.GONE);
        }
        tv.setText(App.resources().getString(resId));
    }
}