package com.yy.yec.ui.fragment.detail.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.yy.yec.R;
import com.yy.yec.able.basic.DetailContract;
import com.yy.yec.ui.activity.detail.base.BaseBackActivity;
import com.yy.yec.ui.widget.comment.CommentActive;
import com.yy.yec.ui.widget.OWebView;
import com.yy.yec.utils.ToastUtil;

import butterknife.Bind;

/**
 * Created by zzq on 2016/10/23.
 */
public abstract class BaseWebViewFragment<TData, OperatorActivity extends DetailContract.Operator<TData>> extends BaseDetailFragment {
    private OperatorActivity mOperator;
    @Bind(R.id.lay_webview)
    protected FrameLayout lay_webview;
    private OWebView mWebView;//自定义webView
    private BaseBackActivity currActivity;

    protected OperatorActivity getOperator() {
        return mOperator;
    }

    @Override
    protected void initData() {
        currActivity = (BaseBackActivity) getActivity();
        mOperator = (OperatorActivity) getContext();
    }

    @Override
    protected void initWidget(View view) {
        OWebView webView = new OWebView(getActivity());
        lay_webview.addView(webView);
        mWebView = webView;
    }

    protected void setHtmlBody(String body) {
        mWebView.loadDetailDataAsync(body, new Runnable() {
            @Override
            public void run() {
                if (mOperator != null) {
                    mOperator.hideLoading();
                }
            }
        });
    }

    protected void setCommentCount(int count) {
        if (mOperator != null) {
            mOperator.setCommentCount(count);
        }
    }

    /**
     * 检查当前数据，并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回-1
     */
    protected int getLoginUserId() {
        if (getOperator().getData() == null) {
            ToastUtil.showToast("无数据...");
            return -1;
        }
        return CommentActive.allowSendComment(getActivity());
    }

    protected void showWaitProgressDialog(String msg) {
        currActivity.showWaitProgressDialog(msg);
    }

    protected void hideWaitProgressDialog() {
        currActivity.hideWaitProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mOperator = null;
        lay_webview = null;
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}