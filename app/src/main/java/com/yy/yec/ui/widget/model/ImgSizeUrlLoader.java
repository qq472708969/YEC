package com.yy.yec.ui.widget.model;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 * Created by zzq on 2016/12/30.
 */
public class ImgSizeUrlLoader extends BaseGlideUrlLoader<ImgSizeAble> {//UrlLoader提供的是一个接口泛型，在getUrl中调用接口约定方法

    public ImgSizeUrlLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(ImgSizeAble model, int width, int height) {
        return model.getImgSize(width, height);
    }
}