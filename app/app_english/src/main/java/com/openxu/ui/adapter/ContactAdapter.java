package com.openxu.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.ui.ActivityContact;
import com.openxu.ui.ActivityFirendInfo;
import com.openxu.ui.MyApplication;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {
	private List<ChatUser> list = null;
	private ActivityContact mContext;

	public ContactAdapter(ActivityContact mContext, List<ChatUser> list) {
		this.mContext = mContext;
		if (list == null)
			list = new ArrayList<ChatUser>();
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<ChatUser> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		if (list != null)
			return this.list.size();
		return 0;
	}

	public Object getItem(int position) {
		if (list != null)
			return list.get(position);
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View view, ViewGroup arg2) {
		final ChatUser friend = list.get(position);
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_user_friend, null);
			holder = new Holder();
			holder.tv_alpha = (TextView) view.findViewById(R.id.tv_alpha);
			holder.name = (TextView) view.findViewById(R.id.tv_friend_name);
			holder.avatar = (ImageView) view.findViewById(R.id.img_friend_avatar);
			holder.ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
			holder.ll_content.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		final String name = friend.getUsername();
		final String avatar = friend.getAvatar();
		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, holder.avatar);
		} else {
			holder.avatar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.open_user_icon_def));
		}
		holder.name.setText(name);

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tv_alpha.setVisibility(View.VISIBLE);
			holder.tv_alpha.setText(friend.getSortLetters());
		} else {
			holder.tv_alpha.setVisibility(View.GONE);
		}
		
		holder.ll_content.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mContext.showDeleteDialog(friend);
				return true;
			}
		});
		holder.ll_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//先进入好友的详细资料页面
				Intent intent =new Intent(mContext,ActivityFirendInfo.class);
				intent.putExtra("from", "add");
				intent.putExtra("action", 1);
				intent.putExtra("user", friend);
				mContext.startActivity(intent);
			}
		});

		return view;
	}

	static class Holder {
		LinearLayout ll_content;
		TextView tv_alpha;// 首字母提示
		ImageView avatar;
		TextView name;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if (TextUtils.isEmpty(list.get(position).getSortLetters()))
			return '#';
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr;
			// 没有排序字母的
			if (TextUtils.isEmpty(list.get(i).getSortLetters()))
				sortStr = "#";
			else
				sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}