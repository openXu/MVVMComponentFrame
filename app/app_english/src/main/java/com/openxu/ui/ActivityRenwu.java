package com.openxu.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.waps.AppConnect;

import com.openxu.db.base.DAO;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.BookUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.OpenWordUtil;
import com.openxu.utils.Property;
import com.openxu.view.RoundProgressBar;

/**
 * @author openXu
 */
public class ActivityRenwu extends BaseActivity{
	private RoundProgressBar rb_progress;
	private TextView tv_renwu,tv_finish;
	
	private ViewPager viewpager;
	private PagerTabStrip tabStrip;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private WordAdapter adapter1;
	private WordAdapter adapter2;
	private ArrayList<OpenWord> dbWords1;
	private ArrayList<OpenWord> dbWords2;
	
	private LinearLayout ll_lianxi1;
	private TextView tv_lianxi;
	private LinearLayout ll_lianxi2;
	
	@Override
	protected void initView() {
		setContentView(R.layout.renwu_activity);
		TAG = "ActivityRenwu";
		rb_progress = (RoundProgressBar) findViewById(R.id.rb_progress);
		tv_renwu = (TextView) findViewById(R.id.tv_renwu);
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		
		viewpager = (ViewPager) findViewById(R.id.viewpager);
        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(this.getResources().getColor(R.color.color_white));
        //设置当前tab页签的下划线颜色
//        tabStrip.setTabIndicatorColor(this.getResources().getColor(MyApplication.getApplication().pf.renwu_tab));
        tabStrip.setTextSpacing(250);
         
        View view1 = LayoutInflater.from(this).inflate(R.layout.renwu_view_list, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.renwu_view_list, null);
        //viewpager开始添加view
        viewContainter.add(view1);
        viewContainter.add(view2);
        //页签项
        titleContainer.add("未完成");
        titleContainer.add("已完成");
        viewpager.setAdapter(new ViewPagAdapter());
        ll_lianxi1 = (LinearLayout) view1.findViewById(R.id.ll_lianxi);
        ll_lianxi2 = (LinearLayout) view2.findViewById(R.id.ll_lianxi);
        tv_lianxi = (TextView) view2.findViewById(R.id.tv_lianxi);
        ll_lianxi1.setVisibility(View.GONE);
        ll_lianxi2.setVisibility(View.GONE);
        tv_lianxi.setOnClickListener(this);
        ListView lv_list1 = (ListView) view1.findViewById(R.id.lv_list);
        ListView lv_list2 = (ListView) view2.findViewById(R.id.lv_list);
        adapter1 = new WordAdapter(mContext, false);
        adapter2 = new WordAdapter(mContext, true);
        lv_list1.setAdapter(adapter1);
        lv_list2.setAdapter(adapter2);
        LinearLayout ll_paly1  = (LinearLayout) view1.findViewById(R.id.ll_paly);
        ll_paly1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ActivityRenwuPlay.class); 
				intent.putExtra("action", 1);
				intent.putParcelableArrayListExtra("words1", dbWords1);
				intent.putParcelableArrayListExtra("words2", dbWords2);
				startActivityForResult(intent,1);
			}
		});
        LinearLayout ll_paly2  = (LinearLayout) view2.findViewById(R.id.ll_paly);
        ll_paly2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ActivityRenwuPlay.class); 
				intent.putExtra("action", 2);
				intent.putParcelableArrayListExtra("words1", dbWords1);
				intent.putParcelableArrayListExtra("words2", dbWords2);
				startActivityForResult(intent,2);
			}
		});
        
        if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 迷你广告调用方式
			AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
			AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.title_bg));//设置迷你广告文字颜色
			LinearLayout miniLayout = (LinearLayout) view1.findViewById(R.id.miniAdLinearLayout1);
			AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
			miniLayout  = (LinearLayout) view2.findViewById(R.id.miniAdLinearLayout1);
			AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
		}
	}
	@Override
	protected void setPf() {
		super.setPf();
//		findViewById(R.id.ll_bg1).setBackgroundResource(MyApplication.getApplication().pf.renwu_showbg);
//		int color = getResources().getColor(MyApplication.getApplication().pf.text_color);
//		((TextView)(findViewById(R.id.tv_lable_renwu))).setTextColor(color);
//		((TextView)(findViewById(R.id.tv_renwu))).setTextColor(color);
//		((TextView)(findViewById(R.id.tv_lable_finish))).setTextColor(color);
//		((TextView)(findViewById(R.id.tv_finish))).setTextColor(color);
//		rb_progress.setTextColor(color);
//		rb_progress.setNumTextColor(color);
		rb_progress.setCricleColor(getResources().getColor(MyApplication.getApplication().pf.round_color));
	}
	
	@Override
	protected void initData() {
		dialog.setShowText("正在加载词库...");
		dialog.show();
		new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				dbWords1 = new ArrayList<OpenWord>();
				dbWords2 = new ArrayList<OpenWord>();
				DAO<OpenWord> wordDao = BookUtils.getDaoImpl();
				List<OpenWord> dbWords = null;
				if(MyApplication.property.jcms == Property.VALUE_JCMS_SJ){
					dbWords = wordDao.findRandom(MyApplication.property.renwu_num);
				}else{
					dbWords = wordDao.findShunxu(MyApplication.property.renwu_num);
				}
				//插入时间
				for(OpenWord word : dbWords){
					word.setDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
					MyUtil.LOG_E(TAG, "fani:"+word);
				}
				wordDao.updataAll(dbWords);
				for(OpenWord word : dbWords){
					if(word.getRemenber()==WordDBHelper.BOOLEAN_FALSE){
						dbWords1.add(word);
					}else{
						dbWords2.add(word);
					}
				}
				return 1;
			}
			protected void onPostExecute(Integer result) {
				dialog.cancel();
				adapter1.setWords(dbWords1);
				adapter2.setWords(dbWords2);
				updatePro();
			};
		}.execute();
		
	}

	private void updatePro() {
		tv_renwu.setText((dbWords1.size()+dbWords2.size())+"");
		tv_finish.setText(dbWords2.size()+"");
		rb_progress.setText("已学");
		rb_progress.setMax((dbWords1.size()+dbWords2.size()));
		rb_progress.setProgress(dbWords2.size());
		if(dbWords1.size()<=0){
			ll_lianxi1.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_lianxi:   //练习
			Intent intent = new Intent(mContext, ActivityTest.class); 
			intent.putParcelableArrayListExtra("words1", dbWords1);
			intent.putParcelableArrayListExtra("words2", dbWords2);
			startActivityForResult(intent,2);
			break;
		default:
			break;
		}
	}
	
	class ViewPagAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			 return viewContainter.size();
		}
		//滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                Object object) {
            ((ViewPager) container).removeView(viewContainter.get(position));
        }
		//每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(viewContainter.get(position));
            return viewContainter.get(position);
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titleContainer.get(position);
        }
	}
	
	class WordAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<OpenWord> wordsList;
		private boolean isOk = false;

		public WordAdapter(Context context, boolean isOk) {
			mInflater = LayoutInflater.from(context);
			wordsList = new ArrayList<OpenWord>();
			this.isOk = isOk;
		}

		public void setWords(List<OpenWord> showWords) {
			this.wordsList.clear();
			this.wordsList.addAll(showWords);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return wordsList == null ? 0 : wordsList.size();
		}

		@Override
		public Object getItem(int position) {
			return wordsList == null ? null : wordsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final OpenWord word = wordsList.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.renwu_item, null);
				holder = new ViewHolder();
				holder.ll_content = (LinearLayout) convertView.findViewById(R.id.ll_content);
				holder.tv_english = (TextView) convertView.findViewById(R.id.tv_english);
				holder.tv_voice = (TextView) convertView.findViewById(R.id.tv_voice);
				holder.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice);
				holder.tv_china = (TextView) convertView.findViewById(R.id.tv_china);
				holder.tv_remenber = (TextView) convertView.findViewById(R.id.tv_remenber);
				holder.ll_remenber = (LinearLayout) convertView.findViewById(R.id.ll_remenber);
				holder.iv_remenber = (ImageView) convertView.findViewById(R.id.iv_remenber);
				//设置背景
				holder.ll_content.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
				holder.ll_remenber.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
