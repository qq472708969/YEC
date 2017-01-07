package com.yy.yec.ui.widget.model;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by zzq on 2016/12/30.
 */
public class ImgMemoryCustomSizeModel implements GlideModule {
    public void registerComponents(Context context, Glide glide) {//注册一个自定义加载图片模式
//        glide.register(ImgSizeAble.class, InputStream.class, new ImgSizeUrlLoaderFactory());
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);//Glide分配内存缓存计算器
        int defBPSize = calculator.getBitmapPoolSize();//Glide默认设置Bitmap池大小
        int defMCSizel = calculator.getMemoryCacheSize();//默认内存缓存大小
        builder.setMemoryCache(new LruResourceCache((int) (defMCSizel * 0.3)));//默认值的0.3
        builder.setBitmapPool(new LruBitmapPool((int) (defBPSize * 0.3)));
    }
}