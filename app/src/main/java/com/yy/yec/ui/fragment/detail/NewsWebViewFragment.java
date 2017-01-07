package com.yy.yec.ui.fragment.detail;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.widget.comment.CommentArea;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.able.detail.NewsDetailContract;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestType;
import com.yy.yec.entity.common.Software;
import com.yy.yec.entity.detail.NewsDetail;
import com.yy.yec.ui.fragment.detail.base.BaseWebViewFragment;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.ui.widget.comment.PartCommentsView;
import com.yy.yec.ui.widget.able.CommentClickListener;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;
import com.yy.yec.utils.UIUtil;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/10/27.
 */
public class NewsWebViewFragment extends BaseWebViewFragment<NewsDetail, NewsDetailContract.Operator> implements CommentClickListener {
    private long mId;
    @Bind(R.id.tv_pub_date)
    protected TextView mTVPubDate;
    @Bind(R.id.tv_title)
    protected TextView mTVTitle;
    @Bind(R.id.tv_info_view)
    protected TextView mTVName;
    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    //private DetailAboutView mAbouts;
    @Bind(R.id.lay_detail_comment)
    protected PartCommentsView mComments;
    @Bind(R.id.fragment_blog_detail)
    protected CoordinatorLayout mLayCoordinator;
    @Bind(R.id.lay_nsv)
    protected NestedScrollView mLayContent;
    @Bind(R.id.lay_option)
    protected CommentArea cArea;
    @Bind(R.id.tv_about_software_title)
    protected TextView mAbhoutSoftwareTitle;
    @Bind(R.id.lay_about_software)
    protected LinearLayout mAboutSoftware;
    @Bind(R.id.iv_info_view)
    protected ImageView mImgDetailAuthorInfo;
    @Bind(R.id.iv_info_comment)
    protected ImageView mImgDetailComment;
    @Bind(R.id.tv_info_comment)
    protected TextView mTxtDetailComment;
    private int currCommentCount;//加载详情页时的总评论数

    @OnClick(R.id.tv_info_view)
    public void tv_info_view_onClick(View v) {

    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mImgDetailComment.setVisibility(View.GONE);
        mImgDetailAuthorInfo.setVisibility(View.GONE);
        cArea.initBrow(BrowRules.getAllBrow());
        cArea.setCommentAreaCtrlListener(new CommentArea.CommentAreaCtrlListener<String>() {
            @Override
            public void sendCommentMsg(EditText editText, String s) {
                submitComment(mId, mCommentId, mCommentAuthorId, editText);
            }

            @Override
            public void keyCodeDelete(EditText editText, String s) {
                if (mCommentId != mId) {
                    if (StringUtils.isEmpty(editText.getText().toString())) {
                        if (mInputDoubleEmpty) {
                            mCommentId = mId;
                            mCommentAuthorId = 0;
                            editText.setHint(R.string.comment_placeholder);
                        } else {
                            mInputDoubleEmpty = true;
                        }
                    } else {
                        mInputDoubleEmpty = false;
                    }
                }
            }

            @Override
            public void share() {

            }

            @Override
            public void collection() {

            }
        });
        //registerScroller(mLayContent, mComments);
    }

    @Override
    protected void initData() {
        super.initData();
        NewsDetail newsDetail = getOperator().getData();
        if (newsDetail == null) return;
        mId = mCommentId = newsDetail.getId();//获取当前条目id，评论需要id唯一操作
        currCommentCount = newsDetail.getCommentCount();
        setCommentCount(currCommentCount);
        setHtmlBody(newsDetail.getBody());
        mTVName.setText(String.format("%s%s%s%s", "@", newsDetail.getAuthor(), "  ", "发布于 "));
        mTVPubDate.setText(StringUtils.formatSomeAgo(newsDetail.getPubDate()));
        mTVTitle.setText(newsDetail.getTitle());

        mTxtDetailComment.setText(StringUtils.formatYearMonthDay(newsDetail.getPubDate()));
        Software software = newsDetail.getSoftware();
        if (software != null) {
            mAboutSoftware.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mAbhoutSoftwareTitle.setText(software.getName());
        } else {
            mAboutSoftware.setVisibility(View.GONE);
        }
//        mAbouts.setAbout(newsDetail.getAbouts(), 6);
        mComments.setTitle(String.format("评论 (%s)", newsDetail.getCommentCount()));
        mComments.init(newsDetail.getId(), RequestType.Type_NEWS, newsDetail.getCommentCount(), this);
    }

    public void submitComment(long id, long commentId, long commentAuthorId, final EditText input) {
        if (StringUtils.isEmpty(input.getText().toString())) {
            ToastUtil.showToast(R.string.comment_context_empty);
            return;
        }

        int userId = getLoginUserId();
        if (userId == -1) return;

        AppWebApi.submitTypeComment(id, commentId, commentAuthorId, input.getText().toString(), RequestType.Type_NEWS, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                showWaitProgressDialog(getResources().getString(R.string.submit_comment_ing));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtil.showToast(getResources().getString(R.string.submit_failure_comment_ing));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<RequestInfo<Comment>>() {
                    }.getType();

                    RequestInfo<Comment> resultBean = JsonUtil.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Comment respComment = resultBean.getResult();
                        if (respComment != null) {
                            sendCommentSuccess(respComment, input);//提示消息提交成功
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                hideWaitProgressDialog();
            }
        });
    }

    private void sendCommentSuccess(Comment comment, EditText input) {
        ToastUtil.showToast(R.string.success_comments);
        int currCount = ++currCommentCount;
        mComments.setTitle(String.format("评论 (%s)", currCount));//评论标题总数更新
        if (currCommentCount < 999) setCommentCount(currCount);//评论成功马上设置最新的评论数
        mComments.addComment(comment, this);//comment返回的是带有html标签的内容，目前是有问题的
        input.setText("");
    }

    @Override
    public void onClick(View view, Comment comment) {
        UIUtil.animateBottomIn(cArea);
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthorId();
        cArea.saveParam(String.format("回复: %s", comment.getAuthor()));
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_news_webview;
    }

    @Override
    public void onDestroy() {
        BrowRules.clear();
        cArea = null;
        mComments = null;
        mAboutSoftware = null;

        mTVPubDate = null;

        mTVTitle = null;

        mTVName = null;

        mLayCoordinator = null;

        mLayContent = null;

        mAbhoutSoftwareTitle = null;

        mImgDetailAuthorInfo = null;

        mImgDetailComment = null;
        mTxtDetailComment = null;
        super.onDestroy();
    }
}