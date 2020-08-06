package com.openxu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.openxu.db.DatabaseManager;
import com.openxu.db.bean.User;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;
import com.openxu.utils.Pf;
import com.openxu.utils.Property;
import com.openxu.utils.VoicePlayerImpl;

public class MyApplication extends Application {
	private String TAG = "MyApplication";
	private static MyApplication application;
	public static Property property;
	public VoicePlayerImpl player;
	public static Pf pf;
	public static User user;
	public static boolean showAd = false;
	
	private ArrayList<Activity> activitys;
	public DisplayImageOptions sentensOptions;
	
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		
		activitys = new ArrayList<Activity>();
		
		loadData();
		
        sentensOptions = new DisplayImageOptions.Builder()  
//         .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片  
        .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
        .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
        //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
        //设置图片加入缓存前，对bitmap进行设置  
        //.preProcessor(BitmapProcessor preProcessor)  
        .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
        .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
        .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
        .build();//构建完成  
        
        initImageLoader();
        initBaidu();
	}
	
	
	/**
	 * 初始化百度相关sdk initBaidumap
	 * @Title: initBaidumap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}

	/**
	 * 初始化百度定位sdk
	 * @Title: initBaiduLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}
	
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			if (lastPoint != null) {
				MyUtil.LOG_D(TAG, "百度定位："+location.getLatitude());
				if (lastPoint.getLatitude() == location.getLatitude()&& lastPoint.getLongitude() == location.getLongitude()) {
//					BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocationClient.stop();
					return;
				}
			}
			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}
	
	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.m_person_head_icon)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.open_user_icon_def) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.open_user_icon_def) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private String longtitude = "";

	/**
	 * 获取经度
	 * 
	 * @return
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 * 
	 * @param pwd
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}

	public final String PREF_LATITUDE = "latitude";// 经度
	private String latitude = "";

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置维度
	 * 
	 * @param pwd
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}

	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setLatitude(null);
		setLongtitude(null);
	}
	
	public static MyApplication getApplication() {
		return application;
	}

	public static void loadData(){
		// 初始化数据库管理工具类
		DatabaseManager.initializeInstance(application);
		property = new Property();
		pf = new Pf();
	}
	
	public void addActivity(Activity a){
		activitys.add(a);
	}

	public void removeActivity(Activity a){
		activitys.remove(a);
	}
	
	public void finishActivitys(){
		for(Activity activity : activitys){
			if(activity!=null){
				activity.finish();
			}
		}
	}
	
	MediaPlayer mMediaPlayer;
	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}

}
