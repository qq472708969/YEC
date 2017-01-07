package com.yy.yec.ui.fragment.detail;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.adapter.NewsListViewAdapter;

import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.entity.detail.News;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.fragment.detail.base.BaseListFragment;
import com.yy.yec.ui.manager.HaveReadList;
import com.yy.yec.utils.UIUtil;

import java.lang.reflect.Type;

/**
 * Created by zzq on 2016/10/4.
 */
public class NewsListFragment extends BaseListFragment<News> {
    private String mCacheTag = getClass().getSimpleName();//读取缓存的标记
    public static String haveRead_news = "haveRead_news";
    private ListViewAdapter<News> mAdapter;

    @Override
    protected void getServerTime(String time) {
        ((NewsListViewAdapter) mAdapter).getServerTime(time);
    }

    @Override
    protected Type getType() {
        return new TypeToken<RequestInfo<RequestInfoPage<News>>>() {
        }.getType();
    }

    @Override
    protected void requestNetData(boolean reqFirst) {
        AppWebApi.getNewsList(reqFirst ? getCurrReqInfoEntity().getFirstPageToken() : getCurrReqInfoEntity().getNextPageToken(), getRespHandler());
    }

    @Override
    protected String getCacheTag() {
        return mCacheTag;
    }

    @Override
    protected ListViewAdapter<News> getListAdapter() {
        mAdapter = new NewsListViewAdapter();
        return mAdapter;
    }

    @Override
    public void listView_onItemClick(AdapterView parent, View view, int position, long id) {
        super.listView_onItemClick(parent, view, position, id);
        News news = mAdapter.getItem(position);
        if (news == null) return;
        DetailActivityFactory.showDetailActivity(getActivity(), news.getType(), news.getId(), news.getHref());
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        TextView content = (TextView) view.findViewById(R.id.tv_description);
        //设置条目颜色
        UIUtil.updateContentColor(R.color.haveRead_text_color_light, title, content);
        //存储已读条目
        HaveReadList.getInstance().setProperty(haveRead_news, String.valueOf(news.getId()));
    }

    @Override
    public void onDestroy() {
        mCacheTag = null;//读取缓存的标记
        haveRead_news = null;
        mAdapter = null;
        super.onDestroy();
    }
}