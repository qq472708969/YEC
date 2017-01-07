package com.yy.yec.ui.activity.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.widget.comment.CommentArea;
import com.common.widget.comment.brow.BrowParam;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.detail.ReverseTweetLike;
import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.ui.activity.basic.LoginActivity;
import com.yy.yec.ui.activity.detail.base.BaseBackActivity;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.fragment.viewpager.TweetDetailViewPagerFragment;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.ui.widget.TweetPicturesLayout;
import com.yy.yec.utils.AssimilateUtils;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.HTMLUtil;
import com.yy.yec.utils.ImgUtils;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.PlatformUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;
import com.yy.yec.utils.UIUtil;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/11/21.
 */
public class TweetDetailActivity extends BaseBackActivity {
    public static String tweet_info = "tweet_info";
    public static int result_code = 11;
    private Tweet tweet;
    @Bind(R.id.appBar)
    AppBarLayout appBar;
    @Bind(R.id.iv_portrait)
    ImageView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.ca_cArea)
    public CommentArea<String> caArea;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_like)
    ImageView iv_like;
    @Bind(R.id.fragment_container)
    FrameLayout mFrameLayout;
    @Bind(R.id.tv_content)
    TextView mContent;
    @Bind(R.id.tv_client)
    TextView mAppClient;
    @Bind(R.id.tweet_pics_layout)
    TweetPicturesLayout mImgsLayout;
    private long currTime = -1;
    private long likeTimeSpan = 5 * 1000;
    private TweetDetailViewPagerFragment mPagerFrag;
    public View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!DeviceUtil.internet()) {
                ToastUtil.showToast(R.string.tip_no_internet);
                return;
            }
            if (!AppCurrentUser.getInstance().getLoginOK()) {
                DetailActivityFactory.startActivity(v.getContext(), -1, LoginActivity.class);//跳转登录页面
                return;
            }
            long currLikeTimeSpan = likeTimeSpan - (System.currentTimeMillis() - currTime);//多少毫秒后可以点赞，限制每次点赞间隔5秒
            if (currLikeTimeSpan > 1000 && currTime != -1) {
                ToastUtil.showToast(String.format(getResources().getString(R.string.tweet_like_Click), currLikeTimeSpan / 1000));
                return;
            }
            currTime = System.currentTimeMillis();//获取当前时间long值
            AppWebApi.reverseTweetLike(tweet.getId(), new TweetLikedHandler());
        }
    };
    private Intent updateListItemIntent;

    //点赞回调
    private class TweetLikedHandler extends TextHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<RequestInfo<ReverseTweetLike>>() {
                }.getType();
                RequestInfo<ReverseTweetLike> resultBean = JsonUtil.createGson().fromJson(responseString, type);
                ReverseTweetLike reverseTweetLike = resultBean.getResult();
                boolean isLiked = reverseTweetLike.isLiked();
                long likeCount = reverseTweetLike.getLikeCount();
                if (iv_like != null) {
                    iv_like.setImageResource(isLiked ? R.mipmap.ic_thumbup_actived : R.mipmap.ic_thumbup_normal);
                    Intent intent = new Intent(AppVariableConstants.UpdateLikeList);
                    intent.putExtra("updateLikeList", true);
                    LocalBroadcastManager.getInstance(TweetDetailActivity.this).sendBroadcast(intent);
                    setUpdateItemLike(isLiked);//更新ListView单条记录
                    setUpdateItemLikeCount(likeCount);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_tweet_detail;
    }

    @Override
    protected void initWidget() {
        setActionBarTitle(R.string.tweet_detail);
        processInputExtra();
        addFragment();
    }

    private void addFragment() {
        try {
            mPagerFrag = TweetDetailViewPagerFragment.class.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(tweet_info, tweet);
            mPagerFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPagerFrag).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void processInputExtra() {
        if (tweet.getAuthor() != null) {//设置作者的名字和头像
            if (TextUtils.isEmpty(tweet.getAuthor().getPortrait()))
                ivPortrait.setImageResource(R.mipmap.head_icon);
            else
                ImgUtils.load(this, tweet.getAuthor().getPortrait(), R.mipmap.head_icon, ivPortrait);
            tvNick.setText(tweet.getAuthor().getName());
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(tweet.getPubDate()));//设置日期
        iv_like.setOnClickListener(likeListener);
        PlatformUtil.setPlatFormString(mAppClient, tweet.getAppClient());
        if (tweet.isLiked())
            iv_like.setImageResource(R.mipmap.ic_thumbup_actived);
        else
            iv_like.setImageResource(R.mipmap.ic_thumbup_normal);
        if (!TextUtils.isEmpty(tweet.getContent())) {
            String content = tweet.getContent().replaceAll("[\n\\s]+", " ");//多个空格替换成一个
            Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(this, content);
            spannable = AssimilateUtils.assimilateOnlyLink(this, spannable);
            spannable = BrowParam.showBrow(this.getResources(), spannable, new BrowParam.SpannableMatcherStrResId() {
                @Override
                public int getResId(String matchStr) {
                    return BrowRules.getBrow(matchStr);
                }
            });
            mContent.setText(spannable);
            mContent.setMovementMethod(LinkMovementMethod.getInstance());//拉动TextView也能整体滑动
            mContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    UIUtil.copyText(HTMLUtil.delHTMLTag(tweet.getContent()));//复制文本
                    return true;
                }
            });
        }
        if (tweet.getImages() != null && tweet.getImages().length > 0)
            mImgsLayout.setImage(tweet.getImages());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    //回传Fragment中，更新ListView指定条目数据
    public void setUpdateItemCommentCount(long commentCount) {
        if (updateListItemIntent == null)
            updateListItemIntent = new Intent();
        updateListItemIntent.putExtra("commentCount", commentCount);
        setResult(result_code, updateListItemIntent);
    }

    public void setUpdateItemLike(boolean isLike) {
        if (updateListItemIntent == null)
            updateListItemIntent = new Intent();
        updateListItemIntent.putExtra("isLike", isLike);
        setResult(result_code, updateListItemIntent);
    }

    public void setUpdateItemLikeCount(long likeCount) {
        if (updateListItemIntent == null)
            updateListItemIntent = new Intent();
        updateListItemIntent.putExtra("likeCount", likeCount);
        setResult(result_code, updateListItemIntent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        tweet = (Tweet) getIntent().getSerializableExtra(tweet_info);
        if (tweet == null)
            return false;
        return true;
    }

    @Override
    protected void onDestroy() {
        caArea = null;
        tweet = null;
        ivPortrait = null;
        tvNick = null;
        tvTime = null;
        iv_like = null;
        mFrameLayout = null;
        mContent = null;
        mAppClient = null;
        mPagerFrag = null;
        mImgsLayout = null;
        likeListener = null;
        updateListItemIntent = null;
        super.onDestroy();
    }
}