package com.openxu.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;

public class ChangeUserInfoDialog extends ItotemBaseDialog {
	private Listener listener;
	private TextView tv_title, tv_cancle, tv_ok;
	private EditText et1, et2;
	public int action;
	public static final int ACTION_NAME = 1;
	public static final int ACTION_PASW = 2;
	public static final int ACTION_EMAIL = 3;
	public static final int ACTION_PHONE = 4;
	

	public ChangeUserInfoDialog(Context context) {
		super(context, R.layout.dialog_userinfo_change, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		et1 = (EditText) findViewById(R.id.et1);
		et2 = (EditText) findViewById(R.id.et2);
		tv_title.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_ok.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_ok.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancle.setBackgroundResource(MyApplication.pf.item_selector);
		et1.setBackgroundResource(MyApplication.pf.regist_edt_bg);
		et2.setBackgroundResource(MyApplication.pf.regist_edt_bg);
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) v.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				cancel();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!NetWorkUtil.isNetworkAvailable(context)) {
					MyUtil.showToast(context, R.string.no_net, "");
					return;
				}
				String str1 = et1.getText().toString().trim();
				String str2 = et2.getText().toString().trim();
				switch (action) {
				case ACTION_NAME:
					if(TextUtils.isEmpty(str1)){
						MyUtil.showToast(context, -1, "请输入昵称");
						return;
					}
					break;
				case ACTION_PASW:
	
					break;
				case ACTION_EMAIL:
					if(TextUtils.isEmpty(str1)){
						MyUtil.showToast(context, -1, "请输入邮箱");
						return;
					}
					if(!MyUtil.isMail(str1)){
						MyUtil.showToast(context, -1, "请输入正确的邮箱号");
						return;
					}
					break;
				case ACTION_PHONE:
					if(TextUtils.isEmpty(str1)){
						MyUtil.showToast(context, -1, "请输入手机号码");
						return;
					}
					if(!MyUtil.isPhone(str1)){
						MyUtil.showToast(context, -1, "请输入正确的手机号码");
						return;
					}
					break;
				default:
					break;
				}
				InputMethodManager imm = (InputMethodManager) et1.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
					imm.hideSoftInputFromWindow(et1.getApplicationWindowToken(), 0);
				listener.ok(str1);
				cancel();
			}
		});
	}
	public void setAction(int action){
		this.action = action;
		switch (action) {
		case ACTION_NAME:
			et1.setVisibility(View.VISIBLE);
			et2.setVisibility(View.GONE);
			et1.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			tv_title.setText("修改昵称");	
			et1.setHint("请输入用户名");
			et1.setText(MyApplication.user.getUsername());
			break;
		case ACTION_PASW:
			et1.setVisibility(View.VISIBLE);
			et2.setVisibility(View.VISIBLE);
			tv_title.setText("修改密码");	
			et1.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			et1.setHint("旧密码");
			et2.setHint("新密码");
			break;
		case ACTION_EMAIL:
			et1.setVisibility(View.VISIBLE);
			et2.setVisibility(View.GONE);
			et1.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			tv_title.setText("绑定邮箱");	
			et1.setHint("请输入邮箱");
			et1.setText(MyApplication.user.getEmail());
			break;
		case ACTION_PHONE:
			et1.setVisibility(View.VISIBLE);
			et2.setVisibility(View.GONE);
			et1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tv_title.setText("绑定手机号");	
			et1.setHint("请输入手机号");
			et1.setText(MyApplication.user.getMobilePhoneNumber());
			break;
		default:
			break;
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {

	}

	public interface Listener {
		public abstract void ok(String text);
	}

}
