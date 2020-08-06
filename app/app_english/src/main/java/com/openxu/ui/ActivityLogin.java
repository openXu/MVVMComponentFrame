package com.openxu.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.openxu.db.bean.User;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;

/**
 * @author openXu
 */
public class ActivityLogin extends BaseActivity{
	private EditText et_name, et_pasw;
	private TextView tv_wgmm;
	private Button login_btn;
	private UserDaoImpl dao;
	private int action;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		et_name = (EditText) findViewById(R.id.et_name);
		et_pasw = (EditText) findViewById(R.id.et_pasw);
		tv_wgmm = (TextView) findViewById(R.id.tv_wgmm);
		login_btn = (Button) findViewById(R.id.login_btn);
	}
	
	@Override
	protected void setPf() {
		login_btn.setTextColor(getResources().getColor(MyApplication.pf.login_btn_text));
		findViewById(R.id.rootView).setBackgroundResource(MyApplication.pf.login_bg);  //登录界面背景
	}
	
	@Override
	protected void initData() {
		action = getIntent().getIntExtra("action", 0);
		dao = new UserDaoImpl();
		List<User> users = dao.findAll();
		MyUtil.LOG_V(TAG, "数据库查找用户："+users.size());
		if(users!=null&&users.size()>0){
			MyApplication.user = users.get(0);
			for(User user : users){
				MyUtil.LOG_V(TAG, "数据库查找用户："+user);
			}
			et_name.setText(MyApplication.user.getUsername());
			et_pasw.setText(MyApplication.user.getPs());
			//密码不为空，说明注册后没有卸载过，不需要登录
			if(!TextUtils.isEmpty(MyApplication.user.getPs())){
				finish();
			}
//			login();
		}
	}
	
	private void login() {
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		dialog.setShowText("正在登陆...");
		dialog.show();
		MyApplication.user.login(this, new SaveListener() {
		    @Override
		    public void onSuccess() {
		    	MyUtil.LOG_V(TAG, "登录成功" );
		    	// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
				updateUserInfos();
	    		BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("username", MyApplication.user.getUsername());
				query.findObjects(mContext, new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						MyUtil.LOG_V(TAG, "获取服务器用户信息：" + object.size());
						BmobUserManager.getInstance(mContext).bindInstallationForRegister(MyApplication.user.getChatName());
						//更新
						User user = object.get(0);
						MyUtil.LOG_V(TAG, "获取服务器用户信息：" + user);
						user.setPassword(MyApplication.user.getPs());   //从服务器查询的用户密码为空
						dao.deleteAll();
						dao.insert(user);
						MyUtil.LOG_V(TAG, "更新本地用户信息：" + user);
						MyApplication.user = user;
						//同步服务器经验值到本地
						MyApplication.property.rewardJy(user.getJingyan(), mContext, false);
						finish();
					}

					@Override
					public void onError(int code, String msg) {
						MyUtil.showToast(mContext, -1, "查询用户失败："+msg);
						MyUtil.LOG_E(TAG, "查询用户失败：" + msg);
					}
				});
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		    	dialog.cancel();
		    	MyApplication.user = null;
		    	MyUtil.showToast(mContext, -1, "登录失败:"+msg);
		    }
		});
	}
	
	public void login(View v){
		String name = et_name.getText().toString().trim();
		String pasw = et_pasw.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			MyUtil.showToast(mContext, -1, "请输入用户名");
			return;
		}
		if(TextUtils.isEmpty(pasw)){
			MyUtil.showToast(mContext, -1, "请输入密码");
			return;
		}
		InputMethodManager imm = (InputMethodManager )v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);     
        if (imm.isActive())     
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken() , 0 );   
        if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
        if(MyApplication.user==null){
        	MyApplication.user = new User();
        }
        MyApplication.user.setUsername(name);
        MyApplication.user.setPs(pasw);
		login();
	}
	
	//注册
	public void regist(View v){
		startActivityForResult(new Intent(mContext, ActivityRegist.class), 1);
	}
	public void zhaohuimima(View v){
		startActivity(new Intent(mContext, ActivityZHMM.class));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			boolean regist = data.getBooleanExtra("regist", false);
			//注册成功
			if(regist && MyApplication.user!=null && MyApplication.user.getId()>0){
				finish();
			}
		}
	}
	
	
	
	
}
