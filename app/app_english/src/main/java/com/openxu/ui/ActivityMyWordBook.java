package com.openxu.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.openxu.db.base.MyWordDaoSupport;
import com.openxu.db.bean.WordBook;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.view.CreateBookDialog;
import com.openxu.view.CreateBookDialog.Listener;
import com.openxu.view.UpdateBookDialog;

/**
 * 单词本
 * @author openXu
 * 
 */
public class ActivityMyWordBook extends BaseActivity implements OnClickListener {

	private ListView lv_list;
	private TextView tv_no;
	private List<WordBook> list;
	private MyBookAdapter adapter;
	private MyWordDaoSupport dao;
	private CreateBookDialog dialog;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_my_wordbook);
		tv_no = (TextView) findViewById(R.id.tv_no);
		tv_no.setVisibility(View.GONE);
		lv_list = (ListView) findViewById(R.id.lv_booklist);
		lv_list.setVisibility(View.GONE);
		adapter = new MyBookAdapter();
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final WordBook book = list.get(position);
				Intent intent = new Intent(mContext, ActivityMyWord.class);
				intent.putExtra("book", book);
				startActivityForResult(intent, 0);
			}
		});
		list = new ArrayList<WordBook>();
		dao = new MyWordDaoSupport();
	}
	
	@Override
	protected void setPf() {
		super.setPf();
		lv_list.setSelector(MyApplication.getApplication().pf.item_selector);
	}

	@Override
	protected void initData() {
		list = dao.findAllBook();
		if(list!=null&&list.size()>0){
			tv_no.setVisibility(View.GONE);
			lv_list.setVisibility(View.VISIBLE);
		}else{
			tv_no.setVisibility(View.VISIBLE);
			lv_list.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected boolean onMenuSelected() {
		//添加单词本
		if(dialog==null)
			dialog = new CreateBookDialog(mContext);
		dialog.setText("");
		dialog.setTitle("新建单词本");
		dialog.setListener(new Listener() {
			@Override
			public void create(String name) {
				WordBook book = new WordBook();
				book.setCreateDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
				book.setName(name);
				book.setWordTable(dao.createTableName());
				int id = (int) dao.insertBook(book);
				dao.createBook(book.getWordTable());
				initData();
				Intent intent = new Intent();
				intent.setAction(Constant.RECEIVER_BOOK_CHANGED);
				sendBroadcast(intent); 
			}
		});
		dialog.show();
		return true;
	}
	
	class Holder{
		ImageView iv_update;
		TextView tv_name, tv_time, tv_num;
	}
	
	class MyBookAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return list==null?0:list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			final WordBook book = list.get(position);
			if(convertView==null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mybook, null);
				holder = new Holder();
				holder.iv_update = (ImageView) convertView.findViewById(R.id.iv_update);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.tv_name.setText(book.getName());
			holder.tv_time.setText("添加日期: "+MyUtil.date2Str(MyUtil.str2Date(book.getCreateDate(), Constant.DATE_DB), Constant.DATE_JS));
			holder.tv_num.setText("共"+book.num+"个单词");
			holder.iv_update.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UpdateBookDialog updalog = new UpdateBookDialog(mContext);
					updalog.setListener(new UpdateBookDialog.Listener() {
						@Override
						public void click(int witch) {
							if(witch==1){//删除
								dao.deleteBook(book.get_id());
								dao.deleteAll(book.getWordTable());
								initData();
								Intent intent = new Intent();
								intent.setAction(Constant.RECEIVER_BOOK_CHANGED);
								sendBroadcast(intent); 
							}else if(witch ==2 ){
								if(dialog==null)
									dialog = new CreateBookDialog(mContext);
								dialog.setText(book.getName());
								dialog.setTitle("修改单词本");
								dialog.setListener(new Listener() {
									@Override
									public void create(String name) {
										book.setName(name);
										int id = (int) dao.updataBook(book);
										initData();
										Intent intent = new Intent();
										intent.setAction(Constant.RECEIVER_BOOK_CHANGED);
										sendBroadcast(intent); 
									}
								});
								dialog.show();
							}
						}
					});
					updalog.show();
				}
			});
			return convertView;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		initData();
	}
	
}
