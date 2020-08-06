package com.openxu.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

import com.openxu.db.bean.User;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.view.TitleView;

/**
 * @author openXu
 */
public class ActivityRegist extends BaseActivity {

	protected TitleView titleView;
	private EditText et_name, et_pasw1;
	private UserDaoImpl dao;
	private ImageView iv_check_nan, iv_check_nv;
	private int sex;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_regist);
		et_name = (EditText) findViewById(R.id.et_name);
		et_pasw1 = (EditText) findViewById(R.id.et_pasw1);
		iv_check_nan = (ImageView) findViewById(R.id.iv_check_nan);
		iv_check_nv = (ImageView) findViewById(R.id.iv_check_nv);
		iv_check_nan.setOnClickListener(this);
		iv_check_nv.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		dao = new UserDaoImpl();
	}

	@Override
	protected void setPf() {
		super.setPf();
		findViewById(R.id.input).setBackgroundResource(MyApplication.pf.regist_edt_bg);
		((ImageView)findViewById(R.id.iv_line1)).setImageResource(MyApplication.pf.regist_line_color);
		((ImageView)findViewById(R.id.iv_line2)).setImageResource(MyApplication.pf.regist_line_color);
		findViewById(R.id.btn_regist).setBackgroundResource(MyApplication.pf.regist_btn_selector);
	}

	@Override
	public void onClick(View v) {
		iv_check_nan.setImageResource(R.drawable.open_hf_icon_check_no);
		iv_check_nv.setImageResource(R.drawable.open_hf_icon_check_no);
		switch (v.getId()) {
		case R.id.iv_check_nan:
			sex=1;
			iv_check_nan.setImageResource(R.drawable.open_hf_icon_check_yes);
			break;
		case R.id.iv_check_nv:
			sex=2;
			iv_check_nv.setImageResource(R.drawable.open_hf_icon_check_yes);
			break;
		}
	}
	
	public void regist(View v) {
		final String name = et_name.getText().toString().trim();
		final String pasw1 = et_pasw1.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			MyUtil.showToast(mContext, -1, "请输入用户名");
			return;
		}
		if (TextUtils.isEmpty(pasw1)) {
			MyUtil.showToast(mContext, -1, "请输入密码");
			return;
		}
		if(sex==0){
			MyUtil.showToast(mContext, -1, "请选择性别");
			return;
		}
		
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive())
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		dialog.setShowText("正在注册...");
		dialog.show();
		MyApplication.user = new User();
		MyApplication.user.setUsername(name);
		MyApplication.user.setPs(pasw1);
		MyApplication.user.setChatName(name);   //聊天账户
		MyApplication.user.setChatPsw(pasw1);
		MyApplication.user.setIcon("");
		MyApplication.user.setSex(sex==1?true : false);
		MyApplication.user.setSexset(1);  //设置了性别
		MyApplication.user.setJingyan(MyApplication.property.getLocalJy() + Constant.REWARD_JY_REGIST); // 注册加100
		/*
		 * if(!TextUtils.isEmpty(mail)){ MyApplication.user.setEmail(mail);
		 * MyApplication
		 * .user.setJingyan(MyApplication.user.getJingyan()+Constant.JY_MAIL);
		 * //注册加100 }
		 */
		
		//将user和设备id进行绑定aa
		MyApplication.user.setDeviceType("android");
		MyApplication.user.setInstallId(BmobInstallation.getInstallationId(mContext));
		
		// 注意：不能用save方法进行注册
		MyApplication.user.signUp(this, new SaveListener() {
			@Override
			public void onSuccess() {
				MyUtil.LOG_V(TAG, "注册账号成功");
				dialog.cancel();
				// 将设备与username进行绑定
				BmobUserManager.getInstance(mContext).bindInstallationForRegister(MyApplication.user.getChatName());
				//更新地理位置信息
				updateUserLocation();
				dao.deleteAll();
				int id = (int) dao.insert(MyApplication.user);
				MyApplication.user.setId(id);
				Intent data = new Intent();
				data.putExtra("regist", true);
				setResult(RESULT_OK, data);
				//同步服务器经验值到本地
				MyApplication.property.rewardJy(MyApplication.user.getJingyan(), mContext, false);
				finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				dialog.cancel();
				MyApplication.user = null;
				MyUtil.LOG_E(TAG, "注册账号失败:" + msg);
				// 注册失败:username 'xx' already taken.
				if (msg.contains("already taken")) {
					MyUtil.showToast(mContext, -1, "该用户名已被注册");
					et_name.setText("");
				} else {
					MyUtil.showToast(mContext, -1, "注册失败:" + msg);
				}
			}
		});
	}
	
	

}
