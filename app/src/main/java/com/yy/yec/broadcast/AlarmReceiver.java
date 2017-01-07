package com.yy.yec.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yy.yec.utils.TLog;


public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TLog.info("onReceive ->net.oschina.app收到定时获取消息");
		//NoticeUtils.requestNotice(context);
	}
}
