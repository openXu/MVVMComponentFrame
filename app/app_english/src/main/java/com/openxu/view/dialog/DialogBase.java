package com.openxu.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

/**
 *自定义对话框基类
 *支持：对话框全屏显示控制、title显示控制，一个button或两个
 */
public abstract class DialogBase extends Dialog {
	protected OnClickListener onSuccessListener;
	protected Context mainContext;
	protected OnClickListener onCancelListener;//提供给取消按钮
	protected OnDismissListener onDismissListener;
	
	protected View view;
	protected TextView tv_cancle, tv_ok;
	private boolean isFullScreen = false;
	
	private boolean hasTitle = true;      //是否有title
	
	private int width = 0, height = 0, x = 0, y = 0;
	private int iconTitle = 0;
	private String message, title;
	private String nameOkButton, nameCancelButton;
	private final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	private boolean isCancel = true;//默认是否可点击back按键/点击外部区域取消对话框
	
	
	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 * 构造函数
	 * @param context 对象应该是Activity
	 */
	public DialogBase(Context context) {
		super(context, R.style.alert);
		this.mainContext = context;
	}
	
	/** 
	 * 创建事件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.dialog_base);
		this.onBuilding();
		// 设置标题和消息
		LinearLayout ll_top = (LinearLayout)findViewById(R.id.ll_top);
		View line_1 = (View)findViewById(R.id.line_1);
		
		//是否有title
		if(hasTitle){
			ll_top.setVisibility(View.VISIBLE);
			line_1.setVisibility(View.VISIBLE);
		}else{
			ll_top.setVisibility(View.GONE);
			line_1.setVisibility(View.GONE);
		}
		TextView titleTextView = (TextView)findViewById(R.id.dialog_title);
		titleTextView.setText(title);
		TextView messageTextView = (TextView)findViewById(R.id.dialog_message);
		messageTextView.setText(message);

		// 设置按钮事件监听
		tv_ok = (TextView)findViewById(R.id.tv_ok);
		tv_cancle = (TextView)findViewById(R.id.tv_cancle);
		tv_cancle.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		tv_ok.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		titleTextView.setTextColor(getContext().getResources().getColor(MyApplication.getApplication().pf.title_bg));
		tv_ok.setTextColor(getContext().getResources().getColor(MyApplication.getApplication().pf.title_bg));
		if(nameOkButton != null && nameOkButton.length()>0){
			tv_ok.setText(nameOkButton);
			tv_ok.setOnClickListener(Gettv_okOnClickListener());
		} 
		if(nameCancelButton != null && nameCancelButton.length()>0){
			tv_cancle.setText(nameCancelButton);
			tv_cancle.setOnClickListener(Gettv_cancleOnClickListener());
		} 
		
		// 设置对话框的位置和大小
		LayoutParams params = this.getWindow().getAttributes();  
		if(this.getWidth()>0)
			params.width = this.getWidth();  
		if(this.getHeight()>0)
			params.height = this.getHeight();  
		if(this.getX()>0)
			params.width = this.getX();  
		if(this.getY()>0)
			params.height = this.getY();  
		
		// 如果设置为全屏
		if(isFullScreen) {
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
		}
		
		//设置点击dialog外部区域可取消
		if(isCancel){
			setCanceledOnTouchOutside(true);
			setCancelable(true);
		}else{
			setCanceledOnTouchOutside(false);
			setCancelable(false);
		}
	    getWindow().setAttributes(params);  
		this.setOnDismissListener(GetOnDismissListener());
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 * 获取OnDismiss事件监听，释放资源
	 * @return OnDismiss事件监听
	 */
	protected OnDismissListener GetOnDismissListener() {
		return new OnDismissListener(){
			public void onDismiss(DialogInterface arg0) {
				DialogBase.this.onDismiss();
				DialogBase.this.setOnDismissListener(null);
				view = null;
				mainContext = null;
				tv_ok = null;
				tv_cancle = null;
				if(onDismissListener != null){
					onDismissListener.onDismiss(null);
				}
			}			
		};
	}

	/**
	 * 获取确认按钮单击事件监听
	 * @return 确认按钮单击事件监听
	 */
	protected View.OnClickListener Gettv_okOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if(OnClicktv_ok())
					DialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 * 获取取消按钮单击事件监听
	 * @return 取消按钮单击事件监听
	 */
	protected View.OnClickListener Gettv_cancleOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				OnClicktv_cancle();
				DialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 * 获取焦点改变事件监听，设置EditText文本默认全选
	 * @return 焦点改变事件监听
	 */
	protected OnFocusChangeListener GetOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v instanceof EditText) {
					((EditText) v).setSelection(0, ((EditText) v).getText().length());
				}
			}
		};
	}
	
	/**
	 * 设置成功事件监听，用于提供给调用者的回调函数
	 * @param listener 成功事件监听
	 */
	public void SetOnSuccessListener(OnClickListener listener){
		onSuccessListener = listener;
	}
	
	/**
	 * 设置关闭事件监听，用于提供给调用者的回调函数
	 * @param listener 关闭事件监听
	 */
	public void SetOnDismissListener(OnDismissListener listener){
		onDismissListener = listener;
	}

	/**提供给取消按钮，用于实现类定制
	 * @param listener
	 */
	public void SetOnCancelListener(OnClickListener listener){
		onCancelListener = listener;
	}
	
	/**
	 * 创建方法，用于子类定制创建过程
	 */
	protected abstract void onBuilding();

	/**
	 * 确认按钮单击方法，用于子类定制
	 */
	protected abstract boolean OnClicktv_ok();

	/**
	 * 取消按钮单击方法，用于子类定制
	 */
	protected abstract void OnClicktv_cancle();

	/**
	 * 关闭方法，用于子类定制
	 */
	protected abstract void onDismiss();

	/**
	 * @return 对话框标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title 对话框标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param iconTitle 标题图标的资源Id
	 */
	public void setIconTitle(int iconTitle) {
		this.iconTitle = iconTitle;
	}

	/**
	 * @return 标题图标的资源Id
	 */
	public int getIconTitle() {
		return iconTitle;
	}

	/**
	 * @return 对话框提示信息
	 */
	protected String getMessage() {
		return message;
	}

	/**
	 * @param message 对话框提示信息
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return 对话框View
	 */
	protected View getView() {
		return view;
	}

	/**
	 * @param view 对话框View
	 */
	protected void setView(View view) {
		this.view = view;
	}

	/**
	 * @return 是否全屏
	 */
	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	/**
	 * @param isFullScreen 是否全屏
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}


	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	
	/**
	 * @return 对话框宽度
	 */
	protected int getWidth() {
		return width;
	}

	/**
	 * @param width 对话框宽度
	 */
	protected void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return 对话框高度
	 */
	protected int getHeight() {
		return height;
	}

	/**
	 * @param height 对话框高度
	 */
	protected void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return 对话框X坐标
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x 对话框X坐标
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return 对话框Y坐标
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y 对话框Y坐标
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @param nameOkButton 确认按钮名称
	 */
	protected void setNameOKButton(String nameOkButton) {
		this.nameOkButton = nameOkButton;
	}

	/**
	 * @param nameCancelButton 取消按钮名称
	 */
	protected void setNameCancelButton(String nameCancelButton) {
		this.nameCancelButton = nameCancelButton;
	}
}