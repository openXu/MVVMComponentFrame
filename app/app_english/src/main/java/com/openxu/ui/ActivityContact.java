package com.openxu.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.ui.adapter.ContactAdapter;
import com.openxu.utils.CollectionUtils;
import com.openxu.utils.MyUtil;
import com.openxu.view.dialog.DialogTips;
import com.openxu.view.sortview.CharacterParser;
import com.openxu.view.sortview.ContactComparator;
import com.openxu.view.sortview.SideBar;
import com.openxu.view.sortview.SideBar.OnTouchingLetterChangedListener;

/**
 * @author openXu 联系人界面
 */
public class ActivityContact extends BaseActivity{
	private ListView lv_cons;
	private SideBar sideBar;
	private TextView tv_pop;
	private ContactAdapter adapter;

	ImageView iv_msg_tips;
	TextView tv_new_name;
	LinearLayout layout_new;// 新朋友
	LinearLayout layout_near;// 附近的人

	List<ChatUser> friends = new ArrayList<ChatUser>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private ContactComparator comparator;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_contact);
		initListView();
	}

	@Override
	protected void setPf() {
		super.setPf();
		tv_pop.setBackgroundResource(MyApplication.getApplication().pf.title_bg);
	}

	private void initListView() {
		characterParser = CharacterParser.getInstance();
		comparator = new ContactComparator();

		sideBar = (SideBar) findViewById(R.id.sideBar);
		tv_pop = (TextView) findViewById(R.id.tv_pop);
		sideBar.setTextView(tv_pop);
		lv_cons = (ListView) findViewById(R.id.lv_cons);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					lv_cons.setSelection(position);
				}
			}
		});

		RelativeLayout headView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.include_new_friend, null);
		iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
		layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
		layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);
		layout_new.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		layout_near.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		layout_new.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, ActivityNewFriend.class);
				intent.putExtra("from", "contact");
				startActivity(intent);
			}
		});
		layout_near.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, ActivityNearPeople.class);
				startActivity(intent);
			}
		});

		lv_cons.addHeaderView(headView);
		adapter = new ContactAdapter(this, friends);
		lv_cons.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
		queryMyfriends();
	}
	public void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	@Override
	protected void initData() {
	}
	@Override
	protected boolean onMenuSelected() {
		//添加好友
		startActivity(new Intent(mContext, ActivitySearchUser.class));
		return true;
	}
	/** 获取好友列表
	  * queryMyfriends
	  * @return void
	  * @throws
	  */
	private void queryMyfriends() {
		//是否有新的好友请求
		if(BmobDB.create(this).hasNewInvite()){
			iv_msg_tips.setVisibility(View.VISIBLE);
		}else{
			iv_msg_tips.setVisibility(View.GONE);
		}
		//在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		MyApplication.getApplication().setContactList(CollectionUtils.list2map(BmobDB.create(this).getContactList()));
	
		Map<String,BmobChatUser> users = MyApplication.getApplication().getContactList();
		//组装新的User
		filledData(CollectionUtils.map2list(users));
		if (adapter == null) {
			adapter = new ContactAdapter(this, friends);
			lv_cons.setAdapter(adapter);
		} else {
			adapter.updateListView(friends);
		}

	}
	public void showDeleteDialog(final ChatUser user) {
		DialogTips dialog = new DialogTips(mContext,user.getUsername(),"删除联系人", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteContact(user);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	 /** 删除联系人
	  * deleteContact
	  * @return void
	  * @throws
	  */
	private void deleteContact(final ChatUser user){
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {
			@Override
			public void onSuccess() {
				MyUtil.showToast(mContext, -1, "删除成功");
				//删除内存
				MyApplication.getApplication().getContactList().remove(user.getUsername());
				//更新界面
				ActivityContact.this.runOnUiThread(new Runnable() {
					public void run() {
						friends.remove(user);
						adapter.updateListView(friends);
					}
				});
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MyUtil.showToast(mContext, -1, "删除失败："+arg1);
			}
		});
	}
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			ChatUser sortModel = new ChatUser();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getUsername();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, comparator);
	}

	
	@Override
	public void onAddUser(BmobInvitation message) {
		super.onAddUser(message);
		iv_msg_tips.setVisibility(View.VISIBLE);
		//同时提醒通知
		String tickerText = message.getFromname()+"请求添加好友";
		boolean isAllowVibrate = MyApplication.property.vibrate;
		BmobNotifyManager.getInstance(this).showNotify(MyApplication.property.voice,
				isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),ActivityNewFriend.class);
	}
}
