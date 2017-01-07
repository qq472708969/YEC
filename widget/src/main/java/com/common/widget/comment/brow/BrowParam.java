package com.common.widget.comment.brow;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.common.widget.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzq on 2016/10/26.
 */
public class BrowParam {
    static final int Group = 3;
    static final int HorizontalNum = 8;

    //匹配字符串并转换成对应的图片添加到EditText中
    public static CharSequence showBrow(Resources r, final BrowEntity be) {
        String browStr = be.getBrowTagStr();
        //SpannableString能setSpan去设置样式，但不能更改字符串内容
        return showBrow(r, new SpannableString(browStr), new SpannableMatcherStrResId() {
            @Override
            public int getResId(String matchStr) {
                return be.getResId();
            }
        });
    }

    public static Spannable showBrow(Resources r, Spannable spannable, SpannableMatcherStrResId sr) {
        if (sr == null)
            throw new ExceptionInInitializerError("SpannableResId must achieve!");
        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])|(:[^:\\[\\]\\s\\n]+:)");//表情对应的字符串，软键盘输入字符串，不予表情匹配
        Matcher matcher = pattern.matcher(spannable.toString());
        while (matcher.find()) {
            String browStrG = matcher.group();
            if (TextUtils.isEmpty(browStrG)) continue;
            int resId = sr.getResId(browStrG);
            if (resId <= 0) continue;
            Drawable drawable = r.getDrawable(resId);//使用Drawable限制图片的大小
            drawable.setBounds(0, 0, (int) Utils.dpToPx(r, 23), (int) Utils.dpToPx(r, 23));
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//ALIGN_BOTTOM底部对齐
            spannable.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//开始和结束位置添加新内容都不使用这个Span
        }
        return spannable;
    }

    public interface SpannableMatcherStrResId {
        int getResId(String matchStr);
    }
}