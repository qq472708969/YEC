package com.yy.yec.ui.activity.detail;

import com.google.gson.reflect.TypeToken;
import com.yy.yec.R;
import com.yy.yec.able.detail.NewsDetailContract;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.detail.NewsDetail;
import com.yy.yec.ui.activity.detail.base.BaseDetailActivity;
import com.yy.yec.ui.fragment.detail.NewsWebViewFragment;
import com.yy.yec.ui.fragment.detail.base.BaseWebViewFragment;

import java.lang.reflect.Type;

/**
 * Created by zzq on 2016/10/25.
 */
public class NewsDetailActivity extends BaseDetailActivity<NewsDetail> implements NewsDetailContract.Operator {
    public String news = "news";
    private final String mCacheTag = getClass().getSimpleName();//读取缓存的标记

    @Override
    protected Class<? extends BaseWebViewFragment> getDataViewFragment() {
        return NewsWebViewFragment.class;
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<RequestInfo<NewsDetail>>() {
        }.getType();
    }

    @Override
    protected String getCacheTag() {
        return mCacheTag;
    }

    @Override
    protected void requestData() {
        AppWebApi.getNewsDetail(getItemId(), news, getRequestHandler());
    }

    @Override
    protected void onDestroy() {
        news = null;
        super.onDestroy();
    }
}