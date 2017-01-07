package com.yy.yec.adapter;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yy.yec.R;
import com.yy.yec.ui.widget.PictureView;
import com.yy.yec.utils.ImgUtils;

import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zzq on 2016/10/20.
 */
public class DetailPhotoViewAdapter extends PagerAdapter {
    private Activity context;
    private String[] imgUri;
    private Map<Integer, FrameLayout> pictureMap = new ArrayMap<Integer, FrameLayout>();

    public DetailPhotoViewAdapter(Activity context, String... imgUri) {
        this.context = context;
        this.imgUri = imgUri;
    }

    @Override
    public int getCount() {
        return imgUri.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FrameLayout fL = pictureMap.get(position);
        if (fL == null) {
            String href = imgUri[position];
            fL = (FrameLayout) context.getLayoutInflater().inflate(R.layout.detail_photo_view, null);
            PictureView pv = (PictureView) fL.findViewById(R.id.pv_currPhoto);
            ImgUtils.load(context, href, R.mipmap.load_default_img, pv);
            pv.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {//点击图片外
                @Override
                public void onViewTap(View view, float x, float y) {
                    context.finish();
                }
            });
            pictureMap.put(position, fL);
        }
        container.addView(fL);
        return fL;
    }
}