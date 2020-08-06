package com.openxu.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;

/**
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午2:55:19
 */
public class ActivityFirendInfo extends BaseActivity implements OnClickListener {

	TextView tv_set_nick, tv_set_gender;
	ImageView iv_set_avator;
	LinearLayout layout_all;

	Button btn_chat, btn_add_friend;

	String from;
	int action;
	String username;
	ChatUser user;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_firend_info);
		from = getIntent().getStringExtra("from");   //me add other  从哪个页面进入
		action = getIntent().getIntExtra("action", 0);
		if(action==1){
			user = (ChatUser) getIntent().getSerializableExtra("user");
			username = user.getUsername();
		}else{
			username = getIntent().getStringExtra("username");
		}
		
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend);

		//不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
		btn_chat.setVisibility(View.VISIBLE);
		btn_chat.setOnClickListener(this);
		if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
			if (mApplication.getContactList().containsKey(username)) {// 是好友
				btn_add_friend.setVisibility(View.GONE);
			} else {
				btn_add_friend.setVisibility(View.VISIBLE);
				btn_add_friend.setOnClickListener(this);
			}
		} else {// 查看他人
		}
		//自己
		if(username.equals(MyApplication.user.getUsername())){
			btn_add_friend.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
		}
	}

	@Override
	protected void setPf() {
		super.setPf();
		btn_add_friend.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
		btn_chat.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
	}
	@Override
	protected void initData() {
		if(action==1){
			showData();
		}else{
			userManager.queryUser(username, new FindListener<ChatUser>() {
				@Override
				public void onError(int arg0, String arg1) {
					MyUtil.LOG_E(TAG, "查询用户信息失败:" + arg1);
				}
				@Override
				public void onSuccess(List<ChatUser> arg0) {
					if (arg0 != null && arg0.size() > 0) {
						user = arg0.get(0);
						showData();
					} else {
						MyUtil.LOG_E(TAG, "查询用户信息失败:onSuccess 查无此人");
					}
				}
			});
		}
	
	}
	private void  showData(){
		if(user!=null){
			String avatar = user.getAvatar();
			if (avatar != null && !avatar.equals("")) {
				ImageLoader.getInstance().displayImage(avatar, iv_set_avator);
			} else {
				iv_set_avator.setImageResource(R.drawable.open_user_icon_def);
			}
			boolean nan = user.getSex();
			tv_set_gender.setText(nan?"男":"女");
			tv_set_nick.setText(user.getUsername());
		}
		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chat:// 发起聊天
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			intent.putExtra("action", 1);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_add_friend://添加好友
			addFriend();
			break;
		}
	}
	
	/**
	 * 添加好友请求
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
				user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						MyUtil.showToast(mContext, -1, "发送请求成功，等待对方验证");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						MyUtil.showToast(mContext, -1, "发送请求失败:" + arg1);
					}
				});
	}





}
