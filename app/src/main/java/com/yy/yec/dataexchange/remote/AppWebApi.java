package com.yy.yec.dataexchange.remote;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.entity.common.RequestType;
import com.yy.yec.global.AppHttpOperate;

/**
 * Created by zzq on 2016/8/27.
 */
public class AppWebApi {
    /**
     * BUG上传
     *
     * @param data
     * @param handler
     */
    public static void uploadLog(String data, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "1");
        params.put("report", "1");
        params.put("msg", data);
        AppHttpOperate.getInstance().post("action/api/user_report_to_admin", params, handler);
    }

    /**
     * 获取个人信息（指当前登录人，还有一个是提供id来获取一个人的信息***但是为部分公开信息）
     */
    public static void getUserInfo(TextHttpResponseHandler handler) {
        AppHttpOperate.getInstance().get("action/apiv2/user_info", handler);
    }

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @param handler
     */
    public static void login(String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("pwd", password);
        params.put("keep_login", 1);
        AppHttpOperate.getInstance().post("action/api/login_validate", params, handler);
    }

    /**
     * 请求新闻列表
     * 求上下页数据令牌
     *
     * @param handler   AsyncHttpResponseHandl
     * @param pageToken 请er
     */
    public static void getNewsList(String pageToken, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }
        AppHttpOperate.getInstance().get("action/apiv2/news", params, handler);
    }

    /**
     * 请求动弹列表
     *
     * @param type      type类型，1: 最新、2: 热门
     * @param pageToken pageToken
     * @param handler   回调
     */
    public static void getTweetList(int type, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("type", type);
        if (!TextUtils.isEmpty(pageToken))
            params.put("pageToken", pageToken);
        AppHttpOperate.getInstance().get("action/apiv2/tweets", params, handler);
    }

    /**
     * 请求动弹评论列表
     *
     * @param sourceId 动弹id
     * @param handler  回调
     */
    public static void getTweetCommentList(long sourceId, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        if (!TextUtils.isEmpty(pageToken))
            params.put("pageToken", pageToken);
        AppHttpOperate.getInstance().get("action/apiv2/tweet_comments", params, handler);
    }

    /**
     * 请求动弹点赞列表
     *
     * @param sourceId 动弹id
     * @param handler  回调
     */

    public static void getTweetLikeList(long sourceId, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        if (!TextUtils.isEmpty(pageToken))
            params.put("pageToken", pageToken);
        AppHttpOperate.getInstance().get("action/apiv2/tweet_likes", params, handler);
    }

    /**
     * 发表动弹评论列表
     *
     * @param sourceId 动弹id
     * @param content  内容
     * @param replyId  回复的用户id
     * @param handler  回调
     */
    public static void sendTweetComment(long sourceId, String content, long replyId, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("content", content);
        if (replyId > 0) params.put("replyId", replyId);
        AppHttpOperate.getInstance().post("action/apiv2/tweet_comment", params, handler);
    }

    /**
     * 更改动弹点赞状态
     *
     * @param sourceId 动弹id
     * @param handler  回调
     */
    public static void reverseTweetLike(long sourceId, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        AppHttpOperate.getInstance().get("action/apiv2/tweet_like_reverse", params, handler);
    }

    /**
     * 删除动弹
     *
     * @param sourceId 动弹id
     * @param handler  回调
     */
    public static void deleteTweet(long sourceId, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        AppHttpOperate.getInstance().get("action/apiv2/tweet_delete", params, handler);
    }

    /**
     * 请求资讯详情
     *
     * @param id      请求该资讯详情页
     * @param handler AsyncHttpResponseHandler
     */
    public static void getNewsDetail(long id, String type, AsyncHttpResponseHandler handler) {
        if (id <= 0) return;
        RequestParams params = new RequestParams();
        params.put("id", id);
        AppHttpOperate.getInstance().get("action/apiv2/" + type, params, handler);
    }

    /**
     * 评论提交api
     *
     * @param articleId 文章id
     * @param referId   引用的评论的id
     * @param replyId   回复的评论的id
     * @param oid       引用、回复的发布者id
     * @param type      文章类型 1:软件推荐, 2:问答帖子, 3:博客, 4:翻译文章, 5:活动, 6:资讯
     * @param content   内容
     * @param handler   结果处理
     */
    public static void submitComment(long articleId, long referId, long replyId, long oid, int type, String content, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", articleId);
        params.put("type", type);
        params.put("content", content);
        if (referId > 0)
            params.put("referId", referId);
        if (replyId > 0)
            params.put("replyId", replyId);
        if (oid > 0)
            params.put("reAuthorId", oid);
        AppHttpOperate.getInstance().post("action/apiv2/comment_pub", params, handler);
    }

    /**
     * 发表某一评论种类
     */
    public static void submitTypeComment(long articleId, long commentId, long commentAuthorId, String comment, int type, TextHttpResponseHandler handler) {
        if (commentId <= 0 || commentId == articleId) {
            commentId = 0;
            commentAuthorId = 0;
        }
        switch (type) {
            case RequestType.Type_HREF:
                //新闻链接

                break;
            case RequestType.Type_SOFTWARE:
                //软件推荐

                break;
            case RequestType.Type_QUESTION:
                //问答

                break;
            case RequestType.Type_BLOG:
                //博客

                break;
            case RequestType.Type_TRNSLATE:
                //4.翻译

                break;
            case RequestType.Type_EVENT:
                //活动

                break;
            case RequestType.Type_NEWS:
                //6.资讯
                submitComment(articleId, 0, commentId, commentAuthorId, RequestType.Type_NEWS, comment, handler);
                break;
        }
    }

    /**
     * 请求评论列表
     *
     * @param articleId 目标Id，该sourceId为资讯、博客、问答等文章的Id
     * @param type      类型
     * @param parts     请求的数据节点 parts="refer,reply"
     * @param pageToken 请求上下页数据令牌
     * @param handler   AsyncHttpResponseHandler
     */
    public static void getListComments(long articleId, int type, String parts, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", articleId);
        params.put("type", type);
        if (!TextUtils.isEmpty(parts)) {
            params.put("parts", parts);
        }
        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }
        AppHttpOperate.getInstance().get("action/apiv2/comment", params, handler);
    }
}
