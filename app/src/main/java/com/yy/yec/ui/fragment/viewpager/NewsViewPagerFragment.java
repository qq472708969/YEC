package com.yy.yec.ui.fragment.viewpager;

import android.os.Bundle;

import com.yy.yec.R;
import com.yy.yec.adapter.ViewPageFragmentAdapter;
import com.yy.yec.global.App;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.ui.fragment.viewpager.base.BaseViewPagerFragment;
import com.yy.yec.ui.fragment.detail.BlogDetailFragment;
import com.yy.yec.ui.fragment.detail.EventDetailFragment;
import com.yy.yec.ui.fragment.detail.NewsListFragment;
import com.yy.yec.ui.fragment.detail.QuestionDetailFragment;

/**
 * Created by zzq on 2016/9/27.
 */
public class NewsViewPagerFragment extends BaseViewPagerFragment {
    @Override
    protected void createTabToAdapter(ViewPageFragmentAdapter tabsAdapter) {
        String[] title = App.resources().getStringArray(R.array.news_viewpage);
        tabsAdapter.addTab(title[0], "news", NewsListFragment.class, getBundle(AppVariableConstants.CATALOG_ALL));
        tabsAdapter.addTab(title[1], "latest_blog", BlogDetailFragment.class, getBundle(AppVariableConstants.CATALOG_WEEK));
        tabsAdapter.addTab(title[2], "question", QuestionDetailFragment.class, getBundle(AppVariableConstants.CATALOG_LATEST));
        tabsAdapter.addTab(title[3], "activity", EventDetailFragment.class, getBundle(AppVariableConstants.CATALOG_RECOMMEND));
    }

    private Bundle getBundle(int newType) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppVariableConstants.BUNDLE_KEY_CATALOG, newType);
        return bundle;
    }

    /**
     * 基类会根据不同的catalog展示相应的数据
     *
     * @param catalog 要显示的数据类别
     * @return
     */
    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(AppVariableConstants.BUNDLE_BLOG_TYPE, catalog);
        return bundle;
    }
}