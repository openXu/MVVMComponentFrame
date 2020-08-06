package com.openxu.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

import com.openxu.english.R;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;

/**
 * @author openXu
 */
public class ActivityZHMM extends BaseActivity {

	private LinearLayout ll2, ll3;
	private EditText et_1, et_2, et_3;
	private TextView tv_result;
	private Button btn_zhmm;
	private int state = 0;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_zhmm);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		et_1 = (EditText) findViewById(R.id.et_1);
		et_2 = (EditText) findViewById(R.id.et_2);
		et_3 = (EditText) findViewById(R.id.et_3);
		tv_result = (TextView) findViewById(R.id.tv_result);
		btn_zhmm = (Button) findViewById(R.id.btn_zhmm);
		tv_result.setVisibility(View.GONE);
		ll2.setVisibility(View.GONE);
		ll3.setVisibility(View.GONE);
		et_1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btn_zhmm.setText("找回密码");
				String text = et_1.getText().toString().trim();
				if(!TextUtils.isEmpty(text)&&text.equalsIgnoreCase(phone)&&getCodeState!=0){   //与已经发送过的号码一致
					js = false;
				}else{
					tv_result.setVisibility(View.GONE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		et_2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(et_2.getText().toString().trim()))
					btn_zhmm.setText("重置密码");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setPf() {
		super.setPf();
		findViewById(R.id.ll1).setBackgroundResource(MyApplication.pf.regist_edt_bg);
		findViewById(R.id.ll2).setBackgroundResource(MyApplication.pf.regist_edt_bg);
		findViewById(R.id.ll3).setBackgroundResource(MyApplication.pf.regist_edt_bg);
		findViewById(R.id.btn_zhmm).setBackgroundResource(MyApplication.pf.regist_btn_selector);
	}

	public void zhmm(View v) {
		final String text = et_1.getText().toString().trim();
		if (TextUtils.isEmpty(text)) {
			MyUtil.showToast(mContext, -1, "请输入您绑定的邮箱或者手机号");
			return;
		}
		if(MyUtil.isMail(text)){
			byEmail(text);
		}else if(MyUtil.isPhone(text)){
			if(getCodeState==1){
			}else if(getCodeState==2){
				czmm();
			}else if(getCodeState==0){
				getCode(text);
			}
		}else{
			MyUtil.showToast(mContext, -1, "请输入正确的手机号或者邮箱");
			return;
		}
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive())
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	/**
	 * 密码重置流程如下：
	 * 用户输入他们的电子邮件，请求重置自己的密码。
	 * Bmob向他们的邮箱发送一封包含特殊的密码重置链接的电子邮件。
	 * 用户根据向导点击重置密码连接，打开一个特殊的Bmob页面，根据提示他们可以输入一个新的密码。
	 * 用户的密码已被重置为新输入的密码。
	 */
	public void byEmail(String email){
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		ll2.setVisibility(View.GONE);
		ll3.setVisibility(View.GONE);
		tv_result.setVisibility(View.GONE);
		dialog.setShowText("请稍候...");
		dialog.show();
		state = 1; 
        BmobUser.resetPasswordByEmail(mContext, email, new ResetPasswordByEmailListener() {
			@Override
			public void onSuccess() {
				tv_result.setText("验证邮件已发送，请打开邮箱重置密码");
				tv_result.setVisibility(View.VISIBLE);
				dialog.cancel();
			}
			@Override
			public void onFailure(int arg0, String arg1) {
				tv_result.setText("验证邮件发送失败"+arg1);
				tv_result.setVisibility(View.VISIBLE);
				MyUtil.showToast(mContext, -1, "验证邮件发送失败"+arg1);
				dialog.cancel();
			}
		});
	}
	
	//①、获取验证码
	private boolean js = true;
	private int ms = 30;
	private int getCodeState = 0;  //0没有获取，1正在获取，2获取成功
	private Timer timer;
	private String phone;
	private void getCode(final String phone){
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		this.phone = phone;
		getCodeState = 1;
		btn_zhmm.setText(ms+"s后重新获取");
		js = true;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ms -= 1;
				if(ms>0){
					runOnUiThread(new Runnable() {
						public void run() {
							if(js)
								btn_zhmm.setText(ms+"s后重新获取");
						}
					});
				}else{
					runOnUiThread(new Runnable() {
						public void run() {
							btn_zhmm.setText("获取验证码");
						}
					});
					getCodeState = 0;
					ms = 30;
					timer.cancel();
					return;
				}
			}
		}, 1000, 1000);
		ll2.setVisibility(View.VISIBLE);
		ll3.setVisibility(View.VISIBLE);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				BmobSMS.requestSMSCode(mContext, phone, "找回密码",
						new RequestSMSCodeListener() {
							@Override
							public void done(Integer arg0, BmobException arg1) {
								if (arg1 == null) {// 验证码发送成功
									getCodeState = 2;
									MyUtil.LOG_V(TAG, "获取验证码：短信id："+arg0);//用于查询本次短信发送详情
									tv_result.setText("验证码已经发送至您的信箱");
									tv_result.setVisibility(View.VISIBLE);
								}else{
									MyUtil.LOG_V(TAG, "获取验证码失败："+arg1.getMessage());//用于查询本次短信发送详情
						        	if(arg1.getMessage().contains("limit")){
						        		MyUtil.showToast(mContext, -1, "此手机号码已超次数，请更换手机号");
						        	}else{
						        		MyUtil.showToast(mContext, -1, arg1.getMessage());
						        	}
								}
								dialog.cancel();
							}
						});
			}
		}, 3000);
	}
	
	private void czmm(){
		final String code = et_2.getText().toString().trim();
		final String pasw = et_3.getText().toString().trim();
		if(TextUtils.isEmpty(code)){
			MyUtil.showToast(mContext, -1, "请输入验证码");
			return;
		}
		if(TextUtils.isEmpty(pasw)){
			MyUtil.showToast(mContext, -1, "请输入新密码");
			return;
		}
		BmobUser.resetPasswordBySMSCode(this, code, pasw, new ResetPasswordByCodeListener() {
		    @Override
		    public void done(BmobException ex) {
		        if(ex==null){
		        	MyUtil.showToast(mContext, -1, "密码重置成功");
		        	MyUtil.LOG_V(TAG, "密码重置成功");
		        	finish();
		        }else{
		        	MyUtil.showToast(mContext, -1, "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
		        	MyUtil.LOG_E(TAG, "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
		        }
		    }
		});
	}
	
}
