package com.openxu.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.utils.MyUtil;


public class TitleView extends RelativeLayout{
	
	private String TAG = "TitleView";
	private Context context;
	private RelativeLayout rl_title;
	public LinearLayout ll_top;
	private LinearLayout ll_left_back;
	private LinearLayout ll_title_back;
	private LinearLayout ll_title_menu;
	private ImageView iv_back, iv_home, iv_menu;
	private TextView tv_title;
	
	private String title;
	private int backicon;
	private int homeicon;
	private int menuicon;
	private boolean isBackShow;
	private boolean isHomeShow;
	private boolean isMenuShow;
	private int titleTextColor;
	
	private OnClickListener listener;
	
	private void initView(Context context) {
		this.context = context;
		View.inflate(context, R.layout.view_title_layout, this);
		this.setBackgroundColor(Color.TRANSPARENT);
		rl_title = (RelativeLayout) this.findViewById(R.id.rl_title);
		ll_top = (LinearLayout) this.findViewById(R.id.ll_top);
		ll_left_back = (LinearLayout) this.findViewById(R.id.ll_left_back);
		ll_title_back = (LinearLayout) this.findViewById(R.id.ll_title_back);
		ll_title_menu = (LinearLayout) this.findViewById(R.id.ll_title_menu);
		iv_back = (ImageView) this.findViewById(R.id.iv_title_back);
		iv_home = (ImageView) this.findViewById(R.id.iv_title_home);
		iv_menu = (ImageView) this.findViewById(R.id.iv_title_menu);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}
	public TitleView(Context context) {
		super(context);
		initView(context);
	}
	
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.openxu.english", "titleStr");
		
		backicon = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.openxu.english", "backIcon", -1);
		homeicon = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.openxu.english", "homeIcon", -1);
		menuicon = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.openxu.english", "menuIcon", -1);
		
		isBackShow = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.openxu.english", "isBackShow", true);
		isHomeShow = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.openxu.english", "isHomeShow", false);
		isMenuShow = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.openxu.english", "isMenuShow", false);
		
		try{
			int bgColorId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.openxu.english", "bgColorId",-1);
			MyUtil.LOG_V(TAG, "得到title背景颜色ID："+bgColorId);
			if(bgColorId!=-1){
				setTitleBackground(bgColorId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			String bgColorStr = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.openxu.english", "bgColor");
			if(!TextUtils.isEmpty(bgColorStr)){
				setTitleBackgroundColor(Color.parseColor(bgColorStr));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			String titleTextColorStr = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.openxu.english", "titleTextColor");
			if(!TextUtils.isEmpty(titleTextColorStr)){
				titleTextColor = Color.parseColor(titleTextColorStr);
				setTitleTextColor(titleTextColor);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		MyUtil.LOG_I(TAG, "标题tile:"+title);
		MyUtil.LOG_I(TAG, "标题backicon:"+backicon);
		MyUtil.LOG_I(TAG, "标题homeicon:"+homeicon);
		MyUtil.LOG_I(TAG, "标题menuicon:"+menuicon);
		MyUtil.LOG_I(TAG, "标题isBackShow:"+isBackShow);
		MyUtil.LOG_I(TAG, "标题isHomeShow:"+isHomeShow);
		MyUtil.LOG_I(TAG, "标题isMenuShow:"+isMenuShow);
		setBackShow(isBackShow);
		setHomeShow(isHomeShow);
		setMenuShow(isMenuShow);
		setTitle(title);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		
	}
	
	public void setListener(OnClickListener listener){
		this.listener = listener;
		ll_left_back.setOnClickListener(listener);
		if(isBackShow)
			ll_title_back.setOnClickListener(listener);
		if(isMenuShow)
			ll_title_menu.setOnClickListener(listener);
	}
	
	public void setTitle(String title) {
		this.title = title;
		tv_title.setText(title);
		if(!isBackShow&&!isHomeShow&&!isMenuShow)
			tv_title.setGravity(Gravity.CENTER);
	}
	public void setTitleTextColorResources(int colorId) {
		tv_title.setTextColor(context.getResources().getColor(colorId));
	}
	public void setTitleTextColor(int color) {
		tv_title.setTextColor(color);
	}
	public void setBackShow(boolean isBackShow) {
		this.isBackShow = isBackShow;
		if(isBackShow){
			ll_title_back.setVisibility(View.VISIBLE);
			setBackIcon();
		}else
			ll_title_back.setVisibility(View.GONE);
	}
	public void setHomeShow(boolean isHomeShow) {
		this.isHomeShow = isHomeShow;
		if(isHomeShow){
			iv_home.setVisibility(View.VISIBLE);
			setHomeIcon();
		}else
			iv_home.setVisibility(View.GONE);
	}
	public void setMenuShow(boolean isMenuShow) {
		this.isMenuShow = isMenuShow;
		if(isMenuShow){
			ll_title_menu.setVisibility(View.VISIBLE);
			setMenuIcon();
		}else
			ll_title_menu.setVisibility(View.GONE);
	}
	public void setBackIcon(Drawable drawable){
		if(isBackShow)
			iv_back.setImageDrawable(drawable);
	}
	public void setBackIcon(){
		if(isBackShow&&backicon!=-1)
			iv_back.setImageResource(backicon);
	}
	public void setHomeIcon(){
		if(isHomeShow&&homeicon!=-1)
			iv_home.setImageResource(homeicon);
	}
	public void setMenuIcon(){
		if(isMenuShow&&menuicon!=-1)
			iv_menu.setImageResource(menuicon);
	}
	//设置背景色
	public void setTitleBackground(int drawId){
		ll_top.setBackgroundColor(context.getResources().getColor(drawId));
		rl_title.setBackgroundColor(context.getResources().getColor(drawId));
	}
	//设置背景色
	public void setTitleBackgroundColor(int color){
		rl_title.setBackgroundColor(color);
	}
	
	//设置菜单按下背景
	public void setTitleMenuItemBack(int drawId){
//		ll_left_back.setBackgroundResource(drawId);
		if(isBackShow)
			ll_title_back.setBackgroundResource(drawId);
		if(isMenuShow)
			ll_title_menu.setBackgroundResource(drawId);
	}
	//设置菜单按下背景
		public void setTitleMenuItemBackColor(int color){
//			ll_left_back.setBackgroundResource(drawId);
			if(isBackShow)
				ll_title_back.setBackgroundColor(color);
			if(isMenuShow)
				ll_title_menu.setBackgroundColor(color);
		}
	
}
