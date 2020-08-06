package com.openxu.ui;

import java.util.HashMap;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobMsg;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.my.ShareModel;
import cn.sharesdk.my.SharePopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author openXu
 */
public class ActivityApp extends BaseActivity implements OnClickListener, PlatformActionListener {


	private TextView tv_version, tv_new_version;
	private RelativeLayout rl_new, rl_fk, rl_help , rl_share;
	private ImageView iv_new_tips, iv_recent_tips;
	
	private int myVersion;
	
    private SharePopupWindow share;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_app);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_new_version = (TextView) findViewById(R.id.tv_new_version);
		
		iv_new_tips = (ImageView) findViewById(R.id.iv_new_tips);
		iv_new_tips.setVisibility(View.GONE);
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		
		rl_new = (RelativeLayout) findViewById(R.id.rl_new);
		rl_fk = (RelativeLayout) findViewById(R.id.rl_fk);
		rl_help = (RelativeLayout) findViewById(R.id.rl_help);
		rl_share = (RelativeLayout) findViewById(R.id.rl_share);
		rl_new.setOnClickListener(this);
		rl_fk.setOnClickListener(this);
		rl_help.setOnClickListener(this);
		rl_share.setOnClickListener(this);
		
	}
	
	@Override
	protected void setPf() {
		super.setPf();
		rl_new.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_fk.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_help.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_share.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
	}

	@Override
	public void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		//清空
		MyMessageReceiver.mNewNum=0;
		if(MyApplication.property.openXuNum!=0){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	@Override
	protected void initData() {
		try {
			PackageManager pm = getPackageManager();  
			 PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			String versionName = pi.versionName;  
			myVersion = pi.versionCode;
	        MyUtil.LOG_V(TAG, "应用版本名称："+versionName+"   版本号："+myVersion);
	        tv_version.setText("v "+versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
		
		MyUtil.LOG_D(TAG, "获取最新版本："+Constant.URL_GET_NEW_VERSION);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,Constant.URL_GET_NEW_VERSION,
			    new RequestCallBack<String>(){
			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			        }
			        @Override
			        public void onSuccess(ResponseInfo<String> responseInfo) {
			        	try{
			        		MyUtil.LOG_V(TAG, "获取最新版本：:"+responseInfo.result);
			        		//{"results":[{"createdAt":"2015-10-22 13:16:09","objectId":"N9sU777C","updatedAt":"2015-10-22 13:16:09","version":"2"}]}
			        		JSONObject json = JSONObject.parseObject(responseInfo.result);
			        		String result = (String) json.getString("results");
			        		if(!TextUtils.isEmpty(result)){
			        			if(result.startsWith("[")){
			        				result = result.substring(1, result.length()-1);
			        			}
			        		}
			        		json = JSONObject.parseObject(result);
			        		String newVersionName = json.getString("versionName");
			        		String newVersion = json.getString("version");
			        		MyUtil.LOG_V(TAG, "最新版本名："+newVersionName+"  版本号："+newVersion);
				        	tv_new_version.setText("v "+newVersionName);
				        	if(!TextUtils.isEmpty(newVersion)){
				        		int newV = Integer.parseInt(newVersion);
				        		if(newV>myVersion){
					        		iv_new_tips.setVisibility(View.VISIBLE);
				        		}else{
				        			tv_new_version.setText("已是最新版");
				        			iv_new_tips.setVisibility(View.GONE);
				        		}
				        	}
				        	return;
			        	}catch(Exception e){
			        		e.printStackTrace();
			        	}
			        }
			        @Override
			        public void onStart() {
			        }
			        @Override
			        public void onFailure(HttpException error, String msg) {
			        	MyUtil.LOG_E(TAG, "获取最新版本失败："+msg);
			        }
			});
        
	}
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_new:
			/**
			 * 许多应用的设置界面中都会有检查更新等类似功能，需要用户主动触发而检测更新。
			 * 它的默认行为基本和自动更新基本一致。
			 * 它和自动更新的主要区别是：在这种手动更新的情况下，无论网络状况是否Wifi，
			 * 无论用户是否忽略过该版本的更新，都可以像下面的示例一样在按钮的回调中发起更新检查，
			 * 代替update(Context context)：
			 */
			UmengUpdateAgent.forceUpdate(mContext);
			break;
		case R.id.rl_fk:
			if(MyApplication.user==null){
				MyUtil.showToast(mContext, -1, "请先登录");
			}else{
				String name = MyApplication.user.getUsername();
				if(!TextUtils.isEmpty(name)&&name.equalsIgnoreCase(Constant.openName)){
					//开发者
					intent = new Intent(mContext, ChatActivityRecent.class);
					intent.putExtra("action", 2);
					startActivity(intent);
				}else{
					intent = new Intent(mContext, ChatActivityToDevelop.class);
					startActivity(intent);
				}
			}
			break;
		case R.id.rl_help:
			startActivity(new Intent(mContext, ActivityAppHelp.class));
			break;
		case R.id.rl_share:
			if (!NetWorkUtil.isNetworkAvailable(mContext)) {
				MyUtil.showToast(mContext, R.string.no_net, "");
				return;
			}
			share = new SharePopupWindow(mContext, Platform.SHARE_WEBPAGE);
            share.setPlatformActionListener(ActivityApp.this);
            ShareModel model = new ShareModel();
            model.setTitle(mContext.getResources().getString(R.string.app_name));
            model.setText("我正在使用好记单词，一款记单词应用，感觉棒棒哒~\r\n"+"快来一起记单词吧"+Constant.downLoadUrl);
            model.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
            model.setUrl(Constant.downLoadUrl);   //万普
            share.setShareModel(model);
            share.showShareWindow();
            // 显示窗口 (设置layout在PopupWindow中显示的位置)
            share.showAtLocation(ActivityApp.this.findViewById(R.id.rootView),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		super.onMessage(message);
		MyUtil.LOG_V(TAG, "关于好记单词收到消息："+message);
		// 小圆点提示
		String uid = message.getBelongId();
		if (uid.equals(Constant.openID)){// 如果是开发者openXu
			MyApplication.property.setOpneXuNum(1);
		}
		if(MyApplication.property.openXuNum!=0){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		
	}
	
}
