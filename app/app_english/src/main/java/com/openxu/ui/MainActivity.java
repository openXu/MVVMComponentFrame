package com.openxu.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.ChatUser;
import com.openxu.db.bean.User;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.utils.BookUtils;
import com.openxu.utils.CollectionUtils;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.Property;
import com.openxu.view.ChangePfDialog;
import com.openxu.view.ChangePfDialog.PfListener;
import com.openxu.view.DragLayout;
import com.openxu.view.DragLayout.DragListener;
import com.openxu.view.DrawerArrowDrawable;
import com.openxu.view.MainTitleView;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.SystemBarTintManager.SystemBarConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author openXu
 */
public class MainActivity extends FragmentActivity implements OnClickListener,
		EventListener ,UpdatePointsNotifier{

	private final String TAG = "MainActivity";
	private Context mContext;
	public MainTitleView titleView;
	private DrawerArrowDrawable drawerArrow;
	private DragLayout dl;
	// 登录
	private RelativeLayout rl_login;
	private ImageView iv_icon;
	private TextView tv_name;
	// 侧滑菜单
	private RelativeLayout rl_bdc;
	private LinearLayout ll_book, ll_dcb, ll_set, ll_recent, ll_hf;
	private TextView tv_lable_bdc;
	private ImageView iv_set_tips, iv_recent_tips;
	
	private LinearLayout ll_bottom;
	private TextView tv_menu_faxian, tv_menu_jifen;

	private FragmentManager fragmentMng;
	private ViewPager viewPager;
	private FragmentCidian cidianFragment;
	private FragmentFanyi fanyiFragment;
	private FragmentChaxun chaxunFragment;
	protected SystemBarTintManager tintManager;
	
	BmobUserManager userManager;
	BmobChatManager manager;
	
	
	/**
	 * 奖励经验悬浮窗体
	 */
	private View rootView;
	private PopupWindow popup_jy;
	private View view_jy;
	private TextView tv_msg, tv_jy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// 未知异常捕获
		// MyUncaughtExceptionHandler crashHandler =
		// MyUncaughtExceptionHandler.getInstance();
		// // 注册crashHandler
		// crashHandler.init(this);
		MyUtil.LOG_I(TAG, "主界面创建了");
		//开启透明标题栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
    		tintManager.setStatusBarTintEnabled(true);
    		// 使用颜色资源
//		    		tintManager.setStatusBarTintResource(MyApplication.getApplication().pf.title_bg);
		}
		MyApplication.getApplication().addActivity(this);
		initView();
		initTitleView();
		//将title上面流出提示栏的高度
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarConfig config = tintManager.getConfig();
			LayoutParams ll = (LayoutParams) titleView.ll_top.getLayoutParams();
	        ll.height = config.getStatusBarHeight();
	        titleView.ll_top.setLayoutParams(ll);
//					titleView.setPadding(0, config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
		}
		initJyPop();
		setPf();

		initData();
		
		// （万普）初始化统计器，并通过代码设置APP_ID, APP_PID
		MyUtil.LOG_V(TAG, "（万普）初始化统计器");
		AppConnect.getInstance(this);
		// 预加载自定义广告内容（仅在使用了自定义广告、抽屉广告或迷你广告的情况，才需要添加）
		AppConnect.getInstance(this).initAdInfo();
		// 初始化卸载广告
		AppConnect.getInstance(this).initUninstallAd(this);
		try{
			String showAd = AppConnect.getInstance(mContext).getConfig(Constant.SHOWAD_CHANCLE, "false");          
			MyApplication.showAd = Boolean.parseBoolean(showAd);
			Log.e(TAG, Constant.SHOWAD_CHANCLE+"获取在线广告配置参数"+MyApplication.showAd);
			if(!MyApplication.showAd){   //万普服务器配置不现实广告
				ll_bottom.setVisibility(View.GONE);
			}else{
				ll_bottom.setVisibility(View.VISIBLE);
			}
		}catch(Exception e){}
		
		// 如果处于wifi环境则检测更新，如果有更新，弹出对话框提示有新版本，用户点选更新开始下载更新
