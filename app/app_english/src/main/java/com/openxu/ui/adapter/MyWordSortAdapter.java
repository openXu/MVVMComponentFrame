package com.openxu.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.openxu.db.bean.OpenWord;
import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.OpenWordUtil;

public class MyWordSortAdapter extends BaseAdapter implements SectionIndexer {
	private List<OpenWord> list = null;
	private Context mContext;

	public MyWordSortAdapter(Context mContext, List<OpenWord> list) {
		this.mContext = mContext;
		if (list == null)
			list = new ArrayList<OpenWord>();
		this.list = list;
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

	public void remove(int position){
		list.remove(position);
		notifyDataSetChanged();
	}
	public View getView(final int position, View view, ViewGroup arg2) {
		final OpenWord word = list.get(position);
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_my_word, null);
			viewHolder.rl_en = (LinearLayout) view.findViewById(R.id.rl_en);
			viewHolder.tv_en = (TextView) view.findViewById(R.id.tv_en);
			viewHolder.tv_zh = (TextView) view.findViewById(R.id.tv_zh);
			viewHolder.catalog = (TextView) view.findViewById(R.id.catalog);
			viewHolder.rl_en.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(word.getAddDate())) {
			viewHolder.catalog.setVisibility(View.VISIBLE);
			viewHolder.catalog.setText(MyUtil.date2Str(MyUtil.str2Date(word.getAddDate(), Constant.DATE_DB), Constant.DATE_JS));
		} else {
			viewHolder.catalog.setVisibility(View.GONE);
		}
		viewHolder.tv_en.setText(word.getEnglish());
		String parts = OpenWordUtil.formatParts(word.getParts());
		viewHolder.tv_zh.setText(Html.fromHtml(parts));
		return view;
	}

	final static class ViewHolder {
		LinearLayout rl_en;
		TextView tv_en, tv_zh;
		TextView catalog;
		
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(String date) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr= list.get(i).getAddDate();
			if (sortStr.equalsIgnoreCase(date)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}