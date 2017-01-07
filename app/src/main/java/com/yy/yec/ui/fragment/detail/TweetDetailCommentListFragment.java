package com.yy.yec.ui.fragment.detail;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.common.widget.comment.CommentArea;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.adapter.TweetDetailCommentListAdapter;
import com.yy.yec.adapter.base.ListViewAdapter;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.common.RequestInfoPage;
import com.yy.yec.entity.detail.TweetComment;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.ui.activity.detail.TweetDetailActivity;
import com.yy.yec.ui.fragment.detail.base.tweet.TweetDetailListFragment;
import com.yy.yec.ui.widget.BrowRules;
import com.yy.yec.ui.widget.TweetItemCtrlDialog;
import com.yy.yec.ui.widget.comment.CommentActive;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.ToastUtil;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/11/22.
 */
public class TweetDetailCommentListFragment extends TweetDetailListFragment<TweetComment> {
    private TweetDetailCommentListAdapter mAdapter;
    private String cacheTag = getClass().getSimpleName();
    private CommentArea<String> cArea;
    private long tweetId;
    private EditText input;
    private boolean clearReply = false;
    private TextHttpResponseHandler sendResHandler = new TextHttpResponseHandler() {
        @Override
        public void onStart() {
            showWaitProgressDialog(getResources().getString(R.string.submit_comment_ing));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            ToastUtil.showToast(getResources().getString(R.string.submit_failure_comment_ing));
        }

        @Override
        public void onFinish() {
            hideWaitProgressDialog();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<RequestInfo<TweetComment>>() {
                }.getType();

                RequestInfo<TweetComment> resultBean = JsonUtil.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    TweetComment respComment = resultBean.getResult();
                    if (respComment != null)
                        sendCommentSuccess(respComment);//提示消息提交成功
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    private void sendCommentSuccess(TweetComment respComment) {
        ToastUtil.showToast(R.string.success_comments);
        input.setText("");
        //向ListView中添加数据
        mAdapter.addItem(0, respComment);

        Intent intent = new Intent(AppVariableConstants.UpdateTitleLayout);
        intent.putExtra("index", 0);
        intent.putExtra("val", true);//true增加一条
        intent.putExtra("total", -1);//总条数
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    protected void getTotal(long totalResults) {
        Intent intent = new Intent(AppVariableConstants.UpdateTitleLayout);
        intent.putExtra("index", 0);
        intent.putExtra("total", totalResults);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        cArea = ((TweetDetailActivity) getActivity()).caArea;
        tweetId = getCurrTweet().getId();
        cArea.initBrow(BrowRules.getAllBrow());
        cArea.setCommentAreaCtrlListener(new CommentArea.CommentAreaCtrlListener<String>() {
            @Override
            public void sendCommentMsg(EditText editText, String name) {
                input = editText;
                String content = input.getText().toString();
                if (!StringUtils.isEmpty(name))
                    content = name + content;
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToast(R.string.comment_context_empty);
                    return;
                }

                int userId = CommentActive.allowSendComment(getContext());
                if (userId == -1) return;
                AppWebApi.sendTweetComment(tweetId, content, 0, sendResHandler);
            }

            @Override
            public void keyCodeDelete(EditText editText, String name) {
                if (clearReply) {
                    clearReply = false;
                    editText.setHint(R.string.comment_placeholder);
                    if (!StringUtils.isEmpty(name))
                        cArea.clearParam();//清除回传参数
                }
                if (StringUtils.isEmpty(editText.getText().toString()) && !editText.getHint().equals(getResources().getString(R.string.comment_placeholder)))
                    clearReply = true;
            }

            @Override
            public void share() {

            }

            @Override
            public void collection() {

            }
        });
    }

    @Override
    protected Type getType() {
        return new TypeToken<RequestInfo<RequestInfoPage<TweetComment>>>() {
        }.getType();
    }

    @Override
    protected void requestNetData(boolean reqFirst) {
        AppWebApi.getTweetCommentList(getCurrTweet().getId(), reqFirst ? getCurrReqInfoEntity().getFirstPageToken() : getCurrReqInfoEntity().getNextPageToken(), getRespHandler());
    }

    @Override
    protected ListViewAdapter<TweetComment> getListAdapter() {
        if (mAdapter == null)
            mAdapter = new TweetDetailCommentListAdapter(cArea);
        return mAdapter;
    }

    @Override
    protected String getChildCacheTag() {
        return cacheTag;//读取缓存的标记
    }

    @Override
    public void listView_onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean listView_onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TweetComment currTweet = mAdapter.getItem(position);
        int tweetAuthorId = (int) currTweet.getAuthor().getId();
        TweetItemCtrlDialog td = new TweetItemCtrlDialog(getContext(), AppCurrentUser.getInstance().getLoginUserId() == tweetAuthorId, currTweet.getContent(), currTweet.getId(), position, mAdapter);
        td.show();
        return true;
    }

    @Override
    public void onDestroy() {
        BrowRules.clear();
        mAdapter = null;
        cacheTag = null;
        cArea = null;
        input = null;
        sendResHandler = null;
        super.onDestroy();
    }
}