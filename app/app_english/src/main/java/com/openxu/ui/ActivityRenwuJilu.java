package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.openxu.db.bean.RenwuJilu;
import com.openxu.db.impl.RenwuJiluDao;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;

/**
 * @author openXu
 */
public class ActivityRenwuJilu extends BaseActivity{
	
	private ListView pull_list;
	private MyAdapter adapter;
	
	private RenwuJiluDao dao;
	private List<RenwuJilu> jilus;
	private TextView tv_null;
	@Override
	protected void initView() {
		setContentView(R.layout.renwujilu_activity);
		TAG = "ActivityRenwuJilu";
		pull_list = (ListView) findViewById(R.id.pull_list);
		tv_null = (TextView) findViewById(R.id.tv_null);
		tv_null.setVisibility(View.GONE);
		adapter = new MyAdapter(mContext);
		pull_list.setAdapter(adapter);
	}
	@Override
	protected void setPf() {
		super.setPf();
	}
	@Override
	protected void onStart() {
		super.onStart();
		setData();
	}
	@Override
	protected void initData() {}
	
	private void setData(){

		
//		WordDaoImpl da = new WordDaoImpl();
//		List<Word> words = da.findByCondition(WordDBHelper.TABLE_WORD_REMENBER+" =?", new String[]{"1"} , null);
//		MyUtil.LOG_V(TAG, "单词个数："+words.size());
//		for(Word word : words)
//			MyUtil.LOG_V(TAG, "单词："+word);
		dialog.setShowText("正在加载数据...");
		dialog.show();
		new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				dao = new RenwuJiluDao();
				jilus = dao.getDateGroup();
				for(RenwuJilu jilu : jilus)
					MyUtil.LOG_V(TAG, "查询到记录："+jilu);
				return 1;
			}
			protected void onPostExecute(Integer result) {
				dialog.cancel();
				if(jilus==null||jilus.size()<=0){
					tv_null.setVisibility(View.VISIBLE);
					pull_list.setVisibility(View.GONE);
				}else{
					adapter.setData(jilus);
					pull_list.setSelection(jilus.size()-1);
				}
			};
		}.execute();
		
	
	}

	
	class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<RenwuJilu> jilus;
		
		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			jilus = new ArrayList<RenwuJilu>();
		}
		
		public void setData(List<RenwuJilu> jilus){
			this.jilus.clear();
			this.jilus.addAll(jilus);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return jilus == null ? 0 : jilus.size();
		}

		@Override
		public Object getItem(int position) {
			return jilus == null ? null : jilus.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final RenwuJilu jilu = jilus.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.renwujilu_item, null);
				holder = new ViewHolder();
				holder.tv_item_num = (TextView) convertView.findViewById(R.id.tv_item_num);
				holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
				holder.tv_fuxi = (TextView) convertView.findViewById(R.id.tv_fuxi);
				//设置背景
				holder.tv_fuxi.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
//				holder.tv_fuxi.setTextColor(getResources().getColor(MyApplication.getApplication().pf.text_color));
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.tv_item_num.setText("背诵单词"+jilu.getCount()+"个");
			holder.tv_item_time.setText(MyUtil.date2Str(MyUtil.str2Date(jilu.getDate(), null), Constant.DATE_RENWU));
			holder.tv_fuxi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ActivityFuxi1.class);
					intent.putExtra("date", jilu.getDate());
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		public TextView tv_item_num;
		public TextView tv_item_time;
		public TextView tv_fuxi;
	}

}
