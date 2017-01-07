package com.yy.yec.ui.widget.model;

/**
 * Created by zzq on 2016/12/30.
 */
public class ImgSize implements ImgSizeAble {
    String baseImageUrl;

    public ImgSize(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    @Override
    public String getImgSize(int width, int height) {
        return baseImageUrl + "?width=" + width + "&height=" + height;
    }
}