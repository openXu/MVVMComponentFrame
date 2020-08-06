package com.openxu.ui;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.ui.adapter.NewFriendAdapter;
import com.openxu.view.dialog.DialogTips;

/** 新朋友
  * @ClassName: NewFriendActivity
  * @Description: TODO
  * @author smile
  * @date 2014-6-6 下午4:28:09
  */
public class ActivityNewFriend extends BaseActivity implements OnItemLongClickListener{
	
	ListView listview;
	
	NewFriendAdapter adapter;
	
	String from="";
	
	@Override
	protected void initView(){
		setContentView(R.layout.activity_new_friend);
		from = getIntent().getStringExtra("from");
		listview = (ListView)findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(this,BmobDB.create(this).queryBmobInviteList());
		listview.setAdapter(adapter);
		if(from==null){//若来自通知栏的点击，则定位到最后一条
			listview.setSelection(adapter.getCount());
		}
	}
	@Override
	protected void initData() {
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
		showDeleteDialog(position,invite);
		return true;
	}
	@Override
	public void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
	}
	public void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}
	@Override
	public void onAddUser(BmobInvitation message) {
		super.onAddUser(message);
		adapter.setList(BmobDB.create(this).queryBmobInviteList());
		adapter.notifyDataSetChanged();
	}
	public void showDeleteDialog(final int position,final BmobInvitation invite) {
		DialogTips dialog = new DialogTips(this,invite.getFromname(),"删除好友请求", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteInvite(position,invite);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	/** 
	 * 删除请求
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteInvite(int position, BmobInvitation invite){
		adapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
