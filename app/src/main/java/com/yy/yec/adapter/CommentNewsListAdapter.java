package com.yy.yec.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.common.widget.comment.CommentArea;
import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.holder.ViewHolder;
import com.yy.yec.ui.widget.comment.CommentActive;

/**
 * Created by zzq on 2016/9/17.
 */
public class CommentNewsListAdapter extends ListViewAdapter<Comment> {
    private final CommentArea<String> cArea;

    public CommentNewsListAdapter(CommentArea cArea) {
        this.cArea = cArea;
    }

    @Override
    protected void processHolder(ViewHolder vh, final Comment item, Context c, int position) {
        vh.setTextViewContent(R.id.tv_name, item.getAuthor());
        vh.setTextViewContent(R.id.tv_pub_date, item.getPubDate());
        vh.setTextViewContent(R.id.tv_content, item.getContent());
        vh.setImg(R.id.iv_avatar, item.getAuthorPortrait(), R.mipmap.head_icon, c);
        vh.setOnClickListener(R.id.iv_avatar, new View.OnClickListener() {//点击跳转个人主页
            @Override
            public void onClick(View v) {

            }
        });
        LinearLayout ll_refer = vh.getView(R.id.lay_refer);
        if (ll_refer.getChildCount() > 0) {
            ll_refer.removeAllViews();
        }
        if (item.getRefer() != null) {//追加评论
            CommentActive.addRefer(ll_refer, item.getRefer());
        }
        vh.setOnClickListener(R.id.tv_comment, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cArea.saveParam(String.format("回复: %s", item.getAuthor()), item.getId() + ";" + item.getAuthorId());
            }
        });
    }

    @Override
    protected int setContentView() {
        return R.layout.comment_area;
    }
}