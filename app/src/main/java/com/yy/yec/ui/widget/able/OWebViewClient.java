package com.yy.yec.ui.widget.able;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yy.yec.ui.factory.DetailActivityFactory;

/**
 * Created by zzq on 2016/11/3.
 */
public class OWebViewClient extends WebViewClient {
    private Runnable mFinishCallback;

    public OWebViewClient(Runnable finishCallback) {
        super();
        mFinishCallback = finishCallback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mFinishCallback != null) mFinishCallback.run();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {//点击超链接传递url处理
        DetailActivityFactory.openBrowser(view.getContext(), url);//打开具体链接的类型（如果发现是内部链接则要使用应用的activity打开，否则浏览器打开）
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {//不要使用super，否则有些手机访问不了
        handler.cancel();
    }
}