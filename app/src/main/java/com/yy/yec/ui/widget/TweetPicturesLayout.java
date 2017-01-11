package com.yy.yec.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yy.yec.R;
import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.ui.activity.detail.ImageGalleryActivity;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.utils.ImgUtils;

/**
 * Created by zzq on 16/19/30.
 */
public class TweetPicturesLayout extends ViewGroup implements View.OnClickListener {
    private static final int SINGLE_MAX_W = 120;
    private static final int SINGLE_MAX_H = 180;
    private static final int SINGLE_MIN_W = 34;
    private static final int SINGLE_MIN_H = 34;

    private Tweet.Image[] mImages;
    private float mVerticalSpacing;
    private float mHorizontalSpacing;
    private int mColumn;
    private int mMaxPictureSize;

    public TweetPicturesLayout(Context context) {
        this(context, null);
    }

    public TweetPicturesLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TweetPicturesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final Context context = getContext();
        final Resources resources = getResources();
        final float density = resources.getDisplayMetrics().density;

        int vSpace = (int) (4 * density);
        int hSpace = vSpace;

        if (attrs != null) {
            // Load attributes
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.TweetPicturesLayout, defStyleAttr, defStyleRes);

            // Load clip touch corner radius
            vSpace = a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_verticalSpace, vSpace);
            hSpace = a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_horizontalSpace, hSpace);
            setColumn(a.getInt(R.styleable.TweetPicturesLayout_column, 3));//默认3列
            setMaxPictureSize(a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_maxPictureSize, 0));
            a.recycle();
        }

        setVerticalSpacing(vSpace);
        setHorizontalSpacing(hSpace);
    }

    public void setHorizontalSpacing(float pixelSize) {
        mHorizontalSpacing = pixelSize;
    }

    public void setVerticalSpacing(float pixelSize) {
        mVerticalSpacing = pixelSize;
    }

    public void setColumn(int column) {
        if (column < 1)
            column = 1;
        if (column > 20)
            column = 20;
        mColumn = column;
    }

    public void setMaxPictureSize(int maxPictureSize) {
        if (maxPictureSize < 0)
            maxPictureSize = 0;
        mMaxPictureSize = maxPictureSize;
    }

    public void setImage(Tweet.Image[] images) {
        removeAllImage();
        if (images == null || mImages == images)
            return;
        mImages = images;
        if (images != null && images.length > 0) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            ImageView view = null;
            for (int i = 0; i < images.length; i++) {
                view = new ImageView(getContext());
                view.setBackgroundColor(getResources().getColor(R.color.light_gray));//给个底色占位符
                view.setLayoutParams(lp);
                view.setOnClickListener(this);
                String path = images[i].getThumb();
                ImgUtils.load(getContext(), path, view);
                view.setTag(i);
                addView(view);
            }

            if (getVisibility() == VISIBLE)
                requestLayout();
            else
                setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public void removeAllImage() {
        removeAllViews();
        mImages = null;
    }

    private int getMaxChildSize(int size) {
        if (mMaxPictureSize == 0)
            return size;
        else
            return Math.min(mMaxPictureSize, size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int selfWidth = resolveSize(paddingLeft + paddingRight, widthMeasureSpec);//把padding算进来
        int wantedHeight = paddingBottom + paddingTop;//原有基础上增加的高度
        final int childCount = getChildCount();

        //noinspection StatementWithEmptyBody
        if (childCount == 0) {
            // Not have child we can only need padding size
        } else if (childCount == 1) {
            View child = getChildAt(0);
            int imageW = mImages[0].getW();
            int imageH = mImages[0].getH();
            float density = getResources().getDisplayMetrics().density;
            // Get max width and height
            float maxContentW = Math.min(selfWidth - paddingRight - paddingLeft, density * SINGLE_MAX_W);
            float maxContentH = density * SINGLE_MAX_H;

            int childW, childH;

            float hToW = imageH / (float) imageW;
            if (hToW > (maxContentH / maxContentW)) {
                childH = (int) maxContentH;
                childW = (int) (maxContentH / hToW);
            } else {
                childW = (int) maxContentW;
                childH = (int) (maxContentW * hToW);
            }
            // Check the width and height below Min values
            int minW = (int) (SINGLE_MIN_W * density);
            if (childW < minW)
                childW = minW;
            int minH = (int) (SINGLE_MIN_H * density);
            if (childH < minH)
                childH = minH;


            child.measure(MeasureSpec.makeMeasureSpec(childW, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childH, MeasureSpec.EXACTLY));

            wantedHeight += childH;
        } else {
            // Measure all child
            final float maxContentWidth = selfWidth - paddingRight - paddingLeft - mHorizontalSpacing * (mColumn - 1);
            // Get child size
            final int childSize = getMaxChildSize((int) (maxContentWidth / mColumn));
            View childView = null;
            for (int i = 0; i < childCount; ++i) {
                childView = getChildAt(i);
                childView.measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
            }

            //int lines = 0;//(int) (childCount / (float) mColumn + 0.9);
            float linesFloat = childCount / (float) mColumn;
            int linesInt = (int) linesFloat;
            if (linesFloat - linesInt > 0)//保证足够的行数显示完整的图片
                ++linesInt;
            wantedHeight += (int) (linesInt * childSize + mVerticalSpacing * (linesInt - 1));
        }

        setMeasuredDimension(selfWidth, resolveSize(wantedHeight, heightMeasureSpec));//设置自己的大小
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float childCount = getChildCount();
        if (childCount > 0) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();

            if (childCount == 1) {
                View childView = getChildAt(0);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                childView.layout(paddingLeft, paddingTop, paddingLeft + childWidth, paddingTop + childHeight);
            } else {
                int mWidth = r - l;
                int paddingRight = getPaddingRight();

                int lineHeight = 0;
                int childLeft = paddingLeft;
                int childTop = paddingTop;

                for (int i = 0; i < childCount; ++i) {
                    View childView = getChildAt(i);

                    if (childView.getVisibility() == View.GONE) {
                        continue;
                    }

                    int childWidth = childView.getMeasuredWidth();
                    int childHeight = childView.getMeasuredHeight();

                    lineHeight = Math.max(childHeight, lineHeight);

                    if (childLeft + childWidth + paddingRight > mWidth) {
                        childLeft = paddingLeft;
                        childTop += mVerticalSpacing + lineHeight;
                        lineHeight = childHeight;
                    }
                    childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                    childLeft += childWidth + mHorizontalSpacing;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Tweet.Image[] images = mImages;
        if (images == null)
            return;

        Object obj = v.getTag();
        if (obj == null || !(obj instanceof Integer))
            return;
        int index = (int) obj;
        if (index < 0)
            index = 0;
        if (index >= images.length)
            index = images.length - 1;

        String[] mImagesHref = new String[mImages.length];
        for (int i = 0; i < mImages.length; i++) {
            mImagesHref[i] = mImages[i].getHref();
        }

        Bundle b = new Bundle();
        b.putStringArray(ImageGalleryActivity.KEY_IMAGE, mImagesHref);
        b.putInt(ImageGalleryActivity.KEY_POSITION, index);
        DetailActivityFactory.startActivity(getContext(), b, ImageGalleryActivity.class);
    }
}