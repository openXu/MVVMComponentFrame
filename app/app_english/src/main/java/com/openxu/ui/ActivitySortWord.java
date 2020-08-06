package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openxu.db.base.DAO;
import com.openxu.db.bean.OpenWord;
import com.openxu.english.R;
import com.openxu.ui.adapter.WordSortAdapter;
import com.openxu.utils.BookUtils;
import com.openxu.utils.MyUtil;
import com.openxu.view.XlistView.XListView;
import com.openxu.view.XlistView.XListView.IXListViewListener;

public class ActivitySortWord extends BaseActivity implements IXListViewListener{
	private XListView lv_list;
	private WordSortAdapter adapter;
	private DAO<OpenWord> wordDao;
	private List<OpenWord> wordList;
	private int findNum = 30;
	
	private LinearLayout ll_loding;
	private ImageView iv_loding;
	private Animation stateAnim;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_sort_word);
		ll_loding = (LinearLayout) findViewById(R.id.ll_loding);
		iv_loding = (ImageView) findViewById(R.id.iv_loding);
		ll_loding.setVisibility(View.VISIBLE);
		lv_list = (XListView) findViewById(R.id.lv_list);
		lv_list.setPullRefreshEnable(false);
		lv_list.setPullLoadEnable(true);
		lv_list.setXListViewListener(this);
		wordDao = BookUtils.getDaoImpl();
		String level = BookUtils.getLevelLable();
		adapter = new WordSortAdapter(this, null, level);
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext, ActivityShowOneWord.class);
				intent.putExtra("action", 3);
				intent.putExtra("word", (OpenWord)adapter.getItem(position));
				startActivityForResult(intent, 0);
			}
		});
		/*if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 预加载插屏广告内容（仅在使用到插屏广告的情况，才需要添加）
			AppConnect.getInstance(this).initPopAd(this);
			// 设置插屏广告无数据时的回调监听（该方法必须在showPopAd之前调用）
			AppConnect.getInstance(this).setPopAdNoDataListener(new AppListener() {
				@Override
				public void onPopNoData() {
					MyUtil.LOG_E(TAG, "插屏广告暂无可用数据");
				}

			});
			// 显示插屏广告
			AppConnect.getInstance(this).showPopAd(this);
		}*/
	}

	@Override
	protected void initData() {
		// 进度条转圈圈
		stateAnim = AnimationUtils.loadAnimation(mContext,R.anim.open_listviewloding_anim);
		LinearInterpolator lin = new LinearInterpolator();
		stateAnim.setInterpolator(lin);
		iv_loding.startAnimation(stateAnim);
		
		wordList = new ArrayList<OpenWord>();
		onLoadMore();
	}
	@Override
	protected void initTitleView() {
		super.initTitleView();
		titleView.setTitle(BookUtils.getBookName());
	}
	@Override
	protected void setPf() {
		super.setPf();
//		int color = getResources().getColor(MyApplication.getApplication().pf.text_color);
//		tv_pop.setTextColor(color);
//		tv_pop.setBackgroundResource(MyApplication.getApplication().pf.title_bg);
	}
	
	private boolean isFillingData = false;
	@SuppressLint("NewApi") @SuppressWarnings("unchecked")
	@Override
	public void onLoadMore() {
		if (isFillingData)
			return;
		isFillingData = true;
	    new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				List<OpenWord> words = wordDao.getPartWords(wordList.size(), findNum);
				if (words != null && words.size() > 0) {
					MyUtil.LOG_D(TAG, "从数据库获取到：" + words.size()+"个单词");
					wordList.addAll(words);
				} else {
					MyUtil.showToast(mContext, -1, "没有更多单词");
				}
				return null;
			};

			protected void onPostExecute(Void result) {
				lv_list.stopLoadMore();
				lv_list.setVisibility(View.VISIBLE);
				ll_loding.setVisibility(View.GONE);
				stateAnim.cancel();
				if (isFirst) {
					adapter.updateListView(wordList);
					lv_list.setSelection(0);
				}else{
					adapter.updateListView(wordList);
				}
				isFirst = false;
				isFillingData = false;
			}
		}.execute();
		/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		}else{
			task.execute();
		}*/
	}

	private boolean isFirst = true;
	/**
	 * 刷新ListView
	 * @param isSelect 是否定位到原来显示的位置
	 */
	private void updataShow() {
		// 保存当前第一个可见的item的索引和偏移量
		int nowCount = lv_list.getCount() - 1;
		View v = lv_list.getChildAt(0);
		int top = (v == null) ? 0 : v.getHeight();

		nowCount = wordList.size() - nowCount + 1;
		adapter.updateListView(wordList);
			lv_list.setSelectionFromTop(nowCount, top);
	}

	@Override
	public void onRefresh() {
		
	}

	
}
