package com.yy.yec.ui.fragment.viewpager;

import android.os.Bundle;

import com.yy.yec.R;
import com.yy.yec.adapter.ViewPageFragmentAdapter;
import com.yy.yec.global.App;
import com.yy.yec.ui.fragment.detail.TweetsListFragment;
import com.yy.yec.ui.fragment.viewpager.base.BaseViewPagerFragment;

/**
 * Created by zzq on 2016/9/27.
 */
public class TweetsViewPagerFragment extends BaseViewPagerFragment {
    public static String requestCategory = "requestCategory";
    public static int New = 1;
    public static int Hot = 2;

    @Override
    protected void createTabToAdapter(ViewPageFragmentAdapter tabsAdapter) {
        String[] title = App.resources().getStringArray(R.array.tweets_viewpage);
        tabsAdapter.addTab(title[0], "new_tweets", TweetsListFragment.class, getBundle(New));
        tabsAdapter.addTab(title[1], "hot_tweets", TweetsListFragment.class, getBundle(Hot));
    }

    private Bundle getBundle(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(requestCategory, type);
        return bundle;
    }

    @Override
    public void onDestroy() {
        requestCategory = null;
        super.onDestroy();
    }
}