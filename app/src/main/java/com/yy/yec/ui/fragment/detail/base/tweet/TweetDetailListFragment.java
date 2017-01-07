package com.yy.yec.ui.fragment.detail.base.tweet;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.ui.activity.detail.TweetDetailActivity;
import com.yy.yec.ui.fragment.detail.base.BaseListFragment;
import com.yy.yec.ui.fragment.viewpager.TweetDetailViewPagerFragment;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.UIUtil;

/**
 * Created by zzq on 2016/11/2.
 */
public abstract class TweetDetailListFragment<T> extends BaseListFragment<T> {
    private Tweet tweet;
    private ProgressDialog pd;
    private int position;

    protected Tweet getCurrTweet() {
        return tweet;
    }

    @Override
    protected boolean getBundle(Bundle arguments) {
        tweet = (Tweet) arguments.getSerializable(TweetDetailActivity.tweet_info);
        if (tweet == null)
            return false;
        return true;
    }

    @Override
    protected boolean enableLoadStateLayout() {
        return false;
    }

    @Override
    protected void getServerTime(String time) {

    }

    @Override
    protected String getCacheTag() {
        return getChildCacheTag() + tweet.getId();//读取缓存的标记
    }

    protected abstract String getChildCacheTag();

    //-----view tool
    public void showWaitProgressDialog(String msg) {
        if (StringUtils.isEmpty(msg)) return;
        if (pd == null) {
            pd = UIUtil.getProgressDialog(getContext());
            pd.setCancelable(false);//不允许触碰取消
        }
        pd.setMessage(msg);
        pd.show();
    }

    public void hideWaitProgressDialog() {
        if (pd != null) pd.hide();
    }
}