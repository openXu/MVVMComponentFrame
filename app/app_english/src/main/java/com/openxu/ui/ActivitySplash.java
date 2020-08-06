package com.openxu.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.openxu.db.bean.User;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.service.MyPushIntentService;
import com.openxu.utils.Constant;
import com.openxu.utils.FileUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.UIUtils;
import com.openxu.utils.VoicePlayerImpl;
import com.openxu.view.CopyDBDialog;
import com.openxu.view.PrograsDialog;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.SystemBarTintManager.SystemBarConfig;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

/**
 * @author openXu
 */
public class ActivitySplash extends Activity{
	
	private String TAG = "ActivitySplash";
	private Context mContext;
	protected PrograsDialog dialog;
	
	// 定位获取当前用户的地理位置
	private LocationClient mLocationClient;
	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initPT();
		initData();
	}
	
	private void initPT() {
		// 初始化 Bmob SDK
		// 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		MyUtil.LOG_V(TAG, "初始化 Bmob SDK");
		Bmob.initialize(this, Constant.bmobAID);
		// 可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
		BmobChat.DEBUG_MODE = false;
		// BmobIM SDK初始化--只需要这一段代码即可完成初始化
		BmobChat.getInstance(this).init(Constant.bmobAID);
		//开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		//如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
//		BmobChat.getInstance(this).startPollService(10);
		loadUser();
		
		// 开启定位
		initLocClient();
		// 注册地图 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
		
		/**
		 * 友盟
		 */
		MyUtil.LOG_V(TAG, "初始化友盟");
		//友盟需要在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false) 禁止默认的页面统计方式，这样将不会再自动统计Activity。
		MobclickAgent.openActivityDurationTrack(false);
		//发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率。
		MobclickAgent.updateOnlineConfig(this);
		//** 设置是否对日志信息进行加密, 默认false(不加密). 
		AnalyticsConfig.enableEncrypt(true);
		//测试设备
		if(Constant.isDebug){
			MobclickAgent.setDebugMode( Constant.isDebug );
			MyUtil.LOG_E(TAG, MyUtil.getDeviceInfo(this));
		}
		
		// 推送
		PushAgent mPushAgent = PushAgent.getInstance(mContext);
		mPushAgent.enable();
		PushAgent.getInstance(mContext).onAppStart();

		mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
		String device_token = UmengRegistrar.getRegistrationId(mContext);
		MyUtil.LOG_E(TAG, "消息推送测试设备：" + device_token);
		
		MyUtil.LOG_E(TAG, "初始化讯飞语音");
		MyApplication.getApplication().player = new VoicePlayerImpl(this);		
		
	}
	private void loadUser() {
		MyUtil.LOG_V(TAG, "用户信息：" + MyApplication.user);
		if (MyApplication.user == null) {
			UserDaoImpl dao = new UserDaoImpl();
			List<User> users = dao.findAll();
			MyUtil.LOG_V(TAG, "数据库查找用户：" + users.size());
		}
		if(MyApplication.user==null)
			return;
		MyApplication.user.login(this, new SaveListener() {
		    @Override
		    public void onSuccess() {
		    	MyUtil.LOG_V(TAG, "登录成功" );
		    	// 将设备与username进行绑定
				BmobUserManager.getInstance(mContext).bindInstallationForRegister(MyApplication.user.getChatName());
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		    	MyApplication.user = null;
		    	MyUtil.showToast(mContext, -1, "登录失败:"+msg);
		    }
		});
	}
	protected void initView() {
		mContext = this;
		MyApplication.getApplication().addActivity(this);
		dialog = new PrograsDialog(mContext);
		SystemBarTintManager tintManager = null;
		//开启透明标题栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
    		tintManager.setStatusBarTintEnabled(true);
    		// 使用颜色资源
    		tintManager.setStatusBarTintResource(MyApplication.pf.login_ztn);
		}
		
		setContentView(R.layout.activity_splash);
		TAG = "ActivityLogin";
		//开启透明标题栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			View rootView = findViewById(R.id.rootView);
			if(rootView!=null){
				SystemBarConfig config = tintManager.getConfig();
				rootView.setPadding(0, config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
			}
		}
		
		findViewById(R.id.rootView).setBackgroundResource(MyApplication.pf.login_bg);
		
	}
	
	private int ASSETS_SUFFIX_BEGIN = 1;
	private int ASSETS_SUFFIX_END = 12;
	private String ASSETS_NAME = "openword_db.0";
	private void initData() {
		if (!MyApplication.property.dbCopy) {
			MyUtil.LOG_V(TAG, "拷贝数据库");
			final CopyDBDialog updialog = new CopyDBDialog(mContext);
			updialog.setShowText("正在初始化单词文件...");
			updialog.setUploadingText(0 + "%");
			updialog.show();
			new AsyncTask<Void, String, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					String destPath = "/data/data/" + mContext.getPackageName()+ "/databases";
					File dir = new File(destPath);
					if (!dir.exists())
						dir.mkdir();
					AssetManager am = mContext.getResources().getAssets();
					int conut = 0;
					for (String db : Constant.dbs) {
						try {
							int all = 10619880;
							FileOutputStream out = new FileOutputStream(destPath + File.separator + db);
							for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
								MyUtil.LOG_D(TAG, "复制第"+i+"部分数据库");
								String name = ASSETS_NAME + (i<10?("0"+i):i);
								InputStream in = am.open(name);
								byte[] buffer = new byte[1024];
								int length;
								while ((length = in.read(buffer)) > 0) {
									out.write(buffer, 0, length);
									conut += 1024;
									publishProgress(MyUtil.getFloatStr(conut, all));
								}
								// 最后关闭就可以了
								out.flush();
								in.close();
							}
							out.close();
						} catch (Exception e) {
							e.printStackTrace();
							FileUtils.delAllFile(destPath);
							return false;
						}
					}
					return true;
				}

				protected void onProgressUpdate(String... values) {
					updialog.setUploadingText(values[0]);
				};

				protected void onPostExecute(Boolean result) {
					updialog.dismiss();
					if (result) {
						MyApplication.property.setDbCopy(true);
						startActivity(new Intent(mContext, MainActivity.class));
						finish();
					} else
						UIUtils.showToast("初始化失败!!");
				};
			}.execute();
		}else{
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					startActivity(new Intent(mContext, MainActivity.class));
					finish();
				}
			}, 2000);
		}
	}
	
	/**
	 * 开启定位，更新当前用户的经纬度坐标
	 * @Title: initLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initLocClient() {
		MyUtil.LOG_V(TAG, "开启百度定位");
		mLocationClient = MyApplication.getApplication().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
		option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
		option.setIsNeedAddress(false);// 不需要包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				MyUtil.LOG_E(TAG, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				MyUtil.LOG_E(TAG,"当前网络连接不稳定，请检查您的网络设置!");
			}
		}
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		MyApplication.getApplication().removeActivity(this);
		
		// 退出时销毁定位
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		unregisterReceiver(mReceiver);
		
		super.onDestroy();
	}
	
}
