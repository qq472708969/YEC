package com.yy.yec.ui.widget.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.global.App;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.widget.able.CommentClickListener;
import com.yy.yec.utils.ImgUtils;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.UIUtil;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq
 */
public class PartCommentsView extends LinearLayout {
    private final String mCacheTag = getClass().getSimpleName();//读取缓存的标记
    private long mId;
    private int mType;
    private TextView mTitle;
    private TextView mSeeMore;
    private LinearLayout mLayComments;

    public PartCommentsView(Context context) {
        this(context, null);
    }

    public PartCommentsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.comment_area_level_layout, this, true);

        mTitle = (TextView) findViewById(R.id.tv_blog_detail_comment);
        mLayComments = (LinearLayout) findViewById(R.id.lay_blog_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
    }

    public void setTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    public void init(final long id, int type, final int commentTotal, final CommentClickListener commentClickListener) {
        mId = id;
        mType = type;

        mSeeMore.setVisibility(View.GONE);
        setVisibility(GONE);

        Object commentPage = CacheManager.readObjectData(CacheManager.getCachePageFullTag(mCacheTag, String.valueOf(id)), CacheManager.app_detail_cache, true);
        if (commentPage != null) {
            requestComment(((RequestInfoPage<Comment>) commentPage).getItems(), commentTotal, commentClickListener);
            return;
        }

        AppWebApi.getListComments(id, type, "refer,reply", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<RequestInfo<RequestInfoPage<com.yy.yec.entity.common.Comment>>>() {
                    }.getType();

                    RequestInfo<RequestInfoPage<com.yy.yec.entity.common.Comment>> requestInfo = JsonUtil.createGson().fromJson(responseString, type);
                    if (requestInfo != null && requestInfo.isSuccess()) {
                        CacheManager.saveObjectData(requestInfo.getResult(), CacheManager.getCachePageFullTag(mCacheTag, String.valueOf(id)), CacheManager.app_detail_cache);
                        requestComment(requestInfo.getResult().getItems(), commentTotal, commentClickListener);
                        return;
                    }
                    onFailure(statusCode, headers, responseString, null);
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void requestComment(List<com.yy.yec.entity.common.Comment> comments, int commentTotal, CommentClickListener commentClickListener) {
        int sum = comments.size();
        if (comments == null || sum <= 0) return;
        if (sum < commentTotal) {
            mSeeMore.setVisibility(VISIBLE);
            mSeeMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailActivityFactory.startCommentListActivity(getContext(), mId, mType);
                }
            });
        }

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        for (int i = 0; i < sum; i++) {
            if (comments.get(i) == null || comments.get(i).getId() == 0 || StringUtils.isEmpty(comments.get(i).getAuthor()))
                continue;
            ViewGroup lay = addComment(false, comments.get(i), commentClickListener);
            if (i == sum - 1)
                lay.findViewById(R.id.v_bottom_distance).setVisibility(View.GONE);
        }
    }

    public void addComment(final com.yy.yec.entity.common.Comment comment, CommentClickListener onCommentClickListener) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        addComment(true, comment, onCommentClickListener);
    }

    public ViewGroup addComment(boolean addTopComment, final com.yy.yec.entity.common.Comment comment, final CommentClickListener onCommentClickListener) {
        ViewGroup lay = (ViewGroup) UIUtil.inflate(App.context(), R.layout.comment_area);
        ImageView ivAvatar = (ImageView) lay.findViewById(R.id.iv_avatar);
        ImgUtils.load(App.context(), comment.getAuthorPortrait(), R.mipmap.head_icon, ivAvatar);
        ivAvatar.setOnClickListener(new View.OnClickListener() {//跳转到个人主页的activity
            @Override
            public void onClick(View v) {

            }
        });

        ((TextView) lay.findViewById(R.id.tv_name)).setText(comment.getAuthor());
        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(StringUtils.formatSomeAgo(comment.getPubDate()));

        TextView content = (TextView) lay.findViewById(R.id.tv_content);
        content.setText(comment.getContent());

        if (comment.getRefer() != null) {
            ViewGroup lay_refer = (ViewGroup) lay.findViewById(R.id.lay_refer);
            CommentActive.addRefer(lay_refer, comment.getRefer());
        }

        lay.findViewById(R.id.tv_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClickListener.onClick(v, comment);
            }
        });

        if (addTopComment)
            mLayComments.addView(lay, 0);
        else
            mLayComments.addView(lay);
        return lay;
    }
}