//		UmengUpdateAgent.update(this);
		// 当用户进入应用首页后如果处于wifi环境检测更新，如果有更新，后台下载新版本，如果下载成功，则进行通知栏展示，用户点击通知栏开始安装。
		// 静默下载中途如果wifi断开，则会停止下载。
		UmengUpdateAgent.silentUpdate(this);
		
		IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver=new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
	}
	
	private ConnectionChangeReceiver myReceiver;
	class ConnectionChangeReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
	            //改变背景或者 处理网络的全局变量
	        }else {
	        	MyUtil.LOG_V(TAG, "网络状态改变了，加广告");
	        	AppConnect.getInstance(mContext);
	    		// 预加载自定义广告内容（仅在使用了自定义广告、抽屉广告或迷你广告的情况，才需要添加）
	    		AppConnect.getInstance(mContext).initAdInfo();
	    		// 初始化卸载广告
	    		AppConnect.getInstance(mContext).initUninstallAd(mContext);
	        	try{
	    			String showAd = AppConnect.getInstance(mContext).getConfig(Constant.SHOWAD_CHANCLE, "false");          
	    			MyApplication.showAd = Boolean.parseBoolean(showAd);
	    			Log.e(TAG, Constant.SHOWAD_CHANCLE+"获取在线广告配置参数"+MyApplication.showAd);
	    		}catch(Exception e){}
	        	if(!MyApplication.showAd){   //万普服务器配置不现实广告
	        		ll_bottom.setVisibility(View.GONE);
	    		}else{
	    			ll_bottom.setVisibility(View.VISIBLE);
	    		}
	    		//为了避免用户开始断网，开启应用之后再开网，这样fragment里面就加载不出广告
	    		cidianFragment.showAd();
	    		chaxunFragment.showAd();
	    		fanyiFragment.showAd();
	        }
	    }
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void initView() {
		setContentView(R.layout.activity_main);
		rootView = findViewById(R.id.dl);
		// 登录
		rl_login = (RelativeLayout) findViewById(R.id.rl_login);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 侧拉菜单
		rl_bdc = (RelativeLayout) findViewById(R.id.rl_bdc);
		ll_book = (LinearLayout) findViewById(R.id.ll_book);
		ll_dcb = (LinearLayout) findViewById(R.id.ll_dcb);
		ll_set = (LinearLayout) findViewById(R.id.ll_set);
		ll_recent = (LinearLayout) findViewById(R.id.ll_recent);
		iv_set_tips = (ImageView) findViewById(R.id.iv_set_tips);
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		ll_hf = (LinearLayout) findViewById(R.id.ll_hf);
		tv_lable_bdc = (TextView) findViewById(R.id.tv_lable_bdc);
		
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		tv_menu_faxian = (TextView) findViewById(R.id.tv_menu_faxian);   //发现
		tv_menu_jifen = (TextView) findViewById(R.id.tv_menu_jifen);     //积分
		
		rl_login.setOnClickListener(this);
		rl_bdc.setOnClickListener(this);
		ll_book.setOnClickListener(this);
		ll_dcb.setOnClickListener(this);
		ll_set.setOnClickListener(this);
		ll_hf.setOnClickListener(this);
		ll_recent.setOnClickListener(this);
		
		tv_menu_faxian.setOnClickListener(this);
		tv_menu_jifen.setOnClickListener(this);

		WindowManager wm = getWindowManager();
		Constant.WIN_WIDTH = wm.getDefaultDisplay().getWidth();

		cidianFragment = new FragmentCidian();
		fanyiFragment = new FragmentFanyi();
		chaxunFragment = new FragmentChaxun();
		fragmentMng = getSupportFragmentManager();
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(3); // ViewPager预加载数
		viewPager.setAdapter(new MyPagerAdapter(fragmentMng));
		viewPager.setOnPageChangeListener(new MyPagerChangedListener());

	}

	private void initTitleView() {
		titleView = (MainTitleView) findViewById(R.id.titleView);
		// 向上箭头
		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		titleView.setBackIcon(drawerArrow);
		// 初始化侧拉布局
		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				drawerArrow.setProgress(1f);
			}

			@Override
			public void onClose() {
				drawerArrow.setProgress(0.f);
			}

			@Override
			public void onDrag(float percent) {
				drawerArrow.setVerticalMirror(true);
				drawerArrow.setProgress(percent);
			}
		});
		dl.setViewPaget(viewPager);

		titleView.setListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_title_back:
					dl.open();
					break;
				case R.id.tv_danci:
					viewPager.setCurrentItem(0);
					break;
				case R.id.tv_chaxun:
					viewPager.setCurrentItem(1);
					break;
				case R.id.tv_fanyi:
					viewPager.setCurrentItem(3);
					break;
				default:
					break;
				}
			}
		});
	}

	private void setPf() {
		MyUtil.LOG_I(TAG, "主界面设置皮肤" + MyApplication.pf.title_bg);
		titleView.setTitleBackground(MyApplication.pf.title_bg);
		if (viewPager.getCurrentItem() == 0) {
			titleView.setTitleBackgroundAlpha(0);
			drawerArrow.setColor(getResources().getColor(R.color.color_white));
		} else
			drawerArrow.setColor(getResources().getColor(
					MyApplication.pf.text_color));
		// 侧滑菜单
		dl.setBackgroundResource(MyApplication.pf.main_menu_bg);
		int menuColor = getResources().getColor(MyApplication.pf.text_color);
		((TextView) findViewById(R.id.tv_name)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_bdc)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_book)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_dcb)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_set)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_hf)).setTextColor(menuColor);
		((TextView) findViewById(R.id.tv_menu_recent)).setTextColor(menuColor);
		
		tv_lable_bdc.setTextColor(menuColor);
		tv_menu_faxian.setTextColor(menuColor);
		tv_menu_jifen.setTextColor(menuColor);
		
		// ll_set.setBackgroundResource(MyApplication.pf.main_menu_bg_selecter);
		// ll_hf.setBackgroundResource(MyApplication.pf.main_menu_bg_selecter);
		// ll_bdc.setBackgroundResource(MyApplication.pf.main_menu_bg_selecter);
		// ll_book.setBackgroundResource(MyApplication.pf.main_menu_bg_selecter);
		// ll_dcb.setBackgroundResource(MyApplication.pf.main_menu_bg_selecter);

		cidianFragment.setPf();
		fanyiFragment.setPf();
		chaxunFragment.setPf();

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
	private void initData() {
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
		}
		// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
		updateUserInfos();
		
		MyApplication.property.rewardJy(Constant.REWARD_JY_OPEN, mContext, false);
		rewardJy = true;
	}
	private boolean rewardJy = false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(rewardJy){
			showRewardJyPo("启动应用奖励", Constant.REWARD_JY_OPEN);
			rewardJy = false;
		}
	}

	public static final int REQUEST_COED_LOGIN = 1; 
	public static final int REQUEST_COED_WORDBOOK = 2; 
	public static final int REQUEST_COED_MYWORD = 3; 
	public static final int REQUEST_COED_PF = 4; 
	public static final int REQUEST_COED_SETTING = 5; 
	public static final int REQUEST_COED_CIDIAN = 6; 
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_login:
			if (MyApplication.user == null) {
				Intent intent = new Intent(mContext, ActivityLogin.class);
				intent.putExtra("action", 1);
				startActivityForResult(intent, REQUEST_COED_LOGIN);
				overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			} else {
				startActivity(new Intent(mContext, ActivitySetUser.class));
				overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			}
			break;
		case R.id.rl_bdc: // 背单词
			dl.close();
			break;
		case R.id.ll_book: // 单词书
			startActivityForResult(new Intent(mContext, ActivityBook.class), REQUEST_COED_WORDBOOK);
			overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			break;
		case R.id.ll_dcb: // 生词本
			getoMyBook();
			break;
		case R.id.ll_set: // 设置
			startActivityForResult(new Intent(mContext, ActivitySetting.class), REQUEST_COED_SETTING);
			overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			break;
		case R.id.ll_recent://会话
			if (MyApplication.user == null) {
				Intent intent = new Intent(mContext, ActivityLogin.class);
				intent.putExtra("action", 1);
				startActivityForResult(intent, REQUEST_COED_LOGIN);
				overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			} else {
				Intent intent = new Intent(mContext, ChatActivityRecent.class);
				intent.putExtra("action", 1);
				startActivity(intent);
				overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
			}
			break;
		case R.id.ll_hf: // 换肤
			// huanf();
			startActivityForResult(new Intent(mContext, ActivityPf.class), REQUEST_COED_PF);
			overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.base_slide_remain);
			break;
		case R.id.tv_menu_faxian:   //发现广告
			startActivity(new Intent(mContext, ActivityMyAd.class));
			break;
		case R.id.tv_menu_jifen:    //积分
			// 显示推荐列表（综合）
			AppConnect.getInstance(this).showOffers(this);
			break;
		}
	}
	
	public void getoMyBook(){
		startActivityForResult(new Intent(mContext, ActivityMyWordBook.class), REQUEST_COED_MYWORD);
		overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_COED_LOGIN: // 登录
			break;
		case REQUEST_COED_WORDBOOK:  //词书
			if (cidianFragment != null) {
				cidianFragment.updateData();
			}
			break;
		case REQUEST_COED_SETTING: // 设置
			if (cidianFragment != null){
				cidianFragment.setAd();
				chaxunFragment.setAd();
				fanyiFragment.setAd();
				cidianFragment.updateData();
				cidianFragment.showMyBook();
			}
			break;
		case REQUEST_COED_PF: // 皮肤
			if (data != null && data.getBooleanExtra("change", false)) {
				setPf();
			}
			break;
		case REQUEST_COED_MYWORD: // 单词本
			if (cidianFragment != null)
				cidianFragment.showMyBook();
			break;
		case REQUEST_COED_CIDIAN: // 单词任务
			if (cidianFragment != null) {
				cidianFragment.updateData();
			}
			break;

		default:
			break;
		}

	}

	// 换肤
	private void huanf() {
		ChangePfDialog dialog = new ChangePfDialog(mContext,
				MyApplication.property.getPf());
		dialog.setPfListener(new PfListener() {
			@Override
			public void backPf(int color) {
				boolean change = false;
				switch (color) {
				case Property.VALUE_PF_1:
					if (MyApplication.property.getPf() != Property.VALUE_PF_1) {
						MyApplication.property.setPf(Property.VALUE_PF_1); // 存储皮肤ID
						change = true;
					}
					break;
				case Property.VALUE_PF_2:
					if (MyApplication.property.getPf() != Property.VALUE_PF_2) {
						MyApplication.property.setPf(Property.VALUE_PF_2);
						change = true;
					}
					break;
				case Property.VALUE_PF_3:
					if (MyApplication.property.getPf() != Property.VALUE_PF_3) {
						MyApplication.property.setPf(Property.VALUE_PF_3);
						change = true;
					}
					break;
				case Property.VALUE_PF_4:
					if (MyApplication.property.getPf() != Property.VALUE_PF_4) {
						MyApplication.property.setPf(Property.VALUE_PF_4);
						change = true;
					}
					break;
				case Property.VALUE_PF_5:
					if (MyApplication.property.getPf() != Property.VALUE_PF_5) {
						MyApplication.property.setPf(Property.VALUE_PF_5);
						change = true;
					}
					break;
				case Property.VALUE_PF_6:
					if (MyApplication.property.getPf() != Property.VALUE_PF_6) {
						MyApplication.property.setPf(Property.VALUE_PF_6);
						change = true;
					}
					break;
				case Property.VALUE_PF_7:
					if (MyApplication.property.getPf() != Property.VALUE_PF_7) {
						MyApplication.property.setPf(Property.VALUE_PF_7);
						change = true;
					}
					break;
				case Property.VALUE_PF_8:
					if (MyApplication.property.getPf() != Property.VALUE_PF_8) {
						MyApplication.property.setPf(Property.VALUE_PF_8);
						change = true;
					}
					break;
				default:
					break;
				}
				if (change) {
					setPf();
				}
			}
		});
		dialog.show();
	}

	public void setTitleAlpha(int alpha, boolean isScorlY){
		titleView.isScorlY = isScorlY;
		titleView.firstAlpha = alpha;
		titleView.setTitleBackgroundAlpha(alpha);
	}
	
	private int currIndex;
	class MyPagerChangedListener implements OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// tab滑动以及标题变色
			titleView.setTabScoller(currIndex, position, positionOffset);
		}

		@Override
		public void onPageSelected(int position) {
			currIndex = position;
			MyUtil.LOG_I(TAG, "viewpager选中了" + position);
			titleView.setTabSelected(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			/* 隐藏软键盘 */
			try {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(MainActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
		}
	}

	class MyPagerAdapter extends FragmentPagerAdapter {
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (cidianFragment == null)
					cidianFragment = new FragmentCidian();
				return cidianFragment;
			case 1:
				if (chaxunFragment == null)
					chaxunFragment = new FragmentChaxun();
				return chaxunFragment;
			case 2:
				if (fanyiFragment == null)
					fanyiFragment = new FragmentFanyi();
				return fanyiFragment;
			default:
				return null;
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(viewPager.getCurrentItem()!=0){
			viewPager.setCurrentItem(0);
		}else{
			moveTaskToBack(false); // 将activity 退到后台，注意不是finish()退出
		}
		// super.onBackPressed();
	}

	private void loadUser() {
		MyUtil.LOG_V(TAG, "用户信息：" + MyApplication.user);
		if (MyApplication.user != null) {
			tv_name.setText(MyApplication.user.getUsername());
			ImageLoader.getInstance().displayImage(MyApplication.user.getIcon(), iv_icon);
		} else {
			tv_name.setText("登录/注册");
			iv_icon.setImageResource(R.drawable.open_user_icon_def);
			UserDaoImpl dao = new UserDaoImpl();
			List<User> users = dao.findAll();
			MyUtil.LOG_V(TAG, "数据库查找用户：" + users.size());
			if (users != null && users.size() > 0) {
				MyApplication.user = users.get(0);
				for (User user : users) {
					MyUtil.LOG_V(TAG, "数据库查找用户：" + user);
				}
				tv_name.setText(MyApplication.user.getUsername());
				ImageLoader.getInstance().displayImage(MyApplication.user.getIcon(), iv_icon);
			}
		}
	}

	public void onResume() {
		super.onResume();
		// 从服务器端获取当前用户的虚拟货币.
		// 返回结果在回调函数getUpdatePoints(...)中处理
		AppConnect.getInstance(this).getPoints(this);
		MyUtil.LOG_V(TAG, "主界面可见了,pf" + MyApplication.pf);
		tv_lable_bdc.setText(BookUtils.getBookName());
		loadUser();
		// 友盟统计
		MobclickAgent.onResume(this); // 统计时长
		
		//是否有新的好友请求
		if(BmobDB.create(this).hasNewInvite()){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
		if(MyApplication.property.openXuNum!=0){
			iv_set_tips.setVisibility(View.VISIBLE);
		}else{
			iv_set_tips.setVisibility(View.GONE);
		}
		// 小圆点提示
		if (BmobDB.create(this).hasUnReadMsg()) {// 新消息
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_recent_tips.setVisibility(View.VISIBLE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
	}

	public void onPause() {
		super.onPause();
		dl.close();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
		// 友盟统计
		MobclickAgent.onPause(this);

	}

	@Override
	protected void onDestroy() {
		MyApplication.getApplication().removeActivity(this);
		// 万普关闭
		AppConnect.getInstance(this).close();
		//取消定时检测服务
		BmobChat.getInstance(this).stopPollService();
		dismissPopJy();
		if(myReceiver!=null){
			this.unregisterReceiver(myReceiver);
		}
		super.onDestroy();
	}

	
	/*******************Chat监听***********************/
	@Override
	public void onAddUser(BmobInvitation message) {
		iv_recent_tips.setVisibility(View.VISIBLE);

		//同时提醒通知
		String tickerText = message.getFromname()+"请求添加好友";
		boolean isAllowVibrate = MyApplication.property.vibrate;
		BmobNotifyManager.getInstance(this).showNotify(MyApplication.property.voice,
				isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),ActivityContact.class);
	
	}

	@Override
	public void onMessage(BmobMsg message) {
		MyUtil.LOG_V(TAG, "主界面收到消息："+message);
		String uid = message.getBelongId();
		if(MyApplication.property.voice){// 声音提示
			MyApplication.getApplication().getMediaPlayer().start();
		}
		MyUtil.LOG_V(TAG, Constant.openID+"主界面收到消息："+uid); //d6912b85ee

		if (uid.equalsIgnoreCase(Constant.openID)){// 如果是开发者openXu
			MyApplication.property.setOpneXuNum(1);
		}else{
			iv_recent_tips.setVisibility(View.VISIBLE);
		}
		//也要存储起来
		if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}
		if(MyApplication.property.openXuNum!=0){
			iv_set_tips.setVisibility(View.VISIBLE);
		}else{
			iv_set_tips.setVisibility(View.GONE);
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
	
	//获取积分展示
	final Handler mHandler = new Handler();
	private int pointNum = -1;
	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (tv_menu_jifen != null) {
				if(pointNum>=0){
					tv_menu_jifen.setText("积分:"+pointNum);
				}else{
					tv_menu_jifen.setText("积分");
				}
			}
		}
	};
	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		if(pointTotal == 0){
			if(MyApplication.user!=null){
				// 奖励虚拟货币
				AppConnect.getInstance(this).awardPoints(MyApplication.user.getPoint(), this);
				pointNum = MyApplication.user.getPoint();
			}
		}else{
			pointNum = pointTotal;
		}
		mHandler.post(mUpdateResults);
	}

	@Override
	public void getUpdatePointsFailed(String arg0) {
		
	}
	
	
	/*********************************/
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
//			ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
//			ShowLog("newLat ="+newLat+",newLong = "+newLong);
			if(!saveLatitude.equals(newLat)|| !saveLongtitude.equals(newLong)){//只有位置有变化就更新当前位置，达到实时更新的目的
				ChatUser u = (ChatUser) userManager.getCurrentUser(ChatUser.class);
				if(u!=null){
					final ChatUser user = new ChatUser();
					user.setLocation(MyApplication.lastPoint);
					user.setObjectId(u.getObjectId());
					user.update(this,new UpdateListener() {
						@Override
						public void onSuccess() {
							MyApplication.getApplication().setLatitude(String.valueOf(user.getLocation().getLatitude()));
							MyApplication.getApplication().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
//							ShowLog("经纬度更新成功");
						}
						@Override
						public void onFailure(int code, String msg) {
							// TODO Auto-generated method stub
//							ShowLog("经纬度更新 失败:"+msg);
						}
					});
				}
				
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
