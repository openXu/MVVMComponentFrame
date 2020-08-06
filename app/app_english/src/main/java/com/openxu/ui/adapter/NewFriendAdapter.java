package com.openxu.ui.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.CollectionUtils;

/** 新的好友请求
  * @ClassName: NewFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-9 下午1:26:12
  */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {

	public NewFriendAdapter(Context context, List<BmobInvitation> list) {
		super(context, list);
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobInvitation msg = getList().get(arg0);
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		RelativeLayout rl_content = ViewHolder.get(convertView, R.id.rl_content);
		final TextView tv_add = ViewHolder.get(convertView, R.id.tv_add);
		String avatar = msg.getAvatar();
		rl_content.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar);
		} else {
			iv_avatar.setImageResource(R.drawable.open_user_icon_def);
		}

		int status = msg.getStatus();
		if(status==BmobConfig.INVITE_ADD_NO_VALIDATION||status==BmobConfig.INVITE_ADD_NO_VALI_RECEIVED){
			tv_add.setText("同意");
			tv_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_white));
			tv_add.setBackgroundResource(MyApplication.pf.btn_selector);
			tv_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					BmobLog.i("点击同意按钮:"+msg.getFromid());
					agressAdd(tv_add, msg);
				}
			});
		}else if(status==BmobConfig.INVITE_ADD_AGREE){
			tv_add.setText("已同意");
			tv_add.setBackgroundDrawable(null);
			tv_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
			tv_add.setEnabled(false);
		}
		name.setText(msg.getFromname());
		
		return convertView;
	}

	
	/**添加好友
	  * agressAdd
	  * @Title: agressAdd
	  * @Description: TODO
	  * @param @param btn_add
	  * @param @param msg 
	  * @return void
	  * @throws
	  */
	private void agressAdd(final TextView tv_add,final BmobInvitation msg){
		final ProgressDialog progress = new ProgressDialog(mContext);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		try {
			//同意添加好友
			BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {
				@Override
				public void onSuccess() {
					progress.dismiss();
					tv_add.setText("已同意");
					tv_add.setBackgroundDrawable(null);
					tv_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
					tv_add.setEnabled(false);
					//保存到application中方便比较
					MyApplication.getApplication().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));	
				}
				
				@Override
				public void onFailure(int arg0, final String arg1) {
					progress.dismiss();
					ShowToast("添加失败: " +arg1);
				}
			});
		} catch (final Exception e) {
			progress.dismiss();
			ShowToast("添加失败: " +e.getMessage());
		}
	}
}
