package com.openxu.english.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openxu.english.R;


public class MainTitleView extends RelativeLayout{
	
	private String TAG = "MainTitleView";
	private Context context;
	public LinearLayout ll_top;
	private RelativeLayout rl_title, ll_title_menu;
	private LinearLayout ll_title_back;
	private ImageView iv_back, ll_title_bar;
	private TextView tv_danci, tv_chaxun, tv_fanyi;
	
	private OnClickListener listener;
	
	private void initView(Context context) {
		this.context = context;
		View.inflate(context, R.layout.view_maintitle_layout, this);
		rl_title = (RelativeLayout) this.findViewById(R.id.rl_title);
		ll_top = (LinearLayout) this.findViewById(R.id.ll_top);
		ll_title_menu  = (RelativeLayout) this.findViewById(R.id.ll_title_menu);
		ll_title_back = (LinearLayout) this.findViewById(R.id.ll_title_back);
		iv_back = (ImageView) this.findViewById(R.id.iv_title_back);
		ll_title_bar  = (ImageView) this.findViewById(R.id.ll_title_bar);
		tv_danci = (TextView) this.findViewById(R.id.tv_danci);
		tv_chaxun = (TextView) this.findViewById(R.id.tv_chaxun);
		tv_fanyi = (TextView) this.findViewById(R.id.tv_fanyi);
		
		//测量完毕回调
		ViewTreeObserver vto = getViewTreeObserver();   
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
            @Override  
            public void onGlobalLayout() { 
                getViewTreeObserver().removeGlobalOnLayoutListener(this); 
                initTextBar();
            }   
        });
	}
	public MainTitleView(Context context) {
		super(context);
		initView(context);
	}
	
	public MainTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MainTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	public void setListener(OnClickListener listener){
		this.listener = listener;
		ll_title_back.setOnClickListener(listener);
		tv_danci.setOnClickListener(listener);
		tv_chaxun.setOnClickListener(listener);
		tv_fanyi.setOnClickListener(listener);
	}
	
	private void setTitleTextColor(int color, int tranColor, int selected) {
		tv_danci.setTextColor(tranColor);
		tv_chaxun.setTextColor(tranColor);
		tv_fanyi.setTextColor(tranColor);
		switch (selected) {
		case 0:
			tv_danci.setTextColor(color);
			break;
		case 1:
			tv_chaxun.setTextColor(color);
			break;
		case 2:
			tv_fanyi.setTextColor(color);
			break;
		}
		ll_title_bar.setBackgroundColor(color);
	}
	public void setBackIcon(Drawable drawable){
		iv_back.setImageDrawable(drawable);
	}
	public void setBackIcon(int backIcon){
		iv_back.setImageResource(backIcon);
	}
	//设置背景色
	public void setTitleBackground(int drawId){
		rl_title.setBackgroundColor(context.getResources().getColor(drawId));
	}
	
	//设置背景色
	public void setTitleBackgroundAlpha(int alpha){
		rl_title.getBackground().setAlpha(alpha);
	}
	
	
	private int backWidth;  //左边菜单宽度
	private int menuWidth;  //右边菜单宽度
	private int textWidth;  //标题宽度
	private int tabWidth;   //tab宽度
	private void initTextBar() {
		// 设置宽度
		LayoutParams lp = (LayoutParams) ll_title_back.getLayoutParams();
		backWidth = ll_title_back.getWidth()+lp.leftMargin+lp.rightMargin;
		menuWidth = ll_title_menu.getWidth();
		WindowManager wm = ((Activity)getContext()).getWindowManager();
		textWidth = (wm.getDefaultDisplay().getWidth() - backWidth - menuWidth) / 3;
		tabWidth = textWidth;
		LayoutParams lp1 = (LayoutParams) ll_title_bar.getLayoutParams();
		lp1.width = tabWidth;
		System.out.println();
		ll_title_bar.setLayoutParams(lp1);
		//初始化选中第一个
		//选项卡位置
		LayoutParams ll = (LayoutParams) ll_title_bar.getLayoutParams();
		ll.leftMargin = backWidth;
		ll_title_bar.setLayoutParams(ll);
		setTabSelected(0);
	}

	public boolean isScorlY;
	public int firstAlpha;
	public void setTabScoller(int currIndex, int position, float positionOffset){
//		MyUtil.LOG_E(TAG, "  currIndex:"+currIndex+"  position:"+position+"  positionOffset:"+positionOffset);
		int allAlpha = 255;
		if(isScorlY){
			allAlpha = 255-firstAlpha;
		}else{
			allAlpha = 255;
		}
		if(positionOffset>0.99)
			positionOffset = 1;
		if(position==0&&positionOffset>0){
			setTitleBackgroundAlpha((int)(firstAlpha + (allAlpha * positionOffset)));
		}
		if(position==2&&currIndex==2&&positionOffset==0.0){//当点击翻译tab时执行
			setTitleBackgroundAlpha(255);
		}
		LayoutParams ll = (LayoutParams) ll_title_bar.getLayoutParams();
		
        //向右滑动
        if(currIndex == position){
        	 ll.leftMargin = (int)(backWidth + (currIndex * tabWidth + positionOffset * tabWidth));
        }else if(currIndex > position){
        	 ll.leftMargin = (int) (backWidth + currIndex * tabWidth - (1 - positionOffset)* tabWidth);
        }
        ll_title_bar.setLayoutParams(ll);
	}
	
	public void setTabSelected(int selected) {
		//颜色
		int color = context.getResources().getColor(SharedPrefData.getInstance(getContext()).getPf().text_color);
		int trancolor = context.getResources().getColor(R.color.color_tran_white);
		setTitleTextColor(color,trancolor, selected);
		
		switch (selected) {
		case 0:
			if(isScorlY){
				setTitleBackgroundAlpha(firstAlpha);
			}else{
				setTitleBackgroundAlpha(0);
			}
			break;
		default :
			setTitleBackgroundAlpha(255);
			break;
		}
	}
}
