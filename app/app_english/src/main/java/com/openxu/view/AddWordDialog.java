package com.openxu.view;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.openxu.db.bean.WordBook;
import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.MyUtil;

public class AddWordDialog extends ItotemBaseDialog {

	private MyAdapter adapter;
	private ListView lv_list;
	private Listener listener;
	private TextView tv_title, tv_cancle;
	private List<WordBook> mybook;   //此单词已经在这些单词本存在
	private List<WordBook> list;  //所有单词本

    public AddWordDialog(Context context,List<WordBook> mybook, List<WordBook> list) {
        super(context, R.layout.dialog_addword_selectbook, R.style.ItotemTheme_Dialog);
        this.mybook = mybook;
        this.list = list;
    }
    @Override
    protected void initView() {
    	setCancelable(true);
    	setCanceledOnTouchOutside(true);
    	tv_title = (TextView) findViewById(R.id.tv_title);
    	tv_cancle = (TextView) findViewById(R.id.tv_cancle);
    	lv_list = (ListView) findViewById(R.id.lv_list);
    	tv_title.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
    	tv_cancle.setBackgroundResource(MyApplication.pf.item_selector);
    	adapter = new MyAdapter(getContext());
    	lv_list.setAdapter(adapter);
    	tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
    	
    }

    public void setListener(Listener listener){
    	this.listener = listener;
    }
    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    public void setTitle(String text){
    	tv_title.setText(text);
    }
    class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list==null?0:list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			final WordBook book = list.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.dialog_addword_item, null);
				holder = new ViewHolder();
				holder.iv_line = (ImageView) convertView.findViewById(R.id.iv_line);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_name.setBackgroundResource(MyApplication.pf.item_selector);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			if(position==(list.size()-1)){
				holder.iv_line.setVisibility(View.GONE);
			}else{
				holder.iv_line.setVisibility(View.VISIBLE);
			}
			holder.tv_name.setText(book.getName());
			holder.tv_name.setTextColor(Color.parseColor("#dd000000"));
			holder.tv_name.setTag(false);
			if(mybook==null||mybook.size()==0){
			}else{
				String tablename = book.getName();
				for(WordBook wbook : mybook){
					if(tablename.equals(wbook.getName())){   //单词已经存在单词本中
						holder.tv_name.setTag(true);
						holder.tv_name.setTextColor(Color.parseColor("#66000000"));
					}
				}
			}
			holder.tv_name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean has = (Boolean) holder.tv_name.getTag();
					if(has){   //单词已经存在单词本中
						MyUtil.showToast(context, -1, "<"+book.getName()+">中已有此单词");
					}else{
						listener.add(book);
						cancel();
					}
				}
			});
			return convertView;
		}
	}
    private class ViewHolder {
    	public ImageView iv_line;
		public TextView tv_name;
	}

    public interface Listener{
    	public abstract void add(WordBook book);
    }
    
}
