package com.openxu.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.openxu.db.bean.OpenWord;
import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.OpenWordUtil;

public class WordSortAdapter extends BaseAdapter implements SectionIndexer {
	private List<OpenWord> list = null;
	private Context mContext;
	private String level;

	public WordSortAdapter(Context mContext, List<OpenWord> list, String level) {
		this.mContext = mContext;
		if (list == null)
			list = new ArrayList<OpenWord>();
		this.list = list;
		this.level = level;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<OpenWord> list) {
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
		final OpenWord word = list.get(position);
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_word, null);
			viewHolder.tv_show = (TextView) view.findViewById(R.id.tv_show);
			viewHolder.rl_en = (RelativeLayout) view.findViewById(R.id.rl_en);
			viewHolder.catalog = (TextView) view.findViewById(R.id.catalog);
			viewHolder.tv_zh = (TextView) view.findViewById(R.id.tv_zh);
			viewHolder.rl_en.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.catalog.setVisibility(View.VISIBLE);
			viewHolder.catalog.setText(word.getSortLetters());
		} else {
			viewHolder.catalog.setVisibility(View.GONE);
		}
	
//		if(word.getRemenber() == GetWordDBHelper.BOOLEAN_TRUE)
//			viewHolder.tv_remmber.setVisibility(View.VISIBLE);
//		else
//			viewHolder.tv_remmber.setVisibility(View.INVISIBLE);
		viewHolder.tv_show.setText(word.getEnglish());
		String part = OpenWordUtil.getOnePart(word.getParts());
		viewHolder.tv_zh.setText(part);
		return view;
	}

	final static class ViewHolder {
		TextView catalog;
		RelativeLayout rl_en;
		TextView tv_show;
		TextView tv_zh;
		
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