package com.yy.yec.dataexchange.rule;

import com.loopj.android.http.AsyncHttpClient;
import com.yy.yec.global.App;

import java.util.Locale;

/**
 * Created by zzq on 2016/9/24.
 */
public class aaaHttpOperate {
    public final static String mcHost = "www.oschina.net";
    private static String mcApiUrl = "http://www.oschina.net/%s";
    public static final String mcGet = "GET";
    public static final String mcPost = "POST";
    private static AsyncHttpClient mAhc;

    private aaaHttpOperate() {
    }

    public static AsyncHttpClient getAsyncHttpClient() {
        return mAhc;
    }

    public static void setAsyncHttpClient(AsyncHttpClient ahc) {
        mAhc = ahc;
        mAhc.addHeader("Accept-Language", Locale.getDefault().toString());//获取本地国家代码
        mAhc.addHeader("Host", mcHost);
        mAhc.addHeader("Connection", "Keep-Alive");
        mAhc.setEnableRedirects(true);//重定向都被允许
        mAhc.setUserAgent(getUserAgent());
    }

    /**
     * 获得请求的服务端数据的userAgent
     */
    private static String getUserAgent() {
        App app = (App) App.context();
        StringBuilder ua = new StringBuilder("OSChina.NET");
        ua.append('/' + app.packageInfo().versionName + '_'
                + app.packageInfo().versionCode);// app版本信息
        ua.append("/Android");// 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
        ua.append("/" + android.os.Build.MODEL); // 手机型号
        ua.append("/" + app.id());// app客户端Id
        return ua.toString();
    }
}
