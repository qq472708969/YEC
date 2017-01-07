package com.common.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * by zzq 2016/5/23.
 */
public class ScrollRefreshLayout extends SwipeRefreshLayout implements View.OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int State_Loading = 0;
    public static final int State_Net_Error = 1;
    public static final int State_Error = 2;
    public static final int State_No_More = 3;
    private ListView mListView;
    private int mTouchSlop;
    private ScrollRefreshListener mListener;
    private boolean mStateLoading = false;
    private boolean mAllowLoadMore = false;
    private int mDownY;
    private int mUpY;
    private Context mContext;
    private View footerView;
    private ProgressBar pb_footer;
    private TextView tv_footer;

    public interface ScrollRefreshListener {
        void onRefreshing();

        void onLoadMore();
    }

    public ScrollRefreshLayout(Context context) {
        this(context, null);
    }

    public ScrollRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//出发事件，移动的最小距离
        setOnRefreshListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mListView == null) getListView();//初始化ListView对象
    }

    /**
     * 获取ListView并添加Footer
     */
    private void getListView() {
        if (getChildCount() <= 0) return;
        View childView = getChildAt(1);//0为下拉进度提示控件
        if (!(childView instanceof ListView)) return;
        mListView = (ListView) childView;
        mListView.setOnScrollListener(this);//设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
    }

    @Override
    public final void onScrollStateChanged(AbsListView view, int scrollState) {
        loadMoreData(false);
    }

    @Override
    public final void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {//不在这里判断滚动状态，点击ListView的Item时也会调用
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return 是否可以加载更多
     */
    private boolean allowLoadMore(boolean click) {
        return mAllowLoadMore && inBottom() && (click ? true : pullUp()) && mListener != null && !mStateLoading;
    }

    @Override
    public void onRefresh() {
        if (mListener == null || mStateLoading) {
            setRefreshing(false);//不允许刷新，要马上调整状态
            return;
        }
        mListener.onRefreshing();
    }

    private void loadMoreData(boolean click) {
        if (!allowLoadMore(click)) return;//如果到了最底部，而且是上划操作那么执行onLoad方法***刷新时可以加载更多
        mStateLoading = true;
        setFooterState(State_Loading);
        mListener.onLoadMore();
    }

    /**
     * 是否是上拉操作
     *
     * @return 是否是上拉操作
     */
    private boolean pullUp() {
        return (mDownY - mUpY) >= mTouchSlop;
    }

    /**
     * 判断是否到了最底部
     */
    private boolean inBottom() {//判断，ListView不为空的，并且adapter不为空。并且滑倒最后一个item
        return mListView != null && mListView.getAdapter() != null && mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
    }

    /**
     * 加载结束记得调用
     */
    public final void loadMoreAndRefreshComplete() {
        mDownY = mUpY = 0;//加载完成后需要重置，否则影响下次其它加载更多条件满足时，pullUp中的mDownY和mUpY会保留上次的值
        mStateLoading = false;
        setRefreshing(false);
    }

    @Override
    public final boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                mDownY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE://移动
                mUpY = (int) event.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public final void setScrollRefreshListener(ScrollRefreshListener listener) {
        mListener = listener;
    }

    public final void setFooterState(int state) {
        if (!mAllowLoadMore) return;
        switch (state) {
            case State_Loading:
                pb_footer.setVisibility(View.VISIBLE);
                tv_footer.setText(getResources().getString(R.string.footer_state_loading));
                break;
            case State_Net_Error:
                pb_footer.setVisibility(View.GONE);
                tv_footer.setText(getResources().getString(R.string.footer_state_net_error));
                break;
            case State_Error:
                pb_footer.setVisibility(View.GONE);
                tv_footer.setText(getResources().getString(R.string.footer_state_error));
                break;
            case State_No_More:
                pb_footer.setVisibility(View.GONE);
                tv_footer.setText(getResources().getString(R.string.footer_state_not_more));
                break;
        }
    }

    public final void setAllowLoadMore(boolean allow) {
        mAllowLoadMore = allow;
        if (!mAllowLoadMore) return;
        footerView = View.inflate(mContext, R.layout.list_footer_layout, null);
        pb_footer = (ProgressBar) footerView.findViewById(R.id.pb_footer);
        tv_footer = (TextView) footerView.findViewById(R.id.tv_footer);
        footerView.setOnClickListener(this);
        mListView.addFooterView(footerView);
        setFooterState(State_Loading);
    }

    @Override
    public final void onClick(View v) {
        loadMoreData(true);
    }
}