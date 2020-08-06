package com.openxu.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.ui.adapter.MessageRecentAdapter;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.MyUtil;
import com.openxu.view.ClearEditText;
import com.openxu.view.dialog.DialogTips;

/**
 * 聊天界面
 * 
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 下午4:33:11
 */
public class ChatActivityRecent extends BaseActivity implements
		OnItemClickListener, OnItemLongClickListener {

	public BmobUserManager userManager;
	public BmobChatManager manager;

	private ClearEditText mClearEditText;
	private ListView listview;
	private MessageRecentAdapter adapter;

	private PopupWindow popupWindow_friends;
	private ImageView iv_new_tips;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_chat_recent);
		listview = (ListView) findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(mContext,
				R.layout.item_chat_conversation, BmobDB.create(mContext)
						.queryRecents());
		listview.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
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

	protected void initData() {
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 弹出PopupWindow的具体代码
		showCommandPopupwindow();
	}
	@Override
	public void onResume() {
		super.onResume();
		//是否有新的好友请求
		if(iv_new_tips!=null){
			if(BmobDB.create(this).hasNewInvite()){
				iv_new_tips.setVisibility(View.VISIBLE);
			}else{
				iv_new_tips.setVisibility(View.GONE);
			}
		}
		
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
		refresh();
	}
	public void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	@Override
	public void onMessage(BmobMsg message) {
		super.onMessage(message);
		MyUtil.LOG_V(TAG, "聊天列表收到消息：" + message);
		refresh();
	}
	@Override
	public void onAddUser(BmobInvitation message) {
		super.onAddUser(message);
		//是否有新的好友请求
		if(iv_new_tips!=null){
			if(BmobDB.create(this).hasNewInvite()){
				iv_new_tips.setVisibility(View.VISIBLE);
			}else{
				iv_new_tips.setVisibility(View.GONE);
			}
		}

		//同时提醒通知
		String tickerText = message.getFromname()+"请求添加好友";
		boolean isAllowVibrate = MyApplication.property.vibrate;
		BmobNotifyManager.getInstance(this).showNotify(MyApplication.property.voice,
				isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),ActivityNewFriend.class);
	
	}
	/**
	 * 删除会话 deleteRecent
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteRecent(BmobRecent recent) {
		adapter.remove(recent);
		BmobDB.create(mContext).deleteRecent(recent.getTargetid());
		BmobDB.create(mContext).deleteMessages(recent.getTargetid());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(mContext, recent.getUserName(),"删除会话", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		BmobRecent recent = adapter.getItem(position);
		// 重置未读消息
		BmobDB.create(mContext).resetUnread(recent.getTargetid());
		// 组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(mContext, ChatActivity.class);
		intent.putExtra("user", user);
		intent.putExtra("action", getIntent().getIntExtra("action", 1));
		startActivity(intent);
	}

	public void refresh() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(mContext,R.layout.item_chat_conversation, BmobDB.create(mContext).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isClick = true;
	private void showCommandPopupwindow() {
		if(popupWindow_friends == null){
			View popupView = View.inflate(mContext,R.layout.popup_friends, null);
			ImageView iv_friends = (ImageView) popupView.findViewById(R.id.iv_friends);
			// 构造一个悬浮窗体：显示的内容，窗体的宽高
			popupWindow_friends = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupWindow_friends.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			iv_new_tips = (ImageView) popupView.findViewById(R.id.iv_new_tips);
			//是否有新的好友请求
			if(BmobDB.create(this).hasNewInvite()){
				iv_new_tips.setVisibility(View.VISIBLE);
			}else{
				iv_new_tips.setVisibility(View.GONE);
			}
			
			popupView.setBackgroundResource(MyApplication.pf.pop_friend);
			popupView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MyApplication.user == null) {
						MyUtil.showToast(mContext, -1, "请先登录账号才能使用哦");
					} else {
						startActivity(new Intent(mContext, ActivityContact.class));
						overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
					}					
				}
			});
			popupView.setOnTouchListener(new OnTouchListener() {
				int rawX, rawY;
				int orgX, orgY;
				int offsetX, offsetY;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					 switch (event.getAction()) {
				        case MotionEvent.ACTION_DOWN:
				        	isClick = true;
				        	rawX = (int) event.getRawX();
				        	rawY = (int) event.getRawY();
				        	orgX = (int) event.getX();
							orgY = (int) event.getY();
							MyUtil.LOG_I(TAG, "按下x:"+rawX+"  y:"+rawY);
				            return false;
				        case MotionEvent.ACTION_MOVE:
							offsetX = (int) event.getRawX() - orgX;
							offsetY = (int) event.getRawY() - orgY;
							popupWindow_friends.update(offsetX, offsetY, -1, -1, true);
							
							int dX = (int) event.getRawX() - rawX;
							int dY = (int) event.getRawY() - rawY;
							MyUtil.LOG_I(TAG, "移动了x:"+dX+"  Y:"+dY);
							if(Math.abs(dX)>5 || Math.abs(dY)>5)
								isClick = false;
							return false;
				        case MotionEvent.ACTION_UP:
				            return !isClick;
				            default:
				            return false;
				        }   
				}
			});
		}
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int hight = display.getHeight();
		int px = DensityUtil.dip2px(getApplicationContext(), 60);
		int py = DensityUtil.dip2px(getApplicationContext(), 60);

		// 让悬浮窗体显示（显示的父窗体就是ListView；重心；显示的xy坐标）
		popupWindow_friends.showAtLocation(rootView,Gravity.TOP + Gravity.LEFT, width - px- 5, hight/2-py*2);
		
	}

	@Override
	protected void onDestroy() {
		if(popupWindow_friends!=null){
			popupWindow_friends.dismiss();
			popupWindow_friends = null;
		}
		super.onDestroy();
	}
}
