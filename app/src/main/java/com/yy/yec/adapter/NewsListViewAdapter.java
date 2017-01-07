package com.yy.yec.adapter;

import android.content.Context;

import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.entity.detail.News;
import com.yy.yec.holder.ViewHolder;
import com.yy.yec.ui.fragment.detail.NewsListFragment;
import com.yy.yec.ui.manager.HaveReadList;
import com.yy.yec.utils.StringUtils;

/**
 * Created by zzq on 2016/10/10.
 */
public class NewsListViewAdapter extends ListViewAdapter<News> {
    private String mTime;

    @Override
    protected int setContentView() {
        return R.layout.listview_news_item;
    }

    public void getServerTime(String time) {
        mTime = time;
    }

    @Override
    protected void processHolder(ViewHolder vh, News item, Context c, int position) {
        vh.setTextViewContent(R.id.tv_title, item.getTitle());

        if (HaveReadList.getInstance().getProperty(NewsListFragment.haveRead_news, String.valueOf(item.getId())).equals(HaveReadList.haveRead)) {//已经阅读过的，设置颜色
            vh.setTextViewColor(R.id.tv_title, R.color.haveRead_text_color_light);
            vh.setTextViewColor(R.id.tv_description, R.color.haveRead_text_color_light);
        } else {
            vh.setTextViewColor(R.id.tv_title, R.color.blog_title_text_color_light);
            vh.setTextViewColor(R.id.tv_description, R.color.ques_bt_text_color_dark);
        }

        vh.setTextViewContent(R.id.tv_description, item.getBody());//描述内容
        vh.setTextViewContent(R.id.tv_time, StringUtils.friendlyShowTime(item.getPubDate()));//有好方式显示时间
        vh.setTextViewContent(R.id.tv_comment_count, String.valueOf(item.getCommentCount()));//评价的次数

        if (StringUtils.sameDay(mTime, item.getPubDate())) {
            vh.setImg(R.id.iv_today, R.mipmap.ic_label_today);
            vh.setVisible(R.id.iv_today);
        } else {
            vh.setGone(R.id.iv_today);
        }
    }
}