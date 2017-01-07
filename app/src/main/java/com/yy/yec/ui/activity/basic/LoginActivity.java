package com.yy.yec.ui.activity.basic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yy.yec.R;
import com.yy.yec.global.manager.CacheManager;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.entity.common.RequestInfo;
import com.yy.yec.entity.detail.User;
import com.yy.yec.entity.xml.LoginUser;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.global.AppHttpOperate;
import com.yy.yec.ui.activity.detail.base.BaseBackActivity;
import com.yy.yec.ui.fragment.basic.MyInfoFragment;
import com.yy.yec.utils.CyptoUtils;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.JsonUtil;
import com.yy.yec.utils.StringUtils;
import com.yy.yec.utils.TLog;
import com.yy.yec.utils.ToastUtil;
import com.yy.yec.utils.UIUtil;
import com.yy.yec.utils.XmlUtils;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by zzq on 2016/10/6.
 */
public class LoginActivity extends BaseBackActivity {
    @Bind(R.id.et_username)
    protected EditText mEtUserName;
    @Bind(R.id.et_password)
    protected EditText mEtPassword;
    private String mUserName;
    private String mPassword;
    private ProgressDialog pdLogin;

    @OnClick({R.id.btn_login, R.id.iv_qq_login, R.id.iv_wx_login, R.id.iv_sina_login})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                handleLogin();
                break;
            case R.id.iv_qq_login:
                qqLogin();
                break;
            case R.id.iv_wx_login:
                wxLogin();
                break;
            case R.id.iv_sina_login:
                sinaLogin();
                break;
            default:
                break;
        }
    }

    private void qqLogin() {

    }

    private void wxLogin() {

    }

    private void sinaLogin() {

    }

    private void handleLogin() {
        if (prepareLogin()) return;
        mUserName = mEtUserName.getText().toString();
        mPassword = mEtPassword.getText().toString();

        pdLogin = UIUtil.getProgressDialog(this);
        pdLogin.setMessage(getResources().getString(R.string.progress_login));
        pdLogin.setCanceledOnTouchOutside(false);
        pdLogin.show();

        AppWebApi.login(mUserName, mPassword, mHandler);
    }

    private final AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            LoginUser loginUser = XmlUtils.toBean(LoginUser.class, responseBody);
            if (loginUser != null)
                processLogin(loginUser, headers);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    };

    private void processLogin(LoginUser loginUser, Header[] headers) {
        if (!loginUser.getResult().yes()) {//登陆失败
            AppCurrentUser.getInstance().cleanLoginInfo();
            ToastUtil.showToast(loginUser.getResult().getErrorMessage());
            if (pdLogin != null) pdLogin.hide();
            return;
        }

        CookieStore cookies = (CookieStore) AppHttpOperate.getInstance().getAsyncHttpClient().getHttpContext().getAttribute(ClientContext.COOKIE_STORE);
        if (cookies != null) {
            String tmpCookies = "";
            for (Cookie c : cookies.getCookies()) {
                TLog.info("cookie:" + c.getName() + " " + c.getValue());
                tmpCookies += (c.getName() + "=" + c.getValue()) + ";";
            }
            if (StringUtils.isEmpty(tmpCookies) && headers != null) {//如果发现请求对象中cookie为空需要给设置上，而且在响应头中获取
                for (Header header : headers) {
                    String key = header.getName();
                    String value = header.getValue();
                    if (key.contains("Set-Cookie"))
                        tmpCookies += value + ";";
                }
                if (tmpCookies.length() > 0) {
                    tmpCookies = tmpCookies.substring(0, tmpCookies.length() - 1);//去掉最后一个分号
                }
            }
            AppCurrentUser.getInstance().setUserCookie(tmpCookies);
        }
        //保存登录信息
        loginUser.getUser().setAccount(mUserName);
        loginUser.getUser().setPwd(mPassword);
        loginUser.getUser().setRememberMe(true);
        AppCurrentUser.getInstance().saveUserInfo(loginUser.getUser(), false);//保存用户登录信息，到中properties

        pdLogin.hide();//进度条隐藏
        processLoginSuccess();
    }

    private void processLoginSuccess() {
        UIUtil.hideSoftKeyboard(getWindow().getDecorView());//登陆成功后先隐藏键盘
        AppWebApi.getUserInfo(new TextHttpResponseHandler() {//保存用户的完整信息资料
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<RequestInfo<User>>() {
                    }.getType();
                    RequestInfo result = JsonUtil.createGson().fromJson(responseString, type);
                    if (result.isSuccess()) {
                        User userInfo = (User) result.getResult();
                        CacheManager.saveObjectData(userInfo, MyInfoFragment.loginUserInfo, CacheManager.app_global_cache);//缓存个人详细信息
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean prepareLogin() {
        if (!DeviceUtil.internet()) {
            ToastUtil.showToastShort(R.string.tip_no_internet);
            return true;
        }
        if (mEtUserName.length() == 0) {
            mEtUserName.setError(getResources().getString(R.string.input_userName));
            mEtUserName.requestFocus();
            return true;
        }
        if (mEtPassword.length() == 0) {
            mEtPassword.setError(getResources().getString(R.string.input_pwd));
            mEtPassword.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        setActionBarTitle(R.string.login_title);
        mEtUserName.setText(AppCurrentUser.getInstance().getLoginAccount());
        mEtPassword.setText(CyptoUtils.decode(AppCurrentUser.desTag, AppCurrentUser.getInstance().getLoginPwd()));
        mEtUserName.post(new Runnable() {//防止页面未刷新完毕调用requestFocus失效问题
            @Override
            public void run() {
                mEtUserName.requestFocus();
            }
        });
    }

    @Override
    protected void initWidget() {

    }

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onDestroy() {
        mUserName = null;
        mPassword = null;
        pdLogin = null;
        super.onDestroy();
    }
}