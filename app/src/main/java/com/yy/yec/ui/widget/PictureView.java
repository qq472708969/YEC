package com.yy.yec.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zzq on 2016/10/27.
 */
public class PictureView extends PhotoView {
    private PhotoViewAttacher.OnViewTapListener l;

    public PictureView(Context context) {
        this(context, null);
    }

    public PictureView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PictureView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (l != null)
            getIPhotoViewImplementation().setOnViewTapListener(l);
    }

    @Override
    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {
        l = listener;
        super.setOnViewTapListener(listener);
    }
}