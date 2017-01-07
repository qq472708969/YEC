package com.yy.yec.ui.factory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.ui.activity.detail.CommentListActivity;
import com.yy.yec.ui.activity.detail.NewsDetailActivity;
import com.yy.yec.ui.activity.detail.SimpleActivity;
import com.yy.yec.ui.activity.detail.TweetDetailActivity;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzq on 2016/10/23.
 */
public class DetailActivityFactory {
    public static final Pattern Uri_endStr_Image = Pattern.compile(
            ".*?(gif|jpeg|png|jpg|bmp)"
    );

    /**
     * show detail  method
     *
     * @param context context
     * @param type    type
     * @param id      id
     */
    public static void showDetailActivity(Context context, int type, long id, String href) {
        switch (type) {
//            case 0:
//                //新闻链接
//                showUrlRedirect(context, id, href);
//                break;
//            case 1:
//                //软件推荐
//                SoftwareDetailActivity.show(context, id);
//                //UIUtil.showSoftwareDetailById(context, (int) id);
//                break;
//            case 2:
//                //问答
//                QuestionDetailActivity.show(context, id);
//                break;
//            case 3:
//                //博客
//                BlogDetailActivity.show(context, id);
//                break;
//            case 4:
//                //4.翻译
//                TranslateDetailActivity.show(context, id);
//                break;
//            case 5:
//                //活动
//                EventDetailActivity.show(context, id);
//                break;
            case 6:
                //6.资讯
                startActivity(context, id, NewsDetailActivity.class);
                break;
        }
    }

    //动态详情
    public static void startTweetActivity(Fragment f, Tweet tweet) {
        Intent intent = new Intent(f.getContext(), TweetDetailActivity.class);
        intent.putExtra(TweetDetailActivity.tweet_info, tweet);//传递serializable
        f.startActivityForResult(intent, TweetDetailActivity.result_code);
    }

    public static void startSimpleActivity(Context context, int pageType, String title) {
        Intent intent = new Intent(context, SimpleActivity.class);
        intent.putExtra("pageType", pageType);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, long id, Class c) {
        Intent intent = new Intent(context, c);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Bundle b, Class c) {
        Intent intent = new Intent(context, c);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    public static void startCommentListActivity(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 打开手机的浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, "选择浏览器"));
    }

    public static void urlRedirect(Context context, String url) {
        if (StringUtils.isEmpty(url)) return;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

        Matcher matcher = Uri_endStr_Image.matcher(url);
        if (matcher.matches()) {
            ToastUtil.showToast(url);
//            ImageGalleryActivity.show(context, url);
            return;
        }
    }
}