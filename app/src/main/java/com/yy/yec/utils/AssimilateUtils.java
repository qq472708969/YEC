package com.yy.yec.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;

import com.yy.yec.ui.factory.DetailActivityFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 后台很多String数据需要做本地化处理
 * <p/>
 * Created by thanatos on 16/7/27.
 */
public class AssimilateUtils {

    // @thanatosx
    // http://my.oschina.net/u/user_id
    // http://my.oschina.net/user_ident
    public static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a\\s+href=['\"]http[s]?://my\\.oschina\\.[a-z]+/([0-9a-zA-Z_]+" +
                    "|u/([0-9]+))['\"][^<>]*>(@([^@<>\\s]+))</a>"
    );
    public static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    public static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>"
    );
    public static final Pattern PatternSoftwareTag = Pattern.compile(
            "#([^#@<>\\s]+)#"
    );

    // @user links
    @Deprecated
    public static final Pattern PatternAtUserAndLinks = Pattern.compile(
            "<a\\s+href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>" +
                    "|<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // links
    public static final Pattern PatternLinks = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // team task
    public static final Pattern PatternTeamTask = Pattern.compile(
            "<a\\s+style=['\"][^'\"]*['\"]\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // html task
    public static final Pattern PatternHtml = Pattern.compile(
            "<[^<>]+>([^<>]+)</[^<>]+>"
    );

    private interface Action1 {
        void call(String str);
    }

    /**
     * 高亮@User
     *
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable highlightAtUser(Context context, CharSequence content) {
        return highlightAtUser(context, new SpannableString(content));
    }

    /**
     * @param context   Context
     * @param spannable string
     * @return
     * @see #highlightAtUser(Context, Spannable)
     */
    public static Spannable highlightAtUser(Context context, Spannable spannable) {
        String str = spannable.toString();
        Matcher matcher = PatternAtUser.matcher(str);
        while (matcher.find()) {
            ForegroundColorSpan span = new ForegroundColorSpan(0XFF6888AD);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 与 {@link #highlightAtUser(Context, Spannable)} 不同的是这个方法是针对<a>标签包裹的@User
     *
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable assimilateOnlyAtUser(final Context context, CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternAtUserWithHtml.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(1); // ident
                final String group1 = matcher.group(2); // uid
                final String group2 = matcher.group(3); // @Nick
                final String group3 = matcher.group(4); // Nick
                builder.replace(matcher.start(), matcher.end(), group2);
                long uid = 0;
                try {
                    uid = group1 == null ? 0 : Integer.valueOf(group1);
                } catch (Exception e) {
                    uid = 0;
                }
                final long _uid = uid;
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        if (_uid > 0){
//                            OtherUserHomeActivity.show(context, _uid);
//                        }else if (!TextUtils.isEmpty(group0)){
//                            OtherUserHomeActivity.show(context, 0, group0);
//                        }else {
//                            OtherUserHomeActivity.show(context, group3);
//                        }
                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }


    /**
     * 格式化link链接
     * <p/>
     * 注意: 最好在最后处理这个过程,否则有些需要特殊处理的就会被它格式化掉
     *
     * @param context Context
     * @param content CharSequence
     * @return A spannable object
     */
    public static Spannable assimilateOnlyLink(final Context context, CharSequence content) {
        return assimilate(content, PatternLinks, 1, 2, new SpannableAction() {
            @Override
            public void callBack(String uri) {
                DetailActivityFactory.openBrowser(context, uri);
            }
        });
    }

    /**
     * 本地化处理
     *
     * @param sequence  处理内容
     * @param pattern   匹配规则
     * @param indexUri  正则表达式组号---使用的资源
     * @param indexText 正则表达式组号---显示的内容
     * @param action    回调函数
     * @return Spannable
     */
    private static Spannable assimilate(CharSequence sequence, Pattern pattern, int indexUri, int indexText, final SpannableAction action) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher = null;
        while (true) {
            matcher = pattern.matcher(builder.toString());//每次匹配是由于 builder.replace后长度发生改变，索引不在准确
            if (!matcher.find()) break;
            final String _indexUri = matcher.group(indexUri);//资源地址
            final String _indexText = matcher.group(indexText);//文本
            builder.replace(matcher.start(), matcher.end(), _indexText);//将一个长的超链接字符串替换成用来显示的文本也就是<a></a>中间的内容
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    action.callBack(_indexUri);
                }
            };
            //所以替换成ClickableSpan时也要对应的长度替换
            builder.setSpan(span, matcher.start(), matcher.start() + _indexText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            continue;
        }
        return builder;
    }

    private interface SpannableAction {
        void callBack(String uri);
    }
}