package com.yy.yec.ui.activity.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.common.widget.comment.CommentArea;
import com.common.widget.LoadStateLayout;
import com.common.widget.ScrollRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.adapter.CommentNewsListAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.entity.common.RequestType;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.ui.activity.detail.base.BaseBackActivity;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.ui.widget.comment.CommentActive;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/9/17.
 */
public class CommentListActivity extends BaseBackActivity implements ScrollRefreshLayout.ScrollRefreshListener {
    private String mCacheTag = getClass().getSimpleName();//读取缓存的标记
    @Bind(R.id.ll_area)
    protected LinearLayout mLayout;
    @Bind(R.id.listView)
    protected ListView mListView;
    @Bind(R.id.scrollRefreshLayout)
    protected ScrollRefreshLayout mRefreshLayout;
    @Bind(R.id.lsl_statelayout)
    protected LoadStateLayout mLoadLayout;
    @Bind(R.id.lay_option)
    protected CommentArea<String> cArea;
    private CommentNewsListAdapter mAdapter;
    private long mId;
    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    private int mType;
    private boolean stateRefresh = false;
    private String currCacheTagPart = "";
    private RequestInfoPage<Comment> reqInfoContent;
    private TextHttpResponseHandler txtHandler = new TextHttpResponseHandler() {
        @Override
        public void onFinish() {
            if (mRefreshLayout != null)
                mRefreshLayout.loadMoreAndRefreshComplete();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mRefreshLayout != null)
                mRefreshLayout.setFooterState(ScrollRefreshLayout.State_Net_Error);//网络错误
            if (mLoadLayout != null && mAdapter.getList().size() == 0)
                mLoadLayout.setCurrState(LoadStateLayout.NetError);
            if (throwable != null)
                throwable.printStackTrace();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<RequestInfo<RequestInfoPage<Comment>>>() {
                }.getType();

                RequestInfo<RequestInfoPage<Comment>> requestInfo = JsonUtil.createGson().fromJson(responseString, type);
                if (requestInfo != null && requestInfo.isSuccess())
                    processItemsData(requestInfo);
                else
                    mRefreshLayout.setFooterState(ScrollRefreshLayout.State_No_More);//没有更多数据
            } catch (Exception e) {
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    @OnClick(R.id.lsl_statelayout)
    public void lsl_statelayout_onClick(View v) {
        addData();
    }

    private void processItemsData(RequestInfo<RequestInfoPage<Comment>> info) {
        if (stateRefresh) {
            currCacheTagPart = "";//刷新状态需要重置缓存标记，使其读取正确
            mListView.smoothScrollToPosition(0);//刷新成功定位到最上方
            stateRefresh = false;
        }

        RequestInfoPage<Comment> result = info.getResult();
        List<Comment> data = result.getItems();
        reqInfoContent.setItems(data);//存储展示数据
        reqInfoContent.setNextPageToken(result.getNextPageToken());//存储下一页要访问的数据

        if (currCacheTagPart.equals("")) {//刷新动作要清空适配器数据和所有缓存数据
            mAdapter.clear();
            reqInfoContent.setFirstPageToken(result.getFirstPageToken());//存储首页要访问的数据
            CacheManager.clear(CacheManager.app_list_cache, mCacheTag);
        }
        mAdapter.addItem(data);

        CacheManager.saveObjectData(info, CacheManager.getCachePageFullTag(mCacheTag, currCacheTagPart), CacheManager.app_list_cache);
        currCacheTagPart = reqInfoContent.getNextPageToken();

        if (result.getItems().size() < 20)
            mRefreshLayout.setFooterState(ScrollRefreshLayout.State_No_More);
        if (mAdapter.getList().size() > 0) {
            mLoadLayout.setCurrState(LoadStateLayout.Hide);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mLoadLayout.setCurrState(LoadStateLayout.NoData);
        }
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = mCommentId = bundle.getLong("id");
        mType = bundle.getInt("type");
        StringBuilder _sbTag = new StringBuilder();
        _sbTag.append(mCacheTag);
        _sbTag.append("_");
        _sbTag.append(mId);
        _sbTag.append(mType);
        mCacheTag = _sbTag.toString();
        return mId > 0;
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_comment_list;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        setActionBarTitle(R.string.comment_list);
        mLoadLayout.setCurrState(LoadStateLayout.NetLoading);
        mAdapter = new CommentNewsListAdapter(cArea);
        addData();
        mListView.setAdapter(mAdapter);
    }

    private void addData() {
        RequestInfo<RequestInfoPage<Comment>> reqInfoCache = (RequestInfo<RequestInfoPage<Comment>>) CacheManager.readObjectData(CacheManager.getCachePageFullTag(mCacheTag, currCacheTagPart), CacheManager.app_list_cache, true);
        if (reqInfoCache != null) {
            reqInfoContent = reqInfoCache.getResult();
            if (currCacheTagPart.equals("")) {
                reqInfoContent.setFirstPageToken(reqInfoContent.getFirstPageToken());//存储首页要访问的数据
                mAdapter.clear();
            }
            currCacheTagPart = reqInfoContent.getNextPageToken();
            mAdapter.addItem(reqInfoContent.getItems());
            mRefreshLayout.loadMoreAndRefreshComplete();
            mLayout.setVisibility(View.VISIBLE);
            mLoadLayout.setCurrState(LoadStateLayout.Hide);
        } else {
            if (reqInfoContent == null) {
                reqInfoContent = new RequestInfoPage<Comment>();
                requestNetData(true);
            } else {
                requestNetData(false);
            }
        }
    }

    @Override
    protected void initWidget() {
        mRefreshLayout.setColorSchemeResources(R.color.darker_blue, R.color.swiperefresh_color2, R.color.swiperefresh_color4, R.color.swiperefresh_color3);
        mRefreshLayout.setScrollRefreshListener(this);
        mRefreshLayout.setAllowLoadMore(true);//设置底部可以加载
        cArea.initBrow(BrowRules.getAllBrow());
        cArea.setCommentAreaCtrlListener(new CommentArea.CommentAreaCtrlListener<String>() {
            @Override
            public void sendCommentMsg(EditText editText, String s) {
                long commentId = 0;
                long commentAuthorId = 0;
                if (!StringUtils.isEmpty(s) && !editText.getHint().toString().contains(getResources().getString(R.string.comment_placeholder))) {
                    String[] _s = s.split(";");
                    commentId = Long.parseLong(_s[0]);
                    commentAuthorId = Long.parseLong(_s[1]);
                } else {
                    commentId = mCommentId;
                    commentAuthorId = mCommentAuthorId;
                }
                submitComment(mId, commentId, commentAuthorId, editText);
            }

            @Override
            public void keyCodeDelete(EditText editText, String s) {
                if (!StringUtils.isEmpty(s)) {
                    String[] _s = s.split(";");
                    mCommentId = Long.parseLong(_s[0]);
                }
                if (mCommentId != mId) {
                    if (StringUtils.isEmpty(editText.getText().toString())) {
                        if (mInputDoubleEmpty) {
                            mCommentId = mId;
                            mCommentAuthorId = 0;
                            editText.setHint(R.string.comment_placeholder);
                        } else
                            mInputDoubleEmpty = true;
                    } else
                        mInputDoubleEmpty = false;
                }
            }

            @Override
            public void share() {

            }

            @Override
            public void collection() {

            }
        });
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
                        if (respComment != null)
                            sendCommentSuccess(respComment, input);//提示消息提交成功
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

    private void sendCommentSuccess(Comment respComment, EditText input) {
        ToastUtil.showToast(R.string.success_comments);
        input.setText("");
        //向ListView中添加数据
        mAdapter.addItem(0, respComment);
    }

    /**
     * 检查当前数据，并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回-1
     */
    protected int getLoginUserId() {
        if (reqInfoContent.getItems().size() < 1) {
            ToastUtil.showToast("无数据...");
            return -1;
        }
        return CommentActive.allowSendComment(this);
    }

    @Override
    protected void onDestroy() {
        BrowRules.clear();
        mCacheTag = null;
        mLayout = null;
        mListView = null;
        mRefreshLayout = null;
        mLoadLayout = null;
        cArea = null;
        mAdapter = null;
        currCacheTagPart = null;
        reqInfoContent = null;
        txtHandler = null;
        super.onDestroy();
    }

    @Override
    public void onRefreshing() {
        stateRefresh = true;
        requestNetData(true);//加载首页数据
    }

    private void requestNetData(boolean b) {
        AppWebApi.getListComments(mId, mType, "refer", b ? null : reqInfoContent.getNextPageToken(), txtHandler);
    }

    @Override
    public void onLoadMore() {
        addData();
    }
}