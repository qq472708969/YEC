package com.yy.yec.ui.fragment.detail.base;

import android.view.View;
import android.widget.AdapterView;

import com.common.widget.LoadStateLayout;
import com.common.widget.ScrollRefreshLayout;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.able.basic.OnTabTouchRepeatListener;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.ui.widget.NestedScrollListView;
import com.yy.yec.utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/10/8.
 */
public abstract class BaseListFragment<T> extends BaseDetailFragment implements ScrollRefreshLayout.ScrollRefreshListener, OnTabTouchRepeatListener {
    @Bind(R.id.listView)
    protected NestedScrollListView mListView;
     @Bind(R.id.scrollRefreshLayout)
    protected ScrollRefreshLayout mRefreshLayout;
    @Bind(R.id.lsl_statelayout)
    protected LoadStateLayout mLoadStateLayout;
    private ListViewAdapter<T> mAdapter;
    private RequestInfoPage<T> reqInfoContent;
    private String currCacheTagPart = "";
    private boolean stateRefresh = false;
    private int pageSize = 20;
    private TextHttpResponseHandler mHandle = new TextHttpResponseHandler() {//加载网络数据使用
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            RequestInfo<RequestInfoPage<T>> info = JsonUtil.createGson().fromJson(responseString, getType());
            if (info != null && info.isSuccess()) //数据没问题了
                processItemsData(info);
            else
                mRefreshLayout.setFooterState(ScrollRefreshLayout.State_No_More);//没有更多数据
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mRefreshLayout == null || mLoadStateLayout == null) return;
            mRefreshLayout.setFooterState(ScrollRefreshLayout.State_Net_Error);//网络错误
            if (mAdapter.getList().size() == 0)
                if (enableLoadStateLayout())
                    mLoadStateLayout.setCurrState(LoadStateLayout.NetError);
        }

        @Override
        public void onFinish() {
            if (mRefreshLayout != null)
                mRefreshLayout.loadMoreAndRefreshComplete();
        }
    };

    /**
     * 主要记录当前ListView状态的下一页和首页的请求参数，大范围包含最后一次请求体的内容
     *
     * @return
     */
    protected RequestInfoPage<T> getCurrReqInfoEntity() {
        return reqInfoContent;
    }

    /**
     * 主要记录当前ListView状态的下一页和首页的请求参数，大范围包含最后一次请求体的内容
     *
     * @return
     */
    protected TextHttpResponseHandler getRespHandler() {
        return mHandle;
    }

    @Override
    public void onTouchRepeat() {//选中之后触摸再次刷新
        addData();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_list_basic;
    }

    @OnItemClick(R.id.listView)
    public void listView_onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击条目打开
    }

    @OnItemLongClick(R.id.listView)
    public boolean listView_onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //长按条目打开
        return false;
    }

    @Override
    protected void initWidget(View view) {
        mListView.setNestedScrollingEnabled(true);
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2, R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mRefreshLayout.setScrollRefreshListener(this);
        mRefreshLayout.setAllowLoadMore(true);//设置底部可以加载
        if (enableLoadStateLayout()) {//初始化加载遮罩层
            mLoadStateLayout.setCurrState(LoadStateLayout.NetLoading);
            mLoadStateLayout.setOnFailClickListener(new View.OnClickListener() {//任何失败的状态之后点击重新加载数据
                @Override
                public void onClick(View v) {
                    mLoadStateLayout.setCurrState(LoadStateLayout.NetLoading);
                    addData();
                }
            });
        } else
            mLoadStateLayout.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {//装载adapter的数据
        mAdapter = getListAdapter();
        addData();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadMore() {
        addData();
    }

    @Override
    public void onRefreshing() {
        stateRefresh = true;
        requestNetData(true);//加载首页数据
    }

    private void processItemsData(final RequestInfo<RequestInfoPage<T>> info) {
        if (mLoadStateLayout == null || mRefreshLayout == null) return;
        if (stateRefresh) {
            currCacheTagPart = "";//刷新状态需要重置缓存标记，使其读取正确
            mListView.smoothScrollToPosition(0);//刷新成功定位到最上方
            stateRefresh = false;
        }

        getServerTime(info.getTime());
        RequestInfoPage<T> result = info.getResult();
        getTotal(result.getTotalResults());//getPageInfo可能没有
        List<T> data = result.getItems();

        if (currCacheTagPart.equals("") && data.size() < 1) {//无数据状态
            if (enableLoadStateLayout())
                mLoadStateLayout.setCurrState(LoadStateLayout.NoData);
            mRefreshLayout.setFooterState(ScrollRefreshLayout.State_No_More);
            mAdapter.clear();
            return;
        }
        if (data.size() < pageSize)
            mRefreshLayout.setFooterState(ScrollRefreshLayout.State_No_More);
        if (data.size() > 0) {
            if (enableLoadStateLayout())
                mLoadStateLayout.setCurrState(LoadStateLayout.Hide);
            mRefreshLayout.setVisibility(View.VISIBLE);
        }

        reqInfoContent.setItems(data);//存储展示数据
        reqInfoContent.setNextPageToken(result.getNextPageToken());//存储下一页要访问的数据
        if (currCacheTagPart.equals("")) {//刷新动作要清空适配器数据和所有缓存数据
            mAdapter.clear();
            reqInfoContent.setFirstPageToken(result.getFirstPageToken());//存储首页要访问的数据
            CacheManager.clear(CacheManager.app_list_cache, getCacheTag());
        }
        mAdapter.addItem(data);
        CacheManager.saveObjectData(info, CacheManager.getCachePageFullTag(getCacheTag(), currCacheTagPart), CacheManager.app_list_cache);
        currCacheTagPart = reqInfoContent.getNextPageToken();
    }

    protected boolean enableLoadStateLayout() {//是否开启遮罩状态布局
        return true;
    }

    protected void getTotal(long totalResults) {//返回服务器item总条数
    }

    /**
     * 检查缓存并添加数据
     */
    protected void addData() {
        RequestInfo<RequestInfoPage<T>> reqInfoCache = (RequestInfo<RequestInfoPage<T>>) CacheManager.readObjectData(CacheManager.getCachePageFullTag(getCacheTag(), currCacheTagPart), CacheManager.app_list_cache, true);
        if (reqInfoCache != null) {
            getServerTime(reqInfoCache.getTime());//读取缓存中的获取数据时间
            reqInfoContent = reqInfoCache.getResult();

            if (currCacheTagPart.equals("")) {
                mAdapter.clear();
                reqInfoContent.setFirstPageToken(reqInfoContent.getFirstPageToken());//存储首页要访问的数据
            }

            currCacheTagPart = reqInfoContent.getNextPageToken();
            mAdapter.addItem(reqInfoContent.getItems());
            mRefreshLayout.loadMoreAndRefreshComplete();//加载更多的时候都需要调用该方法
            mRefreshLayout.setVisibility(View.VISIBLE);
            if (enableLoadStateLayout())
                mLoadStateLayout.setCurrState(LoadStateLayout.Hide);
        } else {
            if (reqInfoContent == null) {
                reqInfoContent = new RequestInfoPage<T>();
                requestNetData(true);
            } else {
                requestNetData(false);
            }
        }
    }

    protected abstract String getCacheTag();

    protected abstract ListViewAdapter<T> getListAdapter();

    protected abstract void requestNetData(boolean reqFirst);

    protected abstract Type getType();

    protected abstract void getServerTime(String time);//子类通过这个方法获取服务器上的时间
}