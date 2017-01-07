package com.yy.yec.ui.enumerate;

import com.yy.yec.R;
import com.yy.yec.ui.fragment.basic.ExploreFragment;
import com.yy.yec.ui.fragment.basic.MyInfoFragment;
import com.yy.yec.ui.fragment.viewpager.NewsViewPagerFragment;
import com.yy.yec.ui.fragment.viewpager.TweetsViewPagerFragment;

/**
 * Created by zzq on 2016/9/27.
 */
public enum MenuTab {
    NEWS(0, R.string.tabwidget_news, R.drawable.tabwidget_news_icon_selector, NewsViewPagerFragment.class),
    TWEET(1, R.string.tabwidget__tweet, R.drawable.tabwidget_tweet_icon_selector, TweetsViewPagerFragment.class),
    ADD(2, R.string.tabwidget__add, R.drawable.tabwidget_add_selector, null),
    EXPLORE(3, R.string.tabwidget__explore, R.drawable.tabwidget_explore_icon_selector, ExploreFragment.class),
    MY(4, R.string.tabwidget__my, R.drawable.tabwidget_my_icon_selector, MyInfoFragment.class);

    public int getResImgId() {
        return resImgId;
    }

    public Class<?> getC() {
        return c;
    }

    public int getResName() {
        return resName;
    }

    public int getId() {
        return id;
    }

    private final int id;
    private final int resName;
    private final int resImgId;
    private final Class c;

    MenuTab(int id, int resName, int resImgId, Class<?> c) {
        this.id = id;
        this.resName = resName;
        this.resImgId = resImgId;
        this.c = c;
    }
}
