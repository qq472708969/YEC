package com.yy.yec.ui.manager;

import com.yy.yec.global.able.PropertyStorage;

/**
 * Created by zzq on 2016/10/21.
 */
public class HaveReadList extends PropertyStorage {
    private String file;
    public final static String haveRead = "1";//已经阅读过的条目为1

    @Override
    protected String setDir() {
        return "haveReadList";//为android创建真实文件夹名称app_readedList
    }

    private static HaveReadList _this;

    private HaveReadList() {
    }

    public static HaveReadList getInstance() {
        if (_this == null)
            synchronized (HaveReadList.class) {
                _this = new HaveReadList();
            }
        return _this;
    }

    @Override
    protected String setFileName() {
        return file;
    }

    public String getProperty(String fileName, String key) {
        file = fileName;
        String val = super.getProperty(key);
        return val == null ? "" : val;
    }

    public void setProperty(String fileName, String key) {
        file = fileName;
        super.setProperty(key, haveRead);
    }

    public void removeProperty(String fileName, String key) {
        file = fileName;
        super.removeProperty(key);
    }
}