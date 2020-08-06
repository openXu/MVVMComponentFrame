package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.listener.FindListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;

/**
 * @author openXu 添加联系人
 */
public class ActivitySearchUser extends BaseActivity{
	private LinearLayout ll_search;
	private TextView tv_lable_search, tv_search_name;
	private ListView lv_cons;
	private EditText et_name;
	private ImageView tv_line;
	private ContactAdapter adapter;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_search_user);
		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		tv_lable_search = (TextView) findViewById(R.id.tv_lable_search);
		tv_search_name = (TextView) findViewById(R.id.tv_search_name);
		ll_search.setVisibility(View.GONE);
		et_name = (EditText) findViewById(R.id.et_name);
		tv_line = (ImageView) findViewById(R.id.tv_line);
		lv_cons = (ListView) findViewById(R.id.lv_cons);
		adapter = new ContactAdapter(this, null);
		lv_cons.setAdapter(adapter);
		
		ll_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = et_name.getText().toString().trim();
				searchUser(name);
			}
		});
	}

	@Override
	protected void setPf() {
		super.setPf();
		tv_line.setImageResource(MyApplication.getApplication().pf.title_bg);
		tv_lable_search.setTextColor(getResources().getColor(MyApplication.getApplication().pf.title_bg));
	}

	@Override
	protected void initData() {
		et_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = et_name.getText().toString().trim();
				if(TextUtils.isEmpty(name)){
					ll_search.setVisibility(View.GONE);
				}else{
					tv_search_name.setText(name);
					ll_search.setVisibility(View.VISIBLE);
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
	}
	
	private void searchUser(String name){
		userManager.queryUser(name, new FindListener<ChatUser>() {
			@Override
			public void onError(int arg0, String arg1) {
				MyUtil.LOG_E(TAG, "查询用户信息失败:" + arg1);
				MyUtil.showToast(mContext, -1, "用户不存在");
			}
			@Override
			public void onSuccess(List<ChatUser> arg0) {
				 if(arg0!=null && arg0.size()>0){
		               MyUtil.LOG_V(TAG, "查询到"+arg0.size()+"用户");
		               adapter.updateListView(arg0);
		            }else{
		            	MyUtil.showToast(mContext, -1, "用户不存在");
		            }
			}
		});
		
		/*userManager.queryUserByName(name, new FindListener<BmobChatUser>() {
	        @Override
	        public void onError(int arg0, String arg1) {
	        	MyUtil.showToast(mContext, -1, "用户不存在");
	        }

	        @Override
	        public void onSuccess(List<BmobChatUser> arg0) {
	            if(arg0!=null && arg0.size()>0){
	               MyUtil.LOG_V(TAG, "查询到"+arg0.size()+"用户");
	               adapter.updateListView(arg0);
	            }else{
	            	MyUtil.showToast(mContext, -1, "用户不存在");
	            }
	        }
	    });*/
	}
	class ContactAdapter extends BaseAdapter {
		private List<ChatUser> list = null;
		private Context mContext;

		public ContactAdapter(Context mContext, List<ChatUser> list) {
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
			final ChatUser user = list.get(position);
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
			final String name = user.getUsername();
			final String avatar = user.getAvatar();
			if (!TextUtils.isEmpty(avatar)) {
				ImageLoader.getInstance().displayImage(avatar, holder.avatar);
			} else {
				holder.avatar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.open_user_icon_def));
			}
			holder.name.setText(name);

			holder.tv_alpha.setVisibility(View.GONE);
			
			holder.ll_content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//先进入好友的详细资料页面
					Intent intent =new Intent(mContext,ActivityFirendInfo.class);
					intent.putExtra("from", "add");
					intent.putExtra("action", 1);
					intent.putExtra("user", user);
					mContext.startActivity(intent);
					
				}
			});

			return view;
		}

		class Holder {
			LinearLayout ll_content;
			TextView tv_alpha;// 首字母提示
			ImageView avatar;
			TextView name;
		}

	}
	
	
}
