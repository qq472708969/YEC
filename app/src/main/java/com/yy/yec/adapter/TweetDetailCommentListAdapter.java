package com.yy.yec.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.common.widget.comment.CommentArea;
import com.common.widget.comment.brow.BrowParam;
import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.entity.detail.TweetComment;
import com.yy.yec.holder.ViewHolder;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.utils.AssimilateUtils;
import com.yy.yec.utils.StringUtils;

/**
 * Created by zzq on 2016/11/24.
 */
public class TweetDetailCommentListAdapter extends ListViewAdapter<TweetComment> {
    private final CommentArea<String> cArea;

    public TweetDetailCommentListAdapter(CommentArea cArea) {
        this.cArea = cArea;
    }

    @Override
    protected void processHolder(ViewHolder vh, final TweetComment item, Context c, int position) {
        vh.setImageForNet(c, R.id.iv_avatar, item.getAuthor().getPortrait(), R.mipmap.head_icon);
        vh.setTextViewContent(R.id.tv_name, item.getAuthor().getName());
        vh.setTextViewContent(R.id.tv_pub_date, StringUtils.formatSomeAgo(item.getPubDate()));
        vh.setOnClickListener(R.id.tv_comment, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = String.format("回复 %s  :", item.getAuthor().getName());
                cArea.saveParam(String.format("回复: %s", item.getAuthor().getName()), nameStr);
            }
        });
        TextView tv_content = vh.getView(R.id.tv_content);
        String content = "";
        if (!TextUtils.isEmpty(item.getContent())) {
            content = item.getContent().replaceAll("[\n\\s]+", " ");
        }
        Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(c, content);
        spannable = AssimilateUtils.assimilateOnlyLink(c, spannable);
        spannable = BrowParam.showBrow(c.getResources(), spannable, new BrowParam.SpannableMatcherStrResId() {
            @Override
            public int getResId(String matchStr) {
                return BrowRules.getBrow(matchStr);
            }
        });
        tv_content.setText(spannable);
//        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
//        tv_content.setFocusable(false);
    }

    @Override
    protected int setContentView() {
        return R.layout.comment_area;
    }
}