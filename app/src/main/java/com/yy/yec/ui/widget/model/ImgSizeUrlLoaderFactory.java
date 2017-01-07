package com.yy.yec.ui.widget.model;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 * Created by zzq on 2016/12/30.
 */
public class ImgSizeUrlLoaderFactory implements ModelLoaderFactory<ImgSizeAble, InputStream> {//用于创建UrlLoader

    @Override
    public ModelLoader<ImgSizeAble, InputStream> build(Context context, GenericLoaderFactory factories) {
        return new ImgSizeUrlLoader(context);
    }

    @Override
    public void teardown() {//这个工厂对象被其它工厂对象替换时调用，相当于destroy

    }
}