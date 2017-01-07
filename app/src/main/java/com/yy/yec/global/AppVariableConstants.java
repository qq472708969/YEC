package com.yy.yec.global;

/**
 * Created by zzq on 2016/9/24.
 */
public interface AppVariableConstants {
    String UpdateTitleLayout = "YEC_UpdateTitle_BROADCAST";
    String UpdateLikeList = "YEC_UpdateLikeList_BROADCAST";

    String INTENT_ACTION_USER_CHANGE = "net.oschina.action.USER_CHANGE";

    String INTENT_ACTION_COMMENT_CHANGED = "net.oschina.action.COMMENT_CHANGED";

    String INTENT_ACTION_NOTICE = "net.oschina.action.APPWIDGET_UPDATE";

    String INTENT_ACTION_LOGOUT = "net.oschina.action.LOGOUT";

    String WEICHAT_APPID = "wxa8213dc827399101";
    String WEICHAT_SECRET = "5c716417ce72ff69d8cf0c43572c9284";

    String QQ_APPID = "100942993";
    String QQ_APPKEY = "8edd3cc7ca8dcc15082d6fe75969601b";

    //http
    String Host = "www.oschina.net";
    String HostUrl = "http://www.oschina.net/%s";//%s字符串类变量

    //缓存图片文件夹路径
    String imageCacheDir = "imageCacheDir";
    //Log日志文件路径和文件
    String logDir = "logDir";
    String appLog = "app_log.log";

    //Fragment的Bundle标记
    String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";//key
    int CATALOG_ALL = 1;//号外
    int CATALOG_WEEK = 4;//博客
    String CATALOG_LATEST = "latest";//问答
    String CATALOG_RECOMMEND = "recommend";//活动
    String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";
}
