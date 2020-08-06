package com.openxu.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.v3.listener.UpdateListener;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.ui.adapter.MyspinnerAdapter;
import com.openxu.utils.Constant;
import com.openxu.utils.FileUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.Property;
import com.openxu.view.ClearAdDialog;
import com.openxu.view.ClearAdDialog.ClearAdListener;
import com.openxu.view.CopyDBDialog;
import com.openxu.view.pulltozoomview.PullToZoomScrollViewEx;
import com.umeng.analytics.MobclickAgent;

/**
 * @author openXu
 */
public class ActivitySetting extends BaseActivity implements UpdatePointsNotifier{
    private PullToZoomScrollViewEx scrollView;
	private ScrollView rootView;
	//个人信息
	private RelativeLayout rl_userset, rl_lev;
	private ImageView icon_bg, iv_icon, iv_lev;
	private TextView tv_user_name, tv_lev,tv_tx;
	private LinearLayout ll_action_button;
	private TextView tv_register, tv_login;
	//单词任务，播放速度
	private EditText et_renwu_num, et_play_sb;
	private RelativeLayout rl_beifen, rl_huanyuan;
	//记词模式
	private RelativeLayout spinneridjc;
	private TextView tv_jcms;
	private ArrayList<String> jclist; 
	//复习模式
	private RelativeLayout spinneridfx, rl_fx_num;
	private ImageView line;
	private TextView tv_fxms;
	private ArrayList<String> fxlist; 
	private EditText et_fx_num;
	
	private RelativeLayout rl_clearad, rl_msg;
	private ImageView line_ad;
	private TextView tv_lablead1, tv_point, tv_ad;
	
	private RelativeLayout rl_women;
	private ImageView iv_tips;
	//主页显示我的单词本
	private ImageView iv_showbook;
	