//				holder.tv_remenber.setTextColor(getResources().getColor(MyApplication.getApplication().pf.text_color));
				if(isOk){
					holder.tv_remenber.setText("再记记");
					holder.iv_remenber.setImageResource(R.drawable.open_renwu_icon_wjl);
				}else{
					holder.tv_remenber.setText("记住了");
					holder.iv_remenber.setImageResource(R.drawable.open_renwu_icon_jzl);
				}
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.tv_english.setText(word.getEnglish());
			String voice = OpenWordUtil.formatPh(word.getPh_en(), word.getPh_am());
			holder.tv_voice.setText(Html.fromHtml(voice));
			String parts = OpenWordUtil.formatParts(word.getParts());
			holder.tv_china.setText(Html.fromHtml(parts));
			holder.iv_voice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					PLAY_STATE state = MyApplication.getApplication().player.getState();
//					if (state != PLAY_STATE.STOPED) {
//						Log.e(TAG, "上次正在合成，先结束");
//						MyApplication.getApplication().player.cancel();
//					}
					MyUtil.TOAST(mContext, "播报："+word.getEnglish());
//					MyApplication.getApplication().player.play(word.getEnglish());
				}
			});
			holder.ll_remenber.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DAO<OpenWord> wordDao = BookUtils.getDaoImpl();
					if(isOk){  
						//忘记了
						word.setRemenber(WordDBHelper.BOOLEAN_FALSE);
						wordDao.updata(word);
						dbWords2.remove(position);
						adapter2.setWords(dbWords2);
						dbWords1.add(word);
						adapter1.setWords(dbWords1);
					}else{
						//记住了
						word.setRemenber(WordDBHelper.BOOLEAN_TRUE);
						wordDao.updata(word);
						dbWords1.remove(position);
						adapter1.setWords(dbWords1);
						dbWords2.add(word);
						adapter2.setWords(dbWords2);
					}
					updatePro();
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		public LinearLayout ll_content, ll_remenber;
		public TextView tv_english;
		public TextView tv_voice;
		public ImageView iv_voice, iv_remenber;
		public TextView tv_china;
		public TextView tv_remenber;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dbWords1 = data.getParcelableArrayListExtra("words1");
		dbWords2 = data.getParcelableArrayListExtra("words2");
		adapter1.setWords(dbWords1);
		adapter2.setWords(dbWords2);
		updatePro();
	}
	
	
}
