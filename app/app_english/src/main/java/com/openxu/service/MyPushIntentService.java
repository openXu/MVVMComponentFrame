package com.openxu.service;

import org.android.agoo.client.BaseConstants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.openxu.utils.MyUtil;
import com.umeng.message.UmengBaseIntentService;

public class MyPushIntentService extends UmengBaseIntentService {

	private static final String TAG = MyPushIntentService.class.getName();

	// 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。
	@Override
	protected void onMessage(Context context, Intent intent) {
		// 需要调用父类的函数，否则无法统计到消息送达
		super.onMessage(context, intent);
		try {
			// 可以通过MESSAGE_BODY取得消息体
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			MyUtil.LOG_V(TAG, "收到消息推送："+message);
			//收到消息推送：
			//{"msg_id":"us91780144524711006510","display_type":"notification","alias":"","random_min":0,
			//"body":{"title":"我是标题","ticker":"我是标题","text":"阿斯顿法师打发士大夫","after_open":"go_app","play_vibrate":"true","play_sound":"true","play_lights":"true"}}

			my_nofity(message);
			// 完全自定义消息的处理方式，点击或者忽略
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void my_nofity(String message){}
	
}
