package com.yy.yec.global;

import android.support.v4.util.ArrayMap;

import com.yy.yec.entity.xml.User;
import com.yy.yec.utils.CyptoUtils;
import com.yy.yec.utils.StringUtils;

import java.util.Map;

/**
 * Created by zzq on 2016/9/25.
 */
public class AppCurrentUser {
    public static final String desTag = "_Y_E_C___";
    private int loginUid;
    public static final String mcAppReqCookie = "cookie";
    private static AppCurrentUser _this;

    private AppCurrentUser() {
    }

    public static AppCurrentUser getInstance() {
        if (_this == null)
            synchronized (AppCurrentUser.class) {
                _this = new AppCurrentUser();
            }
        return _this;
    }

    /**
     * 初始化用户登录
     */
    public void userLogin() {
        loginUid = getLoginUserId();//尽量少的properties操作，证明登录信息存在
        if (loginUid > 0)
            AppHttpOperate.getInstance().setCookie(getUserCookie());//设置请求时需要的cookie
        else
            cleanLoginInfo();
    }

    /**
     * 用户注销
     */
    public void userLogout() {
        cleanLoginInfo();
        AppHttpOperate.getInstance().setCookie("");//清空请求时需要的cookie， 如果clear不好使的话就置为空
        AppConfig.getInstance().removeProperty(mcAppReqCookie);
        //  Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        //  sendBroadcast(intent);
    }

    /**
     * （保存登录信息）（更新个人信息）
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user, final boolean updateOK) {
        Map<String, String> kv = new ArrayMap<>();
        if (!updateOK) {
            loginUid = user.getId();
            kv.put("user.uid", String.valueOf(user.getId()));
            kv.put("user.account", user.getAccount());
            kv.put("user.location", user.getLocation());
            kv.put("user.pwd", CyptoUtils.encode(desTag, user.getPwd()));
            kv.put("user.isRememberMe", String.valueOf(user.isRememberMe()));// 是否记住我的信息
        }
        kv.put("user.name", user.getName());
        kv.put("user.face", user.getPortrait());// 用户头像-文件名
        kv.put("user.followers", String.valueOf(user.getFollowers()));
        kv.put("user.fans", String.valueOf(user.getFans()));
        kv.put("user.score", String.valueOf(user.getScore()));
        kv.put("user.favoritecount", String.valueOf(user.getFavoritecount()));
        kv.put("user.gender", String.valueOf(user.getGender()));
        AppConfig.getInstance().setProperties(kv);
    }

    public int getLoginUserId() {
        return StringUtils.toInt(getConfigProperty("user.uid"), -1);
    }

    public String getLoginAccount() {
        return getConfigProperty("user.account");
    }

    public String getLoginPwd() {
        return getConfigProperty("user.pwd");
    }

    public String getUserCookie() {
        return getConfigProperty(mcAppReqCookie);
    }

    public void setUserCookie(String cookie) {
        AppConfig.getInstance().setProperty(mcAppReqCookie, cookie);
        AppHttpOperate.getInstance().setCookie(cookie);//设置到请求对象中去
    }

    public boolean getLoginOK() {
        return loginUid > 0;
    }

    public void cleanLoginInfo() {
        loginUid = 0;
        AppConfig.getInstance().removeProperties("user.uid", "user.name", "user.face", "user.location", "user.followers", "user.fans", "user.score",
                "user.isRememberMe", "user.gender", "user.favoritecount");
    }

    /**
     * AppConfig的存储信息读取（K，V）
     *
     * @param key
     * @return
     */
    private String getConfigProperty(String key) {
        return AppConfig.getInstance().getProperty(key);
    }
}