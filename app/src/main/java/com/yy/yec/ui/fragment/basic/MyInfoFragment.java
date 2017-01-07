package com.yy.yec.ui.fragment.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.detail.User;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.ui.activity.basic.LoginActivity;
import com.yy.yec.ui.activity.detail.SimpleActivity;
import com.yy.yec.ui.base.BaseFragment;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.ImgUtils;
import com.yy.yec.utils.JsonUtil;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/9/27.
 */
public class MyInfoFragment extends BaseFragment {
    public static final String loginUserInfo = "_loginUserInfo";
    @Bind(R.id.iv_portrait)
    protected ImageView mPortrait;
    @Bind(R.id.tv_nick)
    protected TextView mTvName;
    @Bind(R.id.iv_gender)
    protected ImageView mIvGander;
    @Bind(R.id.tv_score)
    protected TextView mTvScore;
    @Bind(R.id.ly_about_info)
    protected LinearLayout layAboutCount;
    @Bind(R.id.tv_summary)
    protected TextView mTvSummary;
    @Bind(R.id.tv_tweet)
    protected TextView mTvTweetCount;
    @Bind(R.id.tv_favorite)
    protected TextView mTvFavoriteCount;
    @Bind(R.id.tv_follow_count)
    protected TextView mTvFollowCount;
    @Bind(R.id.tv_follower)
    protected TextView mTvFollowerCount;
    User mUser = null;
    private TextHttpResponseHandler textHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mUser == null) //没有缓存，请求处理失败的情况则隐藏用户成功登录时的相关界面
                hideUserInfoView();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<RequestInfo<User>>() {
                }.getType();

                RequestInfo resultInfo = JsonUtil.createGson().fromJson(responseString, type);
                if (resultInfo.isSuccess()) {
                    mUser = (User) resultInfo.getResult();
                    CacheManager.saveObjectData(mUser, loginUserInfo, CacheManager.app_global_cache);//缓存个人详细信息
                    updateUserInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    @OnClick(R.id.iv_portrait)
    protected void iv_portrait_onClick() {
        if (AppCurrentUser.getInstance().getLoginOK()) {

        } else
            DetailActivityFactory.startActivity(getActivity(), -1, LoginActivity.class);//未登录跳转登录页面
    }

    @OnClick(R.id.iv_logo_setting)
    protected void iv_logo_setting_onClick() {
        DetailActivityFactory.startSimpleActivity(getActivity(), SimpleActivity.UserSetting, getResources().getString(R.string.user_setting));//启动个人设置
    }

    @Override
    protected void init(View contentView) {

    }

    private void updateUserData() {
        mUser = (User) CacheManager.alwaysReadObjectData(loginUserInfo, CacheManager.app_global_cache);//读取缓存个人详细信息
        if (mUser != null)
            updateUserInfo();
        if (DeviceUtil.internet())
            requestUserInfo();
    }

    @Override
    protected void initComplete(View view, @Nullable Bundle savedInstanceState) {
        if (AppCurrentUser.getInstance().getLoginOK())
            updateUserData();
        else
            hideUserInfoView();
    }

    private void updateUserInfo() {
        ImgUtils.load(getActivity(), mUser.getPortrait(), R.mipmap.head_icon, mPortrait);//加载用户的头像
        mTvName.setText(mUser.getName());//设置登陆用户的名称
        mTvName.setVisibility(View.VISIBLE);

        switch (mUser.getGender()) {//设置性别
            case 0:
                mIvGander.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_male);
                break;
            case 2:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_female);
                break;
            default:
                break;
        }
        mTvSummary.setText(mUser.getDesc());//设置个性留言
        mTvSummary.setVisibility(View.VISIBLE);
        mTvScore.setText(String.format("%s  %s", getString(R.string.user_score), formatCount(mUser.getStatistics().getScore())));//积分
        mTvScore.setVisibility(View.VISIBLE);
        layAboutCount.setVisibility(View.VISIBLE);//登陆之后的用户信息显示
        mTvTweetCount.setText(formatCount(mUser.getStatistics().getTweet()));//动弹
        mTvFavoriteCount.setText(formatCount(mUser.getStatistics().getCollect()));//收藏
        mTvFollowCount.setText(formatCount(mUser.getStatistics().getFollow()));//关注
        mTvFollowerCount.setText(formatCount(mUser.getStatistics().getFans()));//粉丝
    }

    private void hideUserInfoView() {
        mPortrait.setImageResource(R.mipmap.head_icon);
        mTvName.setText("点击头像登录");
        mIvGander.setVisibility(View.INVISIBLE);
        mTvSummary.setVisibility(View.INVISIBLE);
        mTvScore.setVisibility(View.INVISIBLE);
        layAboutCount.setVisibility(View.INVISIBLE);
    }

    /**
     * format count
     *
     * @param count count
     * @return formatCount
     */
    private String formatCount(long count) {
        if (count > 1000) {
            int a = (int) (count / 100);
            int b = a % 10;
            int c = a / 10;
            String str;
            if (c <= 9 && b != 0)
                str = c + "." + b;
            else
                str = String.valueOf(c);
            return str + "k";
        } else
            return String.valueOf(count);
    }

    @Override
    public void onResume() {//登陆成填充信息
        super.onResume();
        if (AppCurrentUser.getInstance().getLoginOK())
            updateUserData();
        else hideUserInfoView();
    }

    private void requestUserInfo() {
        AppWebApi.getUserInfo(textHandler);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_user_home;
    }
}