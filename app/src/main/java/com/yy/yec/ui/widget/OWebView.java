package com.yy.yec.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yy.yec.global.AppThreadPool;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.activity.detail.ImageGalleryActivity;
import com.yy.yec.ui.widget.able.OWebViewClient;
import com.yy.yec.ui.widget.able.OnWebViewImageListener;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzq
 * on 16/6/24.
 */
public class OWebView extends WebView {
    private List<String> imgUriList = new ArrayList<String>();

    public OWebView(Context context) {
        super(context);
        init();
    }

    public OWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public OWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void init() {
        setClickable(false);
        setFocusable(false);

        setHorizontalScrollBarEnabled(false);

        WebSettings settings = getSettings();
        settings.setDefaultFontSize(14);//默认字体
        settings.setSupportZoom(false);//支持缩放
        settings.setBuiltInZoomControls(false);//支持缩放控件
        settings.setDisplayZoomControls(false);//隐藏缩放控件
        settings.setJavaScriptEnabled(true);//支持js

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addJavascriptInterface(new OnWebViewImageListener() {//暴露给js接口，js调用showImagePreview相当于调用了java的showImagePreview方法(点击图片时执行)
                @Override
                @JavascriptInterface
                public void showImagePreview(String bigImageUrl) {
                    if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
                        if (imgUriList.size() < 1) return;
                        int indexPosition = 0;
                        Bundle b = new Bundle();
                        String[] ary = imgUriList.toArray(new String[imgUriList.size()]);
                        for (int i = 0; i < ary.length; i++) {
                            if (ary[i].equals(bigImageUrl)) {
                                indexPosition = i;
                                break;
                            }
                        }
                        b.putStringArray(ImageGalleryActivity.KEY_IMAGE, ary);
                        b.putInt(ImageGalleryActivity.KEY_POSITION, indexPosition);
                        DetailActivityFactory.startActivity(getContext(), b, ImageGalleryActivity.class);
                    }
                }
            }, "mWebViewImageListener");
        }
    }

    public void loadDetailDataAsync(final String content, Runnable finishCallback) {
        this.setWebViewClient(new OWebViewClient(finishCallback));
        Context context = getContext();
        if (context != null && context instanceof Activity) {
            final Activity activity = (Activity) context;
            AppThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final String body = setupWebContent(content, true, true, "");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
                        }
                    });
                }
            });
        } else {
            Log.e(OWebView.class.getName(), "loadDetailDataAsync error, the Context isn't ok");
        }
    }

    private String setupWebContent(String content, boolean isShowHighlight, boolean isShowImagePreview, String css) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim()))
            return "";

        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (DeviceUtil.internet()) {
            // 过滤掉 img标签的width,height属性
            content = content.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            content = content.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

            //添加点击图片放大支持
            if (isShowImagePreview) {//$1$2分别代表正在表达式中第一个小括号和第二个小括号的内容（索引从1开始）
                content = content.replaceAll("(<img[^>]+src=\")(\\S+)\"", "$1$2\" onClick=\"javascript:mWebViewImageListener.showImagePreview('$2')\"");
                Pattern p = Pattern.compile("showImagePreview\\(\'(\\S+)\'\\)");
                Matcher m = p.matcher(content);
                while (m.find()) {
                    imgUriList.add(m.group(1));//
                }
            }
        } else {
            // 过滤掉 img标签
            content = content.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }

        // 过滤table的内部属性
        content = content.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");


        return String.format(
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + (isShowHighlight ? "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/shCore.css\">" : "")
                        + (isShowHighlight ? "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/shThemeDefault.css\">" : "")
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common_detail.css\">"
                        + "%s"
                        + "</head>"
                        + "<body>"
                        + "<div class='body-content'>"
                        + "%s"
                        + "</div>"
                        + (isShowHighlight ? "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>" : "")
                        + (isShowHighlight ? "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>" : "")
                        + (isShowHighlight ? "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>" : "")
                        + "</body>"
                        + "</html>"
                , (css == null ? "" : css), content);
    }

    @Override
    public void destroy() {
        setWebViewClient(null);

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);

        removeJavascriptInterface("mWebViewImageListener");
        removeAllViewsInLayout();

        removeAllViews();
        //clearCache(true);

        super.destroy();
    }
}