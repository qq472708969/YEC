package com.yy.yec.global;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.yy.yec.utils.TLog;

import java.util.Locale;

/**
 * Created by zzq on 2016/9/24.
 */
public class AppHttpOperate {
    private static AppHttpOperate _this;
    private AsyncHttpClient mAhc;

    private AppHttpOperate() {
        mAhc = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(App.context());
        mAhc.setCookieStore(myCookieStore);//cookie自动保存到sharedpreferences中
        mAhc.addHeader("Accept-Language", Locale.getDefault().toString());//获取本地国家代码
        mAhc.addHeader("Host", AppVariableConstants.Host);
        mAhc.addHeader("Connection", "Keep-Alive");
        mAhc.setEnableRedirects(true);//重定向都被允许
        mAhc.setUserAgent(getUserAgent());
    }

    public static AppHttpOperate getInstance() {
        if (_this == null)
            synchronized (AppHttpOperate.class) {
                if (_this == null)
                    _this = new AppHttpOperate();
            }
        return _this;
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return mAhc;
    }

    /**
     * 登陆成功后请求api需要设置cookie
     *
     * @param cookie
     */
    public void setCookie(String cookie) {
        mAhc.addHeader("Cookie", cookie);
    }

    /**
     * 注销登陆成功后删除设置的cookie
     */
    public void clearCookie() {
        mAhc.removeHeader("Cookie");
    }

    /**
     * 获得请求的服务端数据的userAgent
     */
    private String getUserAgent() {
        App app = (App) App.context();
        StringBuilder ua = new StringBuilder("OSChina.NET");
        ua.append('/' + app.packageInfo().versionName + '_' + app.packageInfo().versionCode);// app版本信息
        ua.append("/Android");// 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
        ua.append("/" + android.os.Build.MODEL); // 手机型号
        ua.append("/" + app.id());// app客户端Id
        return ua.toString();
    }

    /**
     * 获取域名部分的Url
     *
     * @param partUrl
     * @return
     */
    private String getUrlHostPart(String partUrl) {
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) //如果前缀不为http的就认为是部分路径
            partUrl = String.format(AppVariableConstants.HostUrl, partUrl);
        return partUrl;
    }

    //get动作
    public void get(boolean partUrlOK, String url, AsyncHttpResponseHandler handler) {
        mAhc.get(partUrlOK ? getUrlHostPart(url) : url, handler);
        TLog.info(new StringBuilder("GET ").append(url).toString());
    }

    public void get(boolean partUrlOK, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        mAhc.get(partUrlOK ? getUrlHostPart(url) : url, params, handler);
        TLog.info(new StringBuilder("GET ").append(url).append("?").append(params).toString());
    }

    public void get(String url, AsyncHttpResponseHandler handler) {
        get(true, url, handler);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        get(true, url, params, handler);
    }

    //post动作
    public void post(boolean partUrlOK, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        mAhc.post(partUrlOK ? getUrlHostPart(url) : url, params, handler);
        TLog.info(new StringBuilder("POST ").append(url).append("?").append(params).toString());
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        post(true, url, params, handler);
    }
}