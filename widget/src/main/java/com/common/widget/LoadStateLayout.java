package com.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadStateLayout extends FrameLayout implements View.OnClickListener {
    public static final int Hide = 4;//隐藏加载布局
    public static final int NetError = 1;//加载错误
    public static final int NetLoading = 2;//加载中
    public static final int NoData = 3;//没有数据
    public static final int NoDataClickReset = 5;//没有数据点击可以重试
    private boolean mClickEnable = true;
    private int mCurrState;
    private OnClickListener mFailListener;
    private TextView tv_Tip;
    private ImageView iv_fail;
    private ProgressBar pb;

    public LoadStateLayout(Context context) {
        this(context, null);
    }

    public LoadStateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View root = View.inflate(context, R.layout.load_layout, null);
        iv_fail = (ImageView) root.findViewById(R.id.iv_fail);
        tv_Tip = (TextView) root.findViewById(R.id.tv_tip);
        pb = (ProgressBar) root.findViewById(R.id.pb);
        setBackgroundColor(-1);
        setOnClickListener(this);
        iv_fail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickEnable)
                    if (mFailListener != null)
                        mFailListener.onClick(v);
            }
        });
        addView(root);
    }

    public void gone() {
        mCurrState = Hide;
        setVisibility(View.GONE);
    }

    public int getCurrState() {
        return mCurrState;
    }

    @Override
    public void onClick(View v) {
        if (mClickEnable)
            if (mFailListener != null)
                mFailListener.onClick(v);
    }

    public void setCurrState(int i) {
        setVisibility(View.VISIBLE);
        switch (i) {
            case NetError:
                mCurrState = NetError;
                if (Utils.hasInternet(getContext())) {
                    tv_Tip.setText(R.string.click_refresh);
                    iv_fail.setBackgroundResource(R.mipmap.load_fail);
                } else {
                    tv_Tip.setText(R.string.network_error);
                    iv_fail.setBackgroundResource(R.mipmap.net_error);
                }
                iv_fail.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                mClickEnable = true;
                break;
            case NetLoading:
                mCurrState = NetLoading;
                iv_fail.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                tv_Tip.setText(R.string.loading);
                mClickEnable = false;
                break;
            case NoData:
                mCurrState = NoData;
                iv_fail.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                noData();
                mClickEnable = true;
                break;
            case Hide:
                setVisibility(View.GONE);
                break;
            case NoDataClickReset:
                mCurrState = NoDataClickReset;
                iv_fail.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                noData();
                mClickEnable = true;
                break;
            default:
                break;
        }
    }

    private void noData() {
        tv_Tip.setText(R.string.no_data);
        iv_fail.setBackgroundResource(R.mipmap.load_fail);
    }

    public void setOnFailClickListener(OnClickListener listener) {
        mFailListener = listener;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mCurrState = Hide;
        super.setVisibility(visibility);
    }
}