	//上传数据库
	private LinearLayout ll_dbsc;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting);
		
		scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
		scrollView.setZoomEnabled(true);
		rootView = scrollView.getRootView();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 13.0F)));
        scrollView.setHeaderLayoutParams(localObject);
		
		rl_userset = (RelativeLayout) rootView.findViewById(R.id.rl_userset);
        icon_bg = (ImageView) rootView.findViewById(R.id.icon_bg);
        ll_action_button = (LinearLayout) rootView.findViewById(R.id.ll_action_button);
		iv_icon = (ImageView) rootView.findViewById(R.id.iv_icon);
		tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);  
		tv_register = (TextView) rootView.findViewById(R.id.tv_register);  
		tv_login = (TextView) rootView.findViewById(R.id.tv_login);  
		tv_register.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		rl_userset.setOnClickListener(this);
		rl_lev = (RelativeLayout) rootView.findViewById(R.id.rl_lev);
		rl_lev.setOnClickListener(this);
		iv_lev = (ImageView) rootView.findViewById(R.id.iv_lev);
		tv_lev = (TextView) rootView.findViewById(R.id.tv_lev);  
		tv_tx = (TextView) rootView.findViewById(R.id.tv_tx);  
		rl_msg = (RelativeLayout) rootView.findViewById(R.id.rl_msg);
		rl_msg.setOnClickListener(this);
		
		et_renwu_num = (EditText) rootView.findViewById(R.id.et_renwu_num);
		et_play_sb = (EditText) rootView.findViewById(R.id.et_play_sb);
		rl_beifen = (RelativeLayout) rootView.findViewById(R.id.rl_beifen);
		
		rl_beifen.setOnClickListener(this);
		rl_huanyuan = (RelativeLayout) findViewById(R.id.rl_huanyuan);
		rl_huanyuan.setOnClickListener(this);
		//spinner
		spinneridjc = (RelativeLayout) rootView.findViewById(R.id.spinneridjc);
		tv_jcms = (TextView) rootView.findViewById(R.id.tv_jcms);  
		spinneridfx = (RelativeLayout) rootView.findViewById(R.id.spinneridfx);
		tv_fxms = (TextView) rootView.findViewById(R.id.tv_fxms);  
		rl_fx_num = (RelativeLayout) rootView.findViewById(R.id.rl_fx_num);
		line = (ImageView) findViewById(R.id.line);
		et_fx_num = (EditText) rootView.findViewById(R.id.et_fx_num);
		
		rl_clearad = (RelativeLayout) rootView.findViewById(R.id.rl_clearad);
		rl_clearad.setOnClickListener(this);
		line_ad = (ImageView) findViewById(R.id.line_ad);
		tv_lablead1 = (TextView) rootView.findViewById(R.id.tv_lablead1);
		tv_point = (TextView) rootView.findViewById(R.id.tv_point);  
		tv_ad = (TextView) rootView.findViewById(R.id.tv_ad);  
		tv_lablead1.setText("("+MyApplication.property.clearPoint+"积分"+MyApplication.property.clearDay+"天)");
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			//加载广告
//			tv_ad.setText("有广告");
			rl_clearad.setVisibility(View.VISIBLE);
			line_ad.setVisibility(View.VISIBLE);
		}else{
			//不加载广告
//			String adTime = "到期时间:"+MyApplication.property.getAdTime();
//			tv_ad.setText(adTime);
			rl_clearad.setVisibility(View.GONE);
			line_ad.setVisibility(View.GONE);
		}
		
		rl_women = (RelativeLayout) findViewById(R.id.rl_women);
		iv_tips = (ImageView) findViewById(R.id.iv_tips);
		
		rl_women.setOnClickListener(this);
		
		iv_showbook = (ImageView) findViewById(R.id.iv_showbook);
		iv_showbook.setOnClickListener(this);
		if(MyApplication.property.showBook){
			iv_showbook.setImageResource(R.drawable.on);
		}else{
			iv_showbook.setImageResource(R.drawable.off);
		}
		
		ll_dbsc = (LinearLayout) findViewById(R.id.ll_dbsc);
		ll_dbsc.setOnClickListener(this);
		if(MyApplication.user!=null && MyApplication.user.getUsername().endsWith(Constant.openName)){
			ll_dbsc.setVisibility(View.VISIBLE);
		}else{
			ll_dbsc.setVisibility(View.GONE);
		}
	}
	@Override
	protected void setPf() {
		super.setPf();
		icon_bg.setImageResource(MyApplication.pf.setting_bg);
		rl_msg.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_beifen.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_huanyuan.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_clearad.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		rl_women.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		ll_dbsc.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
//		findViewById(R.id.rl_userset).setBackgroundResource(MyApplication.getApplication().pf.main_menu_bg_id);
	}
	@Override
	protected void initData() {
		et_renwu_num.setText(MyApplication.property.renwu_num+"");
		et_play_sb.setText(MyApplication.property.play_sp+"");
		et_fx_num.setText(MyApplication.property.fx_num+"");
		// 记词模式
		jclist = new ArrayList<String>();  
		jclist.add("随机抽取");  
		jclist.add("字母顺序");  
        adapterjc = new MyspinnerAdapter(this, jclist, null , MyspinnerAdapter.TYPE_1);  
		if(MyApplication.property.jcms == Property.VALUE_JCMS_SJ){
			tv_jcms.setText((CharSequence) adapterjc.getItem(0));  
		}else{
			tv_jcms.setText((CharSequence) adapterjc.getItem(1));  
		}
        // 点击右侧按钮，弹出下拉框  
		spinneridjc.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(jclist.size()>0){  
                	spinneridjc.setBackgroundResource(R.drawable.spinner_first_item);  
                }  
                showWindow(1,spinneridjc);  
            }  
        });  
		// 复习模式
		fxlist = new ArrayList<String>();  
		fxlist.add("从已测单词随机抽取");  
		fxlist.add("复习昨日单词");
        adapterfx = new MyspinnerAdapter(this, fxlist, null , MyspinnerAdapter.TYPE_1);  
		if(MyApplication.property.fxms == Property.VALUE_FXMS_SJ){
			tv_fxms.setText((CharSequence) adapterfx.getItem(0));  
			rl_fx_num.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
		}else{
			tv_fxms.setText((CharSequence) adapterfx.getItem(1));  
			rl_fx_num.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
        // 点击右侧按钮，弹出下拉框  
		spinneridfx.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(fxlist.size()>0){  
                	spinneridfx.setBackgroundResource(R.drawable.spinner_first_item);  
                }  
                showWindow(2, spinneridfx);  
            }  
        });  
		//每日任务数量
		et_renwu_num.addTextChangedListener(new MyTextWatcher(1));
		//单词播放速度
		et_play_sb.addTextChangedListener(new MyTextWatcher(2));
		//复习数量
		et_fx_num.addTextChangedListener(new MyTextWatcher(3));
	}
	
	class MyTextWatcher implements TextWatcher{
		private int action;
		public MyTextWatcher(int action){
			this.action = action;
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String numStr = "";
			int num = 0;
			switch (action) {
			case 1:
				numStr = et_renwu_num.getText().toString().trim();
				break;
			case 2:
				numStr = et_play_sb.getText().toString().trim();
				break;
			case 3:
				numStr = et_fx_num.getText().toString().trim();
				break;
			}
			if(TextUtils.isEmpty(numStr)){
				return;
			}
			try{
				num = Integer.parseInt(numStr);
			}catch(Exception e){
				return;
			}
			switch (action) {
			case 1:
				MyApplication.property.setRenwu_num(num);
				break;
			case 2:
				MyApplication.property.setPlay_sp(num);
				break;
			case 3:
				MyApplication.property.setFx_num(num);
				break;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		//清空
		MyMessageReceiver.mNewNum=0;
		if(MyApplication.property.openXuNum!=0){
			iv_tips.setVisibility(View.VISIBLE);
		}else{
			iv_tips.setVisibility(View.GONE);
		}
		// 从服务器端获取当前用户的虚拟货币.
		// 返回结果在回调函数getUpdatePoints(...)中处理
		AppConnect.getInstance(this).getPoints(this);
		if(MyApplication.user!=null){
			ll_action_button.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(MyApplication.user.getIcon(), iv_icon);
			tv_user_name.setText(MyApplication.user.getUsername());
			tv_user_name.setVisibility(View.VISIBLE);
		}else{
			ll_action_button.setVisibility(View.VISIBLE);
			tv_user_name.setVisibility(View.GONE);
			iv_icon.setImageResource(R.drawable.open_user_icon_def);
		}
		showLev();
	}
	
	private void showLev(){
		int jy = MyApplication.property.getLocalJy();
		int pro = jy%100;
		int lev = jy/100+1;
		String levStr = "";
		//等级
		tv_lev.setText("经验值  "+jy);
		//头衔
		if(jy<=Constant.LV_JY1){
			levStr = "幼儿园  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_1);
		}else if(jy<=Constant.LV_JY2){
			levStr = "小学生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_2);
		}else if(jy<=Constant.LV_JY3){
			levStr = "初中生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_3);
		}else if(jy<=Constant.LV_JY4){
			levStr = "高中生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_4);
		}else if(jy<=Constant.LV_JY5){
			levStr = "大学生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_5);
		}else if(jy<=Constant.LV_JY6){
			levStr = "研究生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_6);
		}else if(jy<=Constant.LV_JY7){
			levStr = "博士生  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_7);
		}else{
			levStr = "博士后  " +"LV."+lev;
			iv_lev.setImageResource(R.drawable.open_user_lv_7);
		}
		tv_tx.setText(levStr);
	}
	
	private LinearLayout drawdownlayout;
	private ListView listView;
	private MyspinnerAdapter adapterjc;
	private MyspinnerAdapter adapterfx;
	private PopupWindow popupWindow;
	public void showWindow(final int witch, View position) {
		drawdownlayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.myspinner_dropdown, null);
		listView = (ListView) drawdownlayout.findViewById(R.id.listView);
		listView.setSelector(MyApplication.getApplication().pf.item_selector);
		popupWindow = new PopupWindow(position);
		if(witch == 1){
			listView.setAdapter(adapterjc);
			// 设置弹框的宽度为布局文件的宽
			popupWindow.setWidth(spinneridjc.getWidth());
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}else if(witch == 2){   //
			listView.setAdapter(adapterfx);
			// 设置弹框的宽度为布局文件的宽
			popupWindow.setWidth(spinneridfx.getWidth());
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		// 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击弹框外部，弹框消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(drawdownlayout);
		// 设置弹框出现的位置，在v的正下方横轴偏移textview的宽度，为了对齐~纵轴不偏移
		popupWindow.showAsDropDown(position, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss() {
				if(witch == 1){
					spinneridjc.setBackgroundResource(R.drawable.preference_single_item);
				}else if(witch == 2){
					spinneridfx.setBackgroundResource(R.drawable.preference_single_item);
				}
			}
		});
		// listView的item点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 弹框消失
				popupWindow.dismiss();
				popupWindow = null;
				int ms = 0;
				if(witch == 1){
					tv_jcms.setText(jclist.get(arg2));// 设置所选的item作为下拉框的标题
					if(jclist.get(arg2).contains("随机")){
						ms = Property.VALUE_JCMS_SJ;
					}else{
						ms = Property.VALUE_JCMS_SX;
					}
					if(MyApplication.property.jcms!=ms){
						MyApplication.property.setJCMS(ms);
					}
				}else if(witch == 2){
					tv_fxms.setText(fxlist.get(arg2));// 设置所选的item作为下拉框的标题
					if(fxlist.get(arg2).contains("随机")){
						ms = Property.VALUE_FXMS_SJ;
						rl_fx_num.setVisibility(View.VISIBLE);
						line.setVisibility(View.VISIBLE);
					}else{
						ms = Property.VALUE_FXMS_SX;
						rl_fx_num.setVisibility(View.GONE);
						line.setVisibility(View.GONE);
					}
					if(MyApplication.property.fxms!=ms){
						MyApplication.property.setFXMS(ms);
					}
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_lev:
			startActivity(new Intent(mContext, ActivitySetUserLev.class));
			break;
		case R.id.rl_userset:
			if(MyApplication.user!=null){
				startActivity(new Intent(mContext, ActivitySetUser.class));
			}
			break;
		case R.id.tv_register:
			startActivityForResult(new Intent(mContext, ActivityRegist.class), 1);
			break;
		case R.id.tv_login:
			intent = new Intent(mContext, ActivityLogin.class);
			intent.putExtra("action", 1);
			startActivity(intent);
			break;
		case R.id.rl_beifen:    //备份数据
			beifen();
			break;
		case R.id.rl_huanyuan:    //还原数据
			huanyuan();
			break;
		case R.id.rl_women:
			startActivity(new Intent(mContext, ActivityApp.class));
			break;
		case R.id.ll_dbsc:
			startActivity(new Intent(mContext, ActivityDBSC.class));
			break;
		case R.id.rl_msg:
			startActivity(new Intent(mContext, ActivitySetMsg.class));
			break;
		case R.id.rl_clearad:    //去广告
			if(MyApplication.property.initAd()&&MyApplication.showAd){
			}else{
				MyUtil.showToast(mContext, -1, "广告已经清除");
				return;
			}
			if(pointNum>=0){
				if(pointNum>=MyApplication.property.clearPoint){
					ClearAdDialog clearsdialog = new ClearAdDialog(mContext);
					clearsdialog.setListener(new ClearAdListener() {
						@Override
						public void clear() {
							//去广告
							MyApplication.property.setAdTime(MyApplication.property.clearDay);
							String adTime = "到期时间:"+MyApplication.property.getAdTime();
							tv_ad.setText(adTime);
							if(MyApplication.user!=null){
								MyApplication.user.setPoint(MyApplication.user.getPoint()-MyApplication.property.clearPoint);
							}
							// 消费虚拟货币.
							AppConnect.getInstance(mContext).spendPoints(MyApplication.property.clearPoint, ActivitySetting.this);
						}
					});
					clearsdialog.show();
				}else{
					MyUtil.showToast(mContext, -1, "您的积分不够哦");
					// 显示推荐列表（综合）
					AppConnect.getInstance(this).showOffers(this);
				}
			}else{
				MyUtil.showToast(mContext, -1, "您的积分不够哦");
				// 显示推荐列表（综合）
				AppConnect.getInstance(this).showOffers(this);
			}
			break;
			
		case R.id.iv_showbook:
			if(MyApplication.property.showBook){
				iv_showbook.setImageResource(R.drawable.off);
				MyApplication.property.setShowBook(false);
			}else{
				iv_showbook.setImageResource(R.drawable.on);
				MyApplication.property.setShowBook(true);
			}
			break;
		}
	}
	
	//备份数据库和配置文件
	private void beifen(){
		if (MyApplication.property.dbCopy) {
			MyUtil.LOG_V(TAG, "备份数据库");
			final CopyDBDialog updialog = new CopyDBDialog(mContext);
			updialog.setShowText("正在备份数据...");
			updialog.setUploadingText(0 + "%");
			updialog.show();
			new AsyncTask<Void, String, Integer>() {
				@Override
				protected Integer doInBackground(Void... params) {
					if(!MyUtil.existSDCard())
						return 1;       //没有sd卡
					//备份数据库
					String srcPath = "/data/data/" + mContext.getPackageName()+ "/databases";
					String destPath = Constant.CATCH_DIR;
					MyUtil.LOG_I(TAG, "备份目录："+destPath);
					File srcFile = new File(srcPath);
					File destFile = new File(destPath);
					if (!srcFile.exists())
						return 11;
					if (!destFile.exists()){
						boolean ok = destFile.mkdir();
						MyUtil.LOG_I(TAG, "创建备份目录："+ok);
					}
					destPath = Constant.CATCH_DIR_DB;
					MyUtil.LOG_I(TAG, "数据库备份目录："+destPath);
					destFile = new File(destPath);
					if (!destFile.exists()){
						boolean ok = destFile.mkdir();
						MyUtil.LOG_I(TAG, "创建备份目录："+ok);
					}
					int conut = 0;
					for (String db : Constant.dbs) {
						try {
							FileInputStream in = new FileInputStream(srcPath + File.separator + db);
							int all = in.available();
							long free = MyUtil.getSDFreeSize();
							MyUtil.LOG_I(TAG, "sd卡容量："+free);
							if(all>free)
								return 2;       //容量不够
							FileOutputStream out = new FileOutputStream(destPath + File.separator + db);
							MyUtil.LOG_I(TAG, "备份数据库："+destPath + File.separator + db);
							byte[] buffer = new byte[1024];
							int length;
							while ((length = in.read(buffer)) > 0) {
								out.write(buffer, 0, length);
								conut += 1024;
								publishProgress(MyUtil.getFloatStr(conut, all));
							}
							// 最后关闭就可以了
							out.flush();
							out.close();
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
							return 11;
						}
					}
					//备份sp
					String spSrcPath = "/data/data/" + mContext.getPackageName()+ "/shared_prefs";
					String spDestPath = Constant.CATCH_DIR_SP;
					MyUtil.LOG_I(TAG, "sp备份目录："+spDestPath);
					File spSrcFile = new File(spSrcPath);
					File spDestFile = new File(spDestPath);
					if (!spSrcFile.exists())
						return 11;
					if (!spDestFile.exists()){
						boolean ok = spDestFile.mkdir();
						MyUtil.LOG_I(TAG, "创建sp备份目录："+ok);
					}
					File[] files = spSrcFile.listFiles();
					for(File file : files){
						try {
							if(file.isFile()){
								String srcFilePath = file.getAbsolutePath();
								MyUtil.LOG_I(TAG, "备份"+srcFilePath);
								FileInputStream in = new FileInputStream(srcFilePath);
								int all = in.available();
								long free = MyUtil.getSDFreeSize();
								if(all>free){
									return 2;       //容量不够
								}
								FileOutputStream out = new FileOutputStream(spDestPath + File.separator + file.getName());
								byte[] buffer = new byte[1024];
								int length;
								while ((length = in.read(buffer)) > 0) {
									out.write(buffer, 0, length);
									conut += 1024;
									publishProgress(MyUtil.getFloatStr(conut, all));
								}
								// 最后关闭就可以了
								out.flush();
								out.close();
								in.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
							FileUtils.delAllFile(destPath);
							return 11;
						}
					}
					return 10;
				}

				protected void onProgressUpdate(String... values) {
					updialog.setUploadingText(values[0]);
				};

				protected void onPostExecute(Integer result) {
					updialog.dismiss();
					switch (result) {
					case 1:
						MyUtil.showToast(mContext, R.string.error_sd, "");
						break;
					case 2:
						MyUtil.showToast(mContext, R.string.error_sd_nofree, "");
						break;
					case 10:
						MyUtil.showToast(mContext, R.string.ok_beifen, "");
						break;
					case 11:
						MyUtil.showToast(mContext, R.string.error_beifen, "");
						break;
					default:
						MyUtil.showToast(mContext, R.string.error_beifen, "");
						break;
					}
					
				};

			}.execute();
		}else{
			MyUtil.showToast(mContext, R.string.error_beifen, "");
		}
	}
	
	//还原数据
	private void huanyuan(){
		if(!MyUtil.existSDCard()){
			MyUtil.showToast(mContext, R.string.error_sd, "");
			return;
		}
		final String dbSrcPath = Constant.CATCH_DIR_DB;
		final String spSrcPath = Constant.CATCH_DIR_SP;
		final File dbSrcFile = new File(dbSrcPath);
		final File spSrcFile = new File(spSrcPath);
		if (!dbSrcFile.exists()&&!spSrcFile.exists()){
			MyUtil.showToast(mContext, R.string.error_huanyuan_nofile, "");
			MyUtil.LOG_W(TAG, "备份文件为空");
			return;
		}
		
		final CopyDBDialog updialog = new CopyDBDialog(mContext);
		updialog.setShowText("正在还原数据...");
		updialog.setUploadingText(0 + "%");
		updialog.show();
		new AsyncTask<Void, String, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				int conut = 0;
				if (!dbSrcFile.exists()){
				}else{
					//还原数据库
					MyUtil.LOG_V(TAG, "还原数据库");
					String dbDestPath = "/data/data/" + mContext.getPackageName()+ "/databases";
					for (String db : Constant.dbs) {
						try {
							FileInputStream in = new FileInputStream(dbSrcPath + File.separator + db);
							int all = in.available();
							FileOutputStream out = new FileOutputStream(dbDestPath + File.separator + db);
							MyUtil.LOG_I(TAG, "还原数据库："+dbDestPath + File.separator + db);
							byte[] buffer = new byte[1024];
							int length;
							while ((length = in.read(buffer)) > 0) {
								out.write(buffer, 0, length);
								conut += 1024;
								publishProgress(MyUtil.getFloatStr(conut, all));
							}
							// 最后关闭就可以了
							out.flush();
							out.close();
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
							return 11;
						}
					}
				}
				
				//还原sp
				if (!spSrcFile.exists()){
				}else{
					String spDestPath = "/data/data/" + mContext.getPackageName()+ "/shared_prefs";
					File spDestFile = new File(spDestPath);
					if (!spDestFile.exists())
						spDestFile.mkdir();
					File[] files = spSrcFile.listFiles();
					for(File file : files){
						try {
							if(file.isFile()){
								String srcFilePath = file.getAbsolutePath();
								MyUtil.LOG_I(TAG, "还原"+srcFilePath);
								FileInputStream in = new FileInputStream(srcFilePath);
								int all = in.available();
								FileOutputStream out = new FileOutputStream(spDestPath + File.separator + file.getName());
								byte[] buffer = new byte[1024];
								int length;
								while ((length = in.read(buffer)) > 0) {
									out.write(buffer, 0, length);
									conut += 1024;
									publishProgress(MyUtil.getFloatStr(conut, all));
								}
								// 最后关闭就可以了
								out.flush();
								out.close();
								in.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
							return 11;
						}
					}
				}
				return 10;
			}

			protected void onProgressUpdate(String... values) {
				updialog.setUploadingText(values[0]);
			};

			protected void onPostExecute(Integer result) {
				updialog.dismiss();
				switch (result) {
				case 1:
					MyUtil.showToast(mContext, R.string.error_sd, "");
					break;
				case 10:
					MyUtil.showToast(mContext, R.string.ok_huanyuan, "");
//					int i = 1/0;   //重新启动
					Intent intent = new Intent(mContext, MainActivity.class);
					PendingIntent restarIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
					AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis()+1500, restarIntent);
					MyApplication.getApplication().finishActivitys();
//					如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
					MobclickAgent.onKillProcess(mContext);
					// 早死早超生
					android.os.Process.killProcess(android.os.Process.myPid());
					break;
				case 11:
					MyUtil.showToast(mContext, R.string.error_huanyuan, "");
					break;
				default:
					MyUtil.showToast(mContext, R.string.error_huanyuan, "");
					break;
				}
			};

		}.execute();
	}
	
	//获取积分展示
	final Handler mHandler = new Handler();
	private int pointNum = -1;
	private String pointErr;
	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (tv_point != null) {
				if(pointNum>=0){
					tv_point.setText("我的积分:"+pointNum);
				}else{
					tv_point.setText("pointErr");
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
				mHandler.post(mUpdateResults);
				return;
			}
		}
		pointNum = pointTotal;
		mHandler.post(mUpdateResults);
		if(MyApplication.user!=null){
			MyApplication.user.setPoint(pointTotal);
			MyApplication.user.update(mContext,MyApplication.user.getObjectId(),new UpdateListener() {
	            @Override
	            public void onSuccess() {
	            	new UserDaoImpl().updata(MyApplication.user);
	            	MyUtil.LOG_E(TAG, "同步用户积分成功"+MyApplication.user);
	            }
	            @Override
	            public void onFailure(int code, String msg) {
	            	MyUtil.LOG_E(TAG, msg);
	            }
	        });
		}
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {
		pointErr = error;
		mHandler.post(mUpdateResults);
	}
	
	
	@Override
	public void onMessage(BmobMsg message) {
		super.onMessage(message);
		MyUtil.LOG_V(TAG, "设置界面收到消息："+message);
		// 小圆点提示
		String uid = message.getBelongId();
		if (uid.equals(Constant.openID)){// 如果是开发者openXu
			MyApplication.property.setOpneXuNum(1);
		}
		if(MyApplication.property.openXuNum!=0){
			iv_tips.setVisibility(View.VISIBLE);
		}else{
			iv_tips.setVisibility(View.GONE);
		}
	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
}
