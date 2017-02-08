package com.yy.yec.utils;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.yy.yec.global.App;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.ui.widget.model.ImgMemoryCustomSizeModel;
import com.yy.yec.ui.widget.model.ImgSize;
import com.yy.yec.ui.widget.model.ImgSizeUrlLoader;

/**
 * Created by zzq on 2016/11/10.
 */
public class ImgUtils {
    private static RequestManager initGlide(Context c) {
        return Glide.with(c);
    }

    public static void load(Context c, String uri, @DrawableRes int res, ImageView img) {//placeholder和error共用
        load(c, uri, res, res, img, true, true, false);
    }

    public static void load(Context c, String uri, @DrawableRes int resPlaceHolder, @DrawableRes int resError, ImageView img) {//placeholder和error不共用
        load(c, uri, resPlaceHolder, resError, img, true, true, false);
    }

    public static void load(Context c, String uri, ImageView img) {//placeholder和error不共用
        load(c, uri, -1, -1, img, false, false, true);
    }

    private static void load(Context c, String uri, @DrawableRes int resPlaceHolder, @DrawableRes int resError, ImageView img, boolean placeholder, boolean error, boolean centerCrop) {
        DrawableRequestBuilder drb = initGlide(c).load(uri).
                //---这种方式能够适应不同的请求对应不同的model，比直接使用<meta-data>更灵活---
                        //using(new ImgSizeUrlLoader(c)).//使用一个UrlLoad策略
               // load(new ImgSize(uri)).
                //------
                //     thumbnail(0.1f)//先加载一个缩略图，提高展示速度
                //使用同一缓存策略，才能有效的读取缓存
                //ALL：缓存所有，加载过程中的所有图片状态；RESULT ：缓存转换之后的图像（忽略中间状态）;SOURCE：只缓存原图
                        diskCacheStrategy(DiskCacheStrategy.RESULT);
        if (placeholder)
            drb.placeholder(resPlaceHolder);
        if (error)
            drb.error(resError);
        //.thumbnail(0.1f)//显示原图1/10的缩略图
        if (centerCrop)
            drb.centerCrop();//裁剪图片适应ImageView
        drb.dontAnimate();//不使用载入动画
        //drb.skipMemoryCache(true);//不使用内存缓存
        drb.into(img);
    }

    public static DrawableTypeRequest load(Context c, Object model) {
        return initGlide(c).load(model);
    }

    public static GlideUrl getUserGlideUrl(String url) {//登陆成功后，请求图片时需要携带cookie
        if (AppCurrentUser.getInstance().getLoginOK())
            return new GlideUrl(url, new LazyHeaders.Builder().addHeader("Cookie", AppCurrentUser.getInstance().getUserCookie()).build());
        else
            return new GlideUrl(url);
    }
}