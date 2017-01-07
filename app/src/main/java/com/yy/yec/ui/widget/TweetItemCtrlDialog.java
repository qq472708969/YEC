package com.yy.yec.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.common.widget.base.BaseDialog;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.HTMLUtil;
import com.yy.yec.utils.ToastUtil;
import com.yy.yec.utils.UIUtil;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/12/18.
 */
public class TweetItemCtrlDialog extends BaseDialog {
    private final boolean del;
    private final String content;
    private final long tweetId;
    private final int position;
    private final ListViewAdapter mAdapter;
    @Bind(R.id.tweet_del)
    protected TextView tweet_del;

    public TweetItemCtrlDialog(Context context, boolean del, String content, long tweetId, int position, ListViewAdapter mAdapter) {
        super(context, R.style.dialog_common);
        this.mAdapter = mAdapter;
        this.position = position;
        this.content = content;
        this.del = del;
        this.tweetId = tweetId;
    }

    @Override
    protected int setContentView() {
        return R.layout.dialog_tweet;
    }

    @Override
    protected void init(Bundle savedInstanceState, Window currWin) {
        currWin.setGravity(Gravity.BOTTOM);
        if (del)
            tweet_del.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tweet_copy)
    protected void tweet_copy_onClick(View v) {
        UIUtil.copyText(HTMLUtil.delHTMLTag(content));//复制文本
        hide();
    }

    @OnClick(R.id.tweet_del)
    protected void tweet_del_onClick(View v) {
        if (!DeviceUtil.internet()) {
            ToastUtil.showToast(getContext().getString(R.string.tip_no_internet));
            return;
        }
        UIUtil.showConfirmDialog(v.getContext(), getContext().getString(R.string.tweet_del_confirm), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppWebApi.deleteTweet(tweetId, new DeleteTweetHandler());
            }
        });
        hide();
    }

    private class DeleteTweetHandler extends TextHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            ToastUtil.showToast(getContext().getString(R.string.remove_tweet_fail));
        }

        @Override
        public void onFinish() {
            hide();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.optInt("code") == 1) {
                    mAdapter.removeItem(position);
                    mAdapter.notifyDataSetChanged();
                    ToastUtil.showToast(getContext().getString(R.string.remove_tweet_success));

                    Intent intent = new Intent(AppVariableConstants.UpdateTitleLayout);
                    intent.putExtra("index", 0);
                    intent.putExtra("total", -1);//总条数
                    intent.putExtra("val", false);//true增加一条
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                } else {
                    ToastUtil.showToast(getContext().getString(R.string.remove_tweet_fail));
                }
            } catch (Exception e) {
            }
        }
    }
}