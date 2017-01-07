package com.yy.yec.ui.fragment.viewpager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.adapter.ViewPageFragmentAdapter;
import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.ui.activity.detail.TweetDetailActivity;
import com.yy.yec.ui.fragment.detail.TweetDetailCommentListFragment;
import com.yy.yec.ui.fragment.detail.TweetDetailLikeListFragment;
import com.yy.yec.ui.fragment.viewpager.base.BaseViewPagerFragment;

/**
 * Created by zzq on 2016/12/21.
 */
@SuppressLint("ValidFragment")
public class TweetDetailViewPagerFragment extends BaseViewPagerFragment {
    private Bundle tweet;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver mItemViewListClickReceiver;
    private String[] title;
    private long currLikeCount;
    private long currCommentCount;
    private TweetDetailActivity tweetDetailActivity;

    @Override
    protected void createTabToAdapter(ViewPageFragmentAdapter tabsAdapter) {
        tweetDetailActivity = (TweetDetailActivity) getActivity();
        registerTitleUpdateBroadCast(tabsAdapter);
        title = getContext().getResources().getStringArray(R.array.tweet_detail_likeAndComment);
        Tweet currTweet = (Tweet) tweet.getSerializable(TweetDetailActivity.tweet_info);//去除当前的Tweet
        tabsAdapter.addTab(String.format(title[0], currTweet.getCommentCount()), "tweet_comment", TweetDetailCommentListFragment.class, tweet);//更受关注放在前面
        tabsAdapter.addTab(String.format(title[1], currTweet.getLikeCount()), "tweet_like", TweetDetailLikeListFragment.class, tweet);
    }

    private void registerTitleUpdateBroadCast(final ViewPageFragmentAdapter tabsAdapter) {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppVariableConstants.UpdateTitleLayout);//建议把它写一个公共的变量，这里方便阅读就不写了。
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int index = intent.getIntExtra("index", 0);
                long total = intent.getLongExtra("total", -1);
                TextView titleText = (TextView) ((ViewGroup) tabsAdapter.getTitleLayout(index)).getChildAt(0);
                if (index == 0) {//评论
                    if (total < 0) {
                        boolean val = intent.getBooleanExtra("val", false);
                        if (val)
                            titleText.setText(String.format(title[index], ++currCommentCount));
                        else
                            titleText.setText(String.format(title[index], --currCommentCount));
                    } else {
                        currCommentCount = total;
                        titleText.setText(String.format(title[index], currCommentCount));
                    }
                    tweetDetailActivity.setUpdateItemCommentCount(currCommentCount);//更新ListView单条记录
                } else if (index == 1) {//赞，重新请求列表
                    if (total < 0) return;
                    currLikeCount = total;
                    titleText.setText(String.format(title[index], currLikeCount));
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    protected boolean getBundle(Bundle arguments) {
        tweet = arguments;
        if (tweet == null)
            return false;
        return true;
    }

    @Override
    public void onDestroy() {
        tweetDetailActivity = null;
        title = null;
        tweet = null;
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
        mItemViewListClickReceiver = null;
        broadcastManager = null;
        super.onDestroy();
    }
}