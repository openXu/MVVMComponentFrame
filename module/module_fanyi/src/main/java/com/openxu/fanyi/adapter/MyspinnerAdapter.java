package com.openxu.fanyi.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.openxu.fanyi.R;

import java.util.ArrayList;
import java.util.List;


public class MyspinnerAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	ArrayList<String> list;
	String[] strs;
	private int downType;
	public static int TYPE_1 = 1;
	public static int TYPE_2 = 2;

	public MyspinnerAdapter(Context context, ArrayList<String> list, String[] strs, int TYPE) {
		super();
		this.context = context;
		this.list = list;
		this.strs = strs;
		this.downType = TYPE;
		inflater = LayoutInflater.from(context);
	}

	public void setStrs(String[] strs) {
		this.strs = strs;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list==null?strs.length : list.size();
	}

	@Override
	public Object getItem(int position) {
		if(null==list){
			return strs[position];
		}else{
			return list.get(position);
		}
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fanyi_myspinner_dropdown_item, null);
			viewHolder = new ViewHolder();
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.myspinner_dropdown_layout);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.myspinner_dropdown_txt);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//整体
		if(downType == TYPE_1){
			if(getCount() == position+1){
				viewHolder.layout.setBackgroundResource(R.drawable.fanyi_spinner_last_item);
			}else{
				viewHolder.layout.setBackgroundResource(R.drawable.fanyi_spinner_item);
			}
		}else{
			if(position == 0){
				viewHolder.layout.setBackgroundResource(R.drawable.fanyi_spinner_first_item);
			}else if(getCount() == position+1){
				viewHolder.layout.setBackgroundResource(R.drawable.fanyi_spinner_last_item);
			}else{
				viewHolder.layout.setBackgroundResource(R.drawable.fanyi_spinner_item);
			}
		}
		if(null==list){
			viewHolder.textView.setText(strs[position]);
		}else{
			viewHolder.textView.setText(list.get(position));
		}
		return convertView;
	}

	public class ViewHolder {
		RelativeLayout layout;
		TextView textView;
	}

	public void refresh(List<String> l) {
		this.list.clear();
		list.addAll(l);
		notifyDataSetChanged();
	}

	public void add(String str) {
		list.add(str);
		notifyDataSetChanged();
	}

	public void add(ArrayList<String> str) {
		list.addAll(str);
		notifyDataSetChanged();

	}
}