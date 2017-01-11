package com.yy.yec.global;

import com.yy.yec.global.able.PropertyStorage;

/**
 * 应用程序配置信息保存到Properties中
 *
 * @author zzq
 * @created 2016年9月25日
 */
public class AppConfig extends PropertyStorage {
    private static AppConfig _this;
    private String config = "config";

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        if (_this == null)
            synchronized (AppConfig.class) {
                if (_this == null)
                    _this = new AppConfig();
            }
        return _this;
    }

    @Override
    protected String setDir() {
        return config;//为android创建真是文件夹名称app_config
    }

    @Override
    protected String setFileName() {
        return config;//存储的文件名
    }
}