package com.openxu.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.utils.CollectionUtils;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.MyUtil;
import com.openxu.view.PrograsDialog;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.SystemBarTintManager.SystemBarConfig;
import com.openxu.view.TitleView;
import com.openxu.view.slidingfinish.SildingFinishLayout;
import com.openxu.view.slidingfinish.SildingFinishLayout.OnSildingFinishListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public abstract class BaseActivity extends Activity implements OnClickListener, EventListener{
	protected Context mContext;
	protected TitleView titleView;
	protected PrograsDialog dialog;
	protected String TAG;
	protected SystemBarTintManager tintManager;
	protected View rootView;
	
	BmobUserManager userManager;
	BmobChatManager manager;
	MyApplication mApplication;
	
	/**
	 * 奖励经验悬浮窗体
	 */
	private PopupWindow popup_jy;
	private View view_jy;
	private TextView tv_msg, tv_jy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = getClass().getSimpleName();
		MyUtil.LOG_I(TAG, "开启"+TAG+"界面");
		mContext = this;
		MyApplication.getApplication().addActivity(this);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = MyApplication.getApplication();
		dialog = new PrograsDialog(mContext);
		//开启透明标题栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
    		tintManager.setStatusBarTintEnabled(true);
    		// 使用颜色资源
//    		tintManager.setStatusBarTintResource(MyApplication.getApplication().pf.title_bg);
		}
		
		initView();
		initTitleView();
		rootView = findViewById(R.id.rootView);
		//将title上面流出提示栏的高度
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if(rootView!=null){
				SystemBarConfig config = tintManager.getConfig();
				LayoutParams ll = (LayoutParams) titleView.ll_top.getLayoutParams();
		        ll.height = config.getStatusBarHeight();
		        titleView.ll_top.setLayoutParams(ll);
//				titleView.setPadding(0, config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
			}
		}
		
		View view = findViewById(R.id.rootView);
		if(view instanceof SildingFinishLayout){
			SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) view;
			mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
				@Override
				public void onSildingFinish() {
					boolean reuslt = onBackSelected();
					MyUtil.LOG_V(TAG, "已经花过头了。要finish了"+reuslt);
					if(!reuslt)
						finish();
				}
			});
		}
		
		initJyPop();
		
		setPf();
		initData();
		//如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
		PushAgent.getInstance(mContext).onAppStart();
		
	}
	
	private void initJyPop() {
		view_jy = View.inflate(this,R.layout.popup_jy, null);
		tv_msg = (TextView) view_jy.findViewById(R.id.tv_msg);
		tv_jy = (TextView) view_jy.findViewById(R.id.tv_jy);
		// 构造一个悬浮窗体：显示的内容，窗体的宽高
		popup_jy = new PopupWindow(view_jy,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// ☆ 注意： 必须要设置背景，才能让悬浮窗体播放动画
		popup_jy.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    return super.onTouchEvent(event);
	}
	protected void initTitleView() {
		titleView = (TitleView)findViewById(R.id.titleView);
		titleView.setListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_title_back:
					if(!onBackSelected())
						finish();
					break;
				case R.id.ll_title_menu:
					onMenuSelected();
					break;
				default:
					break;
				}
			}
		});
		
	}
	protected abstract void initView();
	protected void setPf(){
		//设置title
		titleView.setTitleBackground(MyApplication.getApplication().pf.title_bg);
		titleView.setTitleMenuItemBack(MyApplication.getApplication().pf.title_item_selecter);
		titleView.setTitleTextColorResources(MyApplication.getApplication().pf.text_color);
	}
	//标题按钮按下事件
	protected boolean onBackSelected(){
		return false;
	}
	protected boolean onMenuSelected(){
		return false;
	}
	protected abstract void initData();
	
	@Override
	public void onClick(View v) {
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
//	    MyMessageReceiver.ehList.add(this);// 监听推送的消息
//		//清空
//		MyMessageReceiver.mNewNum=0;
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	    
//	    MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	
	@Override
	protected void onDestroy() {
		MyApplication.getApplication().removeActivity(this);
		if(popup_jy!=null){
			popup_jy.dismiss();
			popup_jy= null;
		}
		if(dialog!=null)
			dialog.cancel();
		super.onDestroy();
	}
	
	
	/** 隐藏软键盘
	  * hideSoftInputView
	  * @Title: hideSoftInputView
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	@Override
	public void onAddUser(BmobInvitation arg0) {
		//刷新好友请求
		if(MyApplication.property.voice){
			MyApplication.getApplication().getMediaPlayer().start();
		}
	}

	@Override
	public void onMessage(BmobMsg message) {
		//刷新好友请求
		if(MyApplication.property.voice){
			MyApplication.getApplication().getMediaPlayer().start();
		}
		//也要存储起来
		if(message!=null){
			BmobChatManager.getInstance(BaseActivity.this).saveReceiveMessage(true,message);
		}
	}

	@Override
	public void onNetChange(boolean arg0) {
	}

	@Override
	public void onOffline() {
	}

	@Override
	public void onReaded(String arg0, String arg1) {
	}
	
	/** 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	  * @Title: updateUserInfos
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserInfos(){
		//更新地理位置信息
		updateUserLocation();
		//查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		//这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		MyUtil.LOG_V(TAG, "获取好友列表");
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
					@Override
					public void onError(int arg0, String arg1) {
						if(arg0==BmobConfig.CODE_COMMON_NONE){
							MyUtil.LOG_E(TAG, arg1);
						}else{
							MyUtil.LOG_E(TAG, "查询好友列表失败："+arg1);
						}
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// 保存到application中方便比较
						MyApplication.getApplication().setContactList(CollectionUtils.list2map(arg0));
					}
				});
	}
	
	/** 更新用户的经纬度信息
	  * @Title: uploadLocation
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserLocation(){
		if(MyApplication.lastPoint!=null){
			String saveLatitude  = MyApplication.getApplication().getLatitude();
			String saveLongtitude =  MyApplication.getApplication().getLongtitude();
			String newLat = String.valueOf(MyApplication.lastPoint.getLatitude());
			String newLong = String.valueOf(MyApplication.lastPoint.getLongitude());
			MyUtil.LOG_V(TAG, "更新用户地坐标位置"+newLat+"  "+newLong);
//			ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
//			ShowLog("newLat ="+newLat+",newLong = "+newLong);
			if(!saveLatitude.equals(newLat)|| !saveLongtitude.equals(newLong)){//只有位置有变化就更新当前位置，达到实时更新的目的
				ChatUser u = (ChatUser) userManager.getCurrentUser(ChatUser.class);
				final ChatUser user = new ChatUser();
				user.setLocation(MyApplication.lastPoint);
				user.setObjectId(u.getObjectId());
				user.update(this,new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						MyApplication.getApplication().setLatitude(String.valueOf(user.getLocation().getLatitude()));
						MyApplication.getApplication().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
//						ShowLog("经纬度更新成功");
					}
					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
//						ShowLog("经纬度更新 失败:"+msg);
					}
				});
			}else{
//				ShowLog("用户位置未发生过变化");
			}
		}
	}
	
	
	public void showRewardJyPo(String msg, int jy){
		MyUtil.LOG_D(TAG, "显示奖励经验pop");
		try{
			tv_msg.setText(msg);
			tv_jy.setText("+"+jy);
			Display display = getWindowManager().getDefaultDisplay();
			//让悬浮窗体显示（显示的父窗体就是ListView；重心；显示的xy坐标）
			popup_jy.showAtLocation(rootView, Gravity.CENTER_HORIZONTAL+Gravity.CENTER_VERTICAL, 0, 0);
			//让悬浮窗体播放动画
			AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);    //透明度动画
			aa.setDuration(200);                                   //执行时间
			//缩放动画：前四个参数（xy缩放前后比例）后四个参数（缩放时的中心点）
			ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
			sa.setDuration(100);
			//让两个动画同时播放
			AnimationSet set = new AnimationSet(false);
			set.addAnimation(sa);
			set.addAnimation(aa);
			view_jy.startAnimation(set);
			//缩放动画：前四个参数（xy缩放前后比例）后四个参数（缩放时的中心点）
			ScaleAnimation jysa = new ScaleAnimation(0.5f, 2.0f, 0.5f, 2.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
			jysa.setDuration(1000);
			tv_jy.startAnimation(jysa);
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissPopJy();
							}
						});
				}
			}, 3000);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void dismissPopJy() {
		if (popup_jy != null && popup_jy.isShowing())
			popup_jy.dismiss();
	}
	
}
