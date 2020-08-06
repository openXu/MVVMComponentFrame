package com.openxu.ui;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.AppListener;

import com.openxu.db.base.MyWordDaoSupport;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.bean.WordBook;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.english.R;
import com.openxu.ui.adapter.MyWordSortAdapter;
import com.openxu.utils.MyUtil;
import com.openxu.view.DeleteMywordDialog;
import com.openxu.view.sortview.PinyinComparator;

public class ActivityMyWord extends BaseActivity{
	private LinearLayout ll_loding;
	private ImageView iv_loding;
	private Animation stateAnim;
	private ListView listView;
	private TextView tv_no;
	private MyWordSortAdapter adapter;
	private MyWordDaoSupport dao;
	private WordBook book;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_mybook_word);
		ll_loding = (LinearLayout) findViewById(R.id.ll_loding);
		iv_loding = (ImageView) findViewById(R.id.iv_loding);
		listView = (ListView) findViewById(R.id.listView);
		tv_no = (TextView) findViewById(R.id.tv_no);
		ll_loding.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		tv_no.setVisibility(View.GONE);
		
		adapter = new MyWordSortAdapter(this, null);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext, ActivityShowOneWord.class);
				intent.putExtra("action", 2);
				intent.putExtra("word", (OpenWord)adapter.getItem(position));
				intent.putExtra("book", book);
				startActivityForResult(intent, 0);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final OpenWord word = (OpenWord)adapter.getItem(position);
				DeleteMywordDialog deleteDialog = new DeleteMywordDialog(mContext);
				deleteDialog.setListener(new DeleteMywordDialog.Listener() {
					@Override
					public void click(int witch) {
						int id = dao.delete(book.getWordTable(), word.get_id());
						adapter.remove(position);
					}
				});
				deleteDialog.show();
				return true;
			}
		});
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
			AppConnect.getInstance(mContext).setBannerAdNoDataListener(new AppListener() {
				@Override
				public void onBannerNoData() {
					MyUtil.LOG_W(TAG, "banner广告暂无可用数据");
				}
			});
			// 互动广告调用方式
			LinearLayout layout = (LinearLayout) findViewById(R.id.miniAdLinearLayout2);
			AppConnect.getInstance(mContext).showBannerAd(mContext, layout);
		}
	}

	@Override
	protected void initData() {
		// 进度条转圈圈
		stateAnim = AnimationUtils.loadAnimation(mContext,R.anim.open_listviewloding_anim);
		LinearInterpolator lin = new LinearInterpolator();
		stateAnim.setInterpolator(lin);
		iv_loding.startAnimation(stateAnim);
		book = getIntent().getParcelableExtra("book");
		titleView.setTitle(book.getName());
		dao = new MyWordDaoSupport();
		 final AnimationDrawable animationDrawable = (AnimationDrawable) iv_loding.getBackground();
	      animationDrawable.start();
		// 查询数据库
		new AsyncTask<Void, Void, Integer>() {
			List<OpenWord> wordList;
			PinyinComparator pinyinComparator = new PinyinComparator();
			@Override
			protected Integer doInBackground(Void... params) {
				wordList = dao.findByCondition(book.getWordTable(), WordDBHelper.TABLE_ID + ">?", 
						new String[]{"0"}, WordDBHelper.TABLE_ID+" DESC ");
				MyUtil.LOG_V(TAG, "一共查询出"+wordList.size()+"个单词");
				return 0;
			}

			protected void onPostExecute(Integer result) {
				titleView.setTitle(book.getName()+"("+wordList.size()+"词)");
				animationDrawable.stop();
				ll_loding.setVisibility(View.GONE);
				stateAnim.cancel();
				if(wordList!=null&&wordList.size()>0){
					listView.setVisibility(View.VISIBLE);
					tv_no.setVisibility(View.GONE);
					adapter.updateListView(wordList);
				}else{
					listView.setVisibility(View.GONE);
					tv_no.setVisibility(View.VISIBLE);
				}
			}

		}.execute();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		initData();
	}

	
}
