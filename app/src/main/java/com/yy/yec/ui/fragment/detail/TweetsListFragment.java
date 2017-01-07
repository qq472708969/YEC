package com.yy.yec.ui.fragment.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.yy.yec.adapter.TweetListViewAdapter;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.ui.activity.detail.TweetDetailActivity;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.fragment.detail.base.BaseListFragment;
import com.yy.yec.ui.fragment.viewpager.TweetsViewPagerFragment;
import com.yy.yec.ui.widget.NestedScrollListView;
import com.yy.yec.ui.widget.TweetItemCtrlDialog;

import java.lang.reflect.Type;

/**
 * Created by zzq on 2016/10/16.
 */
public class TweetsListFragment extends BaseListFragment<Tweet> {
    private String mCacheTag = getClass().getSimpleName();//读取缓存的标记
    private TweetListViewAdapter mAdapter;
    private int ReqType;
    private int currPosition;

    @Override
    protected boolean getBundle(Bundle arguments) {
        super.getBundle(arguments);
        ReqType = arguments.getInt(TweetsViewPagerFragment.requestCategory);
        if (ReqType > 0)
            return true;
        return false;
    }

    @Override
    protected void getServerTime(String time) {
        mAdapter.getServerTime(time);
    }

    @Override
    protected Type getType() {
        return new TypeToken<RequestInfo<RequestInfoPage<Tweet>>>() {
        }.getType();
    }

    @Override
    protected void requestNetData(boolean reqFirst) {
        AppWebApi.getTweetList(ReqType, reqFirst ? getCurrReqInfoEntity().getFirstPageToken() : getCurrReqInfoEntity().getNextPageToken(), getRespHandler());
    }

    @Override
    protected String getCacheTag() {
        return mCacheTag + ReqType;
    }

    @Override
    protected ListViewAdapter<Tweet> getListAdapter() {
        mAdapter = new TweetListViewAdapter();
        return mAdapter;
    }

    @Override
    public boolean listView_onItemLongClick(AdapterView parent, View view, int position, long id) {
        Tweet currTweet = mAdapter.getItem(position);
        int tweetAuthorId = (int) currTweet.getAuthor().getId();
        TweetItemCtrlDialog td = new TweetItemCtrlDialog(getContext(), AppCurrentUser.getInstance().getLoginUserId() == tweetAuthorId, currTweet.getContent(), currTweet.getId(), position, mAdapter);
        td.show();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//回传值设定ListView中item的状态
        super.onActivityResult(requestCode, resultCode, data);
        if (TweetDetailActivity.result_code != requestCode || TweetDetailActivity.result_code != resultCode)
            return;
        long count = data.getLongExtra("commentCount", -1);//最小就是0，总记录0;
        long likeCount = data.getLongExtra("likeCount", -1);
        boolean isLike = data.getBooleanExtra("isLike", false);
        Tweet currTweet = mAdapter.getItem(currPosition);
        if (likeCount == -1 && !isLike && count == currTweet.getCommentCount())
            return;//没有改变数据操，作则不更新条目

        if (count != currTweet.getCommentCount())
            currTweet.setCommentCount(count);
        if (currTweet.isLiked() != isLike) //设置点赞图标状态和点赞总数
            currTweet.setLiked(isLike);
        if (likeCount != -1 && likeCount != currTweet.getLikeCount())
            currTweet.setLikeCount(likeCount);
        updateItem(currTweet.getId());//更新ListView某项
    }

    private void updateItem(long id) {
        NestedScrollListView listView = mListView;
        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
                if (id == ((Tweet) listView.getItemAtPosition(i)).getId()) {
                    View view = listView.getChildAt(i - start);
                    mAdapter.getView(i, view, listView);
                    break;
                }
        }
    }

    @Override
    public void listView_onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currPosition = position;
        DetailActivityFactory.startTweetActivity(this, mAdapter.getItem(position));
    }

    @Override
    public void onDestroy() {
        mAdapter = null;
        mCacheTag = null;
        super.onDestroy();
    }
}