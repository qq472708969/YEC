package com.yy.yec.ui.activity.detail.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.common.widget.LoadStateLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.able.basic.DetailContract;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.ui.fragment.detail.base.BaseWebViewFragment;
import com.yy.yec.utils.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/10/23.
 */
public abstract class BaseDetailActivity<TData extends Serializable> extends BaseBackActivity implements DetailContract.Operator<TData> {
    long mItemId;
    @Bind(R.id.stateLayout)
    protected LoadStateLayout StateLayout;
    private TData mData;//根据列表的id返回的具体ListView的条目数据内容

    @Override
    protected int setContentView() {
        return R.layout.activity_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mItemId = bundle.getLong("id", 0);
        return mItemId != 0;
    }

    private AsyncHttpResponseHandler handler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            throwable.printStackTrace();
            if (StateLayout != null)
                StateLayout.setCurrState(StateLayout.NetError);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if (!processData(responseString))
                StateLayout.setCurrState(StateLayout.NetError);
        }
    };
    private TextView mCommentCountView;//总评论条数

    protected AsyncHttpResponseHandler getRequestHandler() {
        return handler;
    }

    private boolean processData(String responseString) {
        RequestInfo<TData> result;
        try {
            result = JsonUtil.createGson().fromJson(responseString, getDataType());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (result.isSuccess()) {
            mData = result.getResult();
            if (mData != null)
                CacheManager.saveObjectData(mData, CacheManager.getCachePageFullTag(getCacheTag(), String.valueOf(mItemId)), CacheManager.app_detail_cache);
            handleView();
            return true;
        }
        return false;
    }

    private void handleView() {
        try {
            Fragment fragment = getDataViewFragment().newInstance();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.lay_container, fragment);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected long getItemId() {
        return mItemId;
    }

    //Operator--------------start--------------
    @Override
    public TData getData() {//向Fragment传递数据
        return mData;
    }

    @Override
    public void hideLoading() {//隐藏加载整个webview的内容状态布局
        if (StateLayout == null) return;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.webview_detail_alpha_hide_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StateLayout.setCurrState(LoadStateLayout.Hide);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        StateLayout.startAnimation(animation);
    }

    @Override
    public void setCommentCount(int count) {//向Activity设置评论条数
        final TextView view = mCommentCountView;
        if (view == null) return;
        String str;
        if (count <= 999)
            str = String.valueOf(count);
        else
            str = String.valueOf(count) + "+";
        view.setText(str);
    }

    //Operator--------------end--------------
    @Override
    protected void initData(Bundle savedInstanceState) {
        mData = (TData) CacheManager.readObjectData(CacheManager.getCachePageFullTag(getCacheTag(), String.valueOf(mItemId)), CacheManager.app_detail_cache, true);
        //使用一个异步处理的原因是，onCreateOptionsMenu的执行在onCreate之后，所以在onCreate向Menu中设置数据的时候，Menu中的View还未创建
        StateLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mData != null)
                    handleView();
                else
                    requestData();
            }
        });
    }

    @Override
    protected void initWidget() {
    }

    @OnClick(R.id.stateLayout)
    public void stateLayout_onClick(View v) {
        if (StateLayout != null && StateLayout.getCurrState() != LoadStateLayout.Hide) {
            StateLayout.setCurrState(LoadStateLayout.NetLoading);
            requestData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//将显示条目控件添加到menu中
        int menuId = R.menu.comment_menu;
        if (menuId <= 0)
            return false;
        getMenuInflater().inflate(menuId, menu);//构造菜单
        MenuItem item = menu.findItem(R.id.comment_count);//获取某一菜单
        View action = item.getActionView();
        if (item != null && action != null) {
            View comment = action.findViewById(R.id.tv_comment_count);
            if (comment != null)
                mCommentCountView = (TextView) comment;//评论控件全局存储
        }
        return true;
    }

    protected abstract Class<? extends BaseWebViewFragment> getDataViewFragment();

    protected abstract Type getDataType();

    protected abstract String getCacheTag();

    protected abstract void requestData();

    @Override
    protected void onDestroy() {
        mData = null;
        handler.onCancel();
        handler = null;
        super.onDestroy();
    }
}