package com.yy.yec.adapter;

import android.content.Context;

import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.entity.detail.TweetLike;
import com.yy.yec.holder.ViewHolder;

/**
 * Created by zzq on 2016/11/22.
 */
public class TweetDetailLikeListAdapter extends ListViewAdapter<TweetLike> {
    @Override
    protected void processHolder(ViewHolder vh, TweetLike item, Context c, int position) {
        vh.setImageForNet(c, R.id.iv_tweetDetailHeadIco, item.getAuthor().getPortrait(), R.mipmap.head_icon);//头像
        vh.setTextViewContent(R.id.tv_tweetDetailNick, item.getAuthor().getName());
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_tweet_detail_like;
    }
}
