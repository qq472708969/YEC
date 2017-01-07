package com.yy.yec.global;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yy.yec.global.base.BaseApplication;
import com.yy.yec.utils.TLog;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.UUID;

/**
 * Created by zzq on 2016/9/24.
 */
public class App extends BaseApplication {
    public String mcAppId = "YEC";

    @Override
    protected void onCreateApp() {
        initAppTools();
        initUserLogin();
        // UIUtil.sendBroadcast(NoticeService.INTENT_ACTION_BROADCAST);
    }

    private void initUserLogin() {
        AppCurrentUser.getInstance().userLogin();//实例化当前用户操作类，并执行登陆
    }


    private void initAppTools() {
        TLog.DEBUG = true;

    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo packageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 应用appID
     *
     * @return
     */
    public String id() {
        String id = AppConfig.getInstance().getProperty(mcAppId);
        if (StringUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString();
            AppConfig.getInstance().setProperty(mcAppId, id);
        }
        return id;
    }

    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = BaseApplication.context().getPackageManager().getPackageInfo(BaseApplication.context().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }
}