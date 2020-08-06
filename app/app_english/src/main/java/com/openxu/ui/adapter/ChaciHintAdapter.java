package com.openxu.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;


public class ChaciHintAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	String[] strs;

	public ChaciHintAdapter(Context context, String[] strs) {
		super();
		this.context = context;
		this.strs = strs;
		inflater = LayoutInflater.from(context);
	}

	public void setStrs(String[] strs) {
		this.strs = strs;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return strs==null?0:strs.length;
	}

	@Override
	public Object getItem(int position) {
		return strs[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_chaci_hint, null);
			viewHolder = new ViewHolder();
			viewHolder.rootview = (RelativeLayout) convertView.findViewById(R.id.rootview);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_text);
			viewHolder.rootview.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(strs[position]);
		return convertView;
	}

	public class ViewHolder {
		RelativeLayout rootview;
		TextView textView;
	}

}