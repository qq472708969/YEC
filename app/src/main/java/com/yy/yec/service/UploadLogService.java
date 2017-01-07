package com.yy.yec.service;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yy.yec.dataexchange.remote.AppWebApi;
import com.yy.yec.global.App;
import com.yy.yec.global.AppVariableConstants;
import com.yy.yec.service.base.BaseService;
import com.yy.yec.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zzq on 2016/9/27.
 */
public class UploadLogService extends BaseService {
    @Override
    public void executeMainThreadTask() {
        File logDir = App.packgeDir(AppVariableConstants.logDir);
        final File log = new File(logDir, AppVariableConstants.appLog);
        String data = null;
        try {
            FileInputStream inputStream = new FileInputStream(log);
            data = StringUtils.toConvertString(inputStream);//把日志文件转换成字符串 ，上传服务器
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(data)) {
            AppWebApi.uploadLog(data, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    log.delete();
                    executeFinish();
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                    executeFinish();
                }
            });
        } else
            executeFinish();
    }
}
