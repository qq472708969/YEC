package com.yy.yec.ui.fragment.detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.yy.yec.adapter.TweetDetailLikeListAdapter;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.entity.detail.TweetLike;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.ui.fragment.detail.base.tweet.TweetDetailListFragment;

import java.lang.reflect.Type;

/**
 * Created by zzq on 2016/11/22.
 */
public class TweetDetailLikeListFragment extends TweetDetailListFragment<TweetLike> {
    private TweetDetailLikeListAdapter mAdapter;
    private String cacheTag = getClass().getSimpleName();
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver mUpdateLikeListReceiver;

    @Override
    protected void init(View contentView) {
        super.init(contentView);
        registerUpdateLikeListBroadCast();
    }

    private void registerUpdateLikeListBroadCast() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppVariableConstants.UpdateLikeList);
        mUpdateLikeListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean update = intent.getBooleanExtra("updateLikeList", false);
                if (update) onRefreshing();
            }
        };
        broadcastManager.registerReceiver(mUpdateLikeListReceiver, intentFilter);
    }

    @Override
    protected void getTotal(long totalResults) {
        Intent intent = new Intent(AppVariableConstants.UpdateTitleLayout);
        intent.putExtra("index", 1);
        intent.putExtra("total", totalResults);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    protected void requestNetData(boolean reqFirst) {
        AppWebApi.getTweetLikeList(getCurrTweet().getId(), reqFirst ? getCurrReqInfoEntity().getFirstPageToken() : getCurrReqInfoEntity().getNextPageToken(), getRespHandler());
    }

    @Override
    protected Type getType() {
        return new TypeToken<RequestInfo<RequestInfoPage<TweetLike>>>() {
        }.getType();
    }

    @Override
    protected ListViewAdapter<TweetLike> getListAdapter() {
        if (mAdapter == null)
            mAdapter = new TweetDetailLikeListAdapter();
        return mAdapter;
    }

    @Override
    protected String getChildCacheTag() {
        return cacheTag;//读取缓存的标记
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(mUpdateLikeListReceiver);
        mUpdateLikeListReceiver = null;
        cacheTag = null;
        mAdapter = null;
        super.onDestroy();
    }
}