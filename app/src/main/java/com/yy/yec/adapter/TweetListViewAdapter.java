package com.yy.yec.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.widget.comment.brow.BrowParam;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.detail.ReverseTweetLike;
import com.yy.yec.entity.detail.Tweet;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.holder.ViewHolder;
import com.yy.yec.ui.activity.basic.LoginActivity;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.ui.widget.TweetPicturesLayout;
import com.yy.yec.utils.AssimilateUtils;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.PlatformUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/10/15.
 */
public class TweetListViewAdapter extends ListViewAdapter<Tweet> {
    private String mTime;
    private LikeCtrlView lcView;
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
            LikeCtrlView likeCtrlView = (LikeCtrlView) v.getTag();
            AppWebApi.reverseTweetLike(likeCtrlView.getItem().getId(), new TweetLikedHandler(likeCtrlView.getItem(), likeCtrlView.getLikeViewKey(), likeCtrlView.getLikeCountViewKey()));
        }
    };

    public void getServerTime(String time) {
        mTime = time;
    }

    @Override
    protected void processHolder(ViewHolder vh, Tweet item, Context c, int position) {
        vh.setImageForNet(c, R.id.iv_tweet_face, item.getAuthor().getPortrait(), R.mipmap.head_icon);//头像
        vh.setTextViewContent(R.id.tv_tweet_name, item.getAuthor().getName());//用户名
        vh.setTextViewContent(R.id.tv_tweet_time, StringUtils.formatSomeAgo(item.getPubDate()));//时间
        PlatformUtil.setPlatFormString((TextView) vh.getView(R.id.tv_tweet_platform), item.getAppClient());//平台
        vh.setTextViewContent(R.id.tv_tweet_like_count, String.valueOf(item.getLikeCount()));//赞数
        vh.setTextViewContent(R.id.tv_tweet_comment_count, String.valueOf(item.getCommentCount()));//评论数

        TextView tv_content = vh.getView(R.id.tweet_item);

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

        ImageView iv_tweet_like = vh.getView(R.id.iv_like_state);
        iv_tweet_like.setImageResource(item.isLiked() ? R.mipmap.ic_thumbup_actived : R.mipmap.ic_thumbup_normal);//根据是否点赞而添加相应的图片
        lcView = new LikeCtrlView(item, (TextView) vh.getView(R.id.tv_tweet_like_count), iv_tweet_like);
        iv_tweet_like.setTag(lcView);
        iv_tweet_like.setOnClickListener(likeListener);

        TweetPicturesLayout flowLayout = vh.getView(R.id.fl_image);
        if (item.getImages() == null || item.getImages().length < 1) {
            flowLayout.removeAllImage();
            return;
        }
        Tweet.Image[] images = item.getImages();
        flowLayout.setImage(images);
    }

    @Override
    protected int setContentView() {
        return R.layout.listview_tweet_item;
    }

    private class LikeCtrlView {
        private final Tweet item;
        private final TextView likeCountViewKey;
        private final ImageView likeViewKey;

        public Tweet getItem() {
            return item;
        }

        public TextView getLikeCountViewKey() {
            return likeCountViewKey;
        }

        public ImageView getLikeViewKey() {
            return likeViewKey;
        }

        LikeCtrlView(Tweet item, TextView likeCountViewKey, ImageView likeViewKey) {
            this.item = item;
            this.likeCountViewKey = likeCountViewKey;
            this.likeViewKey = likeViewKey;
        }
    }

    //点赞回调
    private class TweetLikedHandler extends TextHttpResponseHandler {
        private final Tweet tw;
        private final ImageView likeView;
        private final TextView countView;

        public TweetLikedHandler(Tweet tw, ImageView likeView, TextView countView) {
            this.tw = tw;
            this.likeView = likeView;
            this.countView = countView;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<RequestInfo<ReverseTweetLike>>() {
                }.getType();
                RequestInfo<ReverseTweetLike> resultBean = JsonUtil.createGson().fromJson(responseString, type);
                boolean isLiked = resultBean.getResult().isLiked();
                likeView.setImageResource(isLiked ? R.mipmap.ic_thumbup_actived : R.mipmap.ic_thumbup_normal);
                long likeCount = resultBean.getResult().getLikeCount();
                countView.setText(String.valueOf(likeCount));
                //将view的状态和评论数设置之后，要将Adapter中的数据源中的item对象数据更新，再次切换回ListView的时候可以重新绑定新数据
                //******但不要立即调用notifyDataSetChanged()这样会导致item中数据马上重新绑定，会“闪烁一下”用户体验差******
                tw.setLikeCount(likeCount);
                tw.setLiked(isLiked);
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    }
}