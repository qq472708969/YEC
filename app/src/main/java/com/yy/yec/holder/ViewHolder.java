package com.yy.yec.holder;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.global.App;
import com.yy.yec.utils.ImgUtils;
import com.yy.yec.utils.UIUtil;

import org.kymjs.kjframe.utils.ImageUtils;

import java.util.HashMap;

/**
 * Created by zzq on 2016/10/10.
 */
public class ViewHolder {
    private int mLayoutId;
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, int layoutId) {
        mViews = new SparseArray<View>();
        mLayoutId = layoutId;
        mConvertView = UIUtil.inflate(context, layoutId);
        mConvertView.setTag(this);
    }

    /**
     * 获取一个viewHolder
     *
     * @param convertView view
     * @param layoutId    布局资源id
     * @return
     */
    public static ViewHolder getViewHolder(Context context, View convertView, int layoutId) {
        if (convertView == null)
            return instance(context, layoutId);
        else {
            ViewHolder vh = (ViewHolder) convertView.getTag();
            if (vh != null && vh.mLayoutId != layoutId)
                return instance(context, layoutId);
        }
        return (ViewHolder) convertView.getTag();
    }

    private static ViewHolder instance(Context context, int layoutId) {
        return new ViewHolder(context, layoutId);
    }

    public View getConvertView() {
        return mConvertView;
    }

    // 通过一个ContentView中的ResId来获取一View
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    //设置文字
    public void setTextViewContent(int viewId, CharSequence text) {
        if (TextUtils.isEmpty(text)) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    //设置颜色
    public void setTextViewColor(int viewId, @ColorRes int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(App.resources().getColor(color));
        tv.setVisibility(View.VISIBLE);
    }

    //设置图片
    public void setImg(int viewId, @DrawableRes int img) {
        ImageView iv = getView(viewId);
        iv.setImageResource(img);
    }

    public void setImageForNet(Context c, int viewId, String imgUrl, int res) {
        ImageView iv = getView(viewId);
        ImgUtils.load(c, imgUrl, res, iv);
    }

    //点击事件
    public void setOnClickListener(int viewId, View.OnClickListener click) {
        View v = getView(viewId);
        v.setOnClickListener(click);
    }

    //图片请求
    public void setImg(int viewId, String uri, @DrawableRes int placeholder, Context c) {
        ImgUtils.load(c, uri, placeholder, (ImageView) getView(viewId));
    }

    //图片请求
    public void addView(int viewId, View Child) {
        ViewGroup vg = getView(viewId);
        vg.addView(Child);
    }

    //隐藏View-Gone
    public void setGone(int viewId) {
        getView(viewId).setVisibility(View.GONE);
    }

    //隐藏View-InVisible
    public void setInVisible(int viewId) {
        getView(viewId).setVisibility(View.INVISIBLE);
    }

    //显示View
    public void setVisible(int viewId) {
        getView(viewId).setVisibility(View.VISIBLE);
    }
}
