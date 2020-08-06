package com.openxu.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.waps.AppConnect;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.base.DAO;
import com.openxu.db.base.MyWordDaoSupport;
import com.openxu.db.bean.OneSentence;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.bean.RenwuJilu;
import com.openxu.db.bean.WordBook;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.db.impl.OneSentenceDaoImpl;
import com.openxu.db.impl.RenwuJiluDao;
import com.openxu.english.R;
import com.openxu.utils.BookUtils;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.Property;
import com.openxu.view.MyScrollView;
import com.openxu.view.MyScrollView.ScrollViewListener;
import com.openxu.view.RoundProgressBar;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class FragmentCidian extends Fragment implements OnClickListener {

	private String TAG = "FragmentCidian";
	private Context mContext;
	private OneSentenceDaoImpl sentenceDao;
	private MyScrollView scrollView;
	
	private RelativeLayout rl_sentence;
	private ImageView iv_icon;
	private TextView tv_one_eng, tv_one_chi;
	private RoundProgressBar rb_progress;
	private TextView tv_all_num,tv_re_num, tv_renwu_num, tv_fuxi_num;
	private LinearLayout ll_renwujilu;
	private RelativeLayout rl_renwu, rl_fuxi;
	
	private LinearLayout ll_mybook, ll_dcb;
	private MyWordDaoSupport dao2;
	private List<WordBook> myBookList;
	
	private View rootView ;
	
	private RenwuJilu fuxi;
	
	
	protected void setPf() {
		if(rootView!=null){
			ll_renwujilu.setBackgroundResource(MyApplication.getApplication().pf.main_item_bg);
			rootView.findViewById(R.id.rl_renwu).setBackgroundResource(MyApplication.getApplication().pf.main_item_bg);
			rootView.findViewById(R.id.rl_fuxi).setBackgroundResource(MyApplication.getApplication().pf.main_item_bg);
			int color = getResources().getColor(MyApplication.getApplication().pf.text_color);
			
			rb_progress.setTextColor(color);
			rb_progress.setNumTextColor(color);
			rb_progress.setCricleColor(getResources().getColor(MyApplication.getApplication().pf.round_color));
			
			addBookView();
		}else{
//			MyUtil.TOAST(MyApplication.getApplication(), "rootView==null");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		rootView = inflater.inflate(R.layout.fragment_cidian, container, false);
		
		rl_sentence = (RelativeLayout) rootView.findViewById(R.id.rl_sentence);
		iv_icon = (ImageView) rootView.findViewById(R.id.iv_icon);
		tv_one_eng = (TextView) rootView.findViewById(R.id.tv_one_eng);
		tv_one_chi = (TextView) rootView.findViewById(R.id.tv_one_chi);
		
		ll_renwujilu = (LinearLayout) rootView.findViewById(R.id.ll_renwujilu);
		rb_progress = (RoundProgressBar) rootView.findViewById(R.id.rb_progress);
		tv_all_num = (TextView) rootView.findViewById(R.id.tv_all_num);
		tv_re_num = (TextView) rootView.findViewById(R.id.tv_re_num);
		
		tv_renwu_num = (TextView) rootView.findViewById(R.id.tv_renwu_num);
		rl_renwu = (RelativeLayout) rootView.findViewById(R.id.rl_renwu);
		
		rl_fuxi = (RelativeLayout) rootView.findViewById(R.id.rl_fuxi);
		tv_fuxi_num = (TextView) rootView.findViewById(R.id.tv_fuxi_num);
		rl_sentence.setOnClickListener(this);
		ll_renwujilu.setOnClickListener(this);
		rl_renwu.setOnClickListener(this);
		rl_fuxi.setOnClickListener(this);
		
		scrollView = (MyScrollView) rootView.findViewById(R.id.scrollView);
		scrollView.setScrollViewListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(int y) {
				if(y>30){
					if(y<rl_sentence.getHeight()){
						float titleAlpha = ((float)(y-30))/(rl_sentence.getHeight()-30-((MainActivity)getActivity()).titleView.getHeight());
						int alpha = (int)(titleAlpha*255);
						if(alpha>255)
							alpha = 255;
//						MyUtil.LOG_V(TAG, titleAlpha+"scorll滑动：y："+y + ", 设置透明度："+alpha);
						((MainActivity)getActivity()).setTitleAlpha(alpha, true);
					}else{
						((MainActivity)getActivity()).setTitleAlpha(255, true);
					}
				}else{
					((MainActivity)getActivity()).setTitleAlpha(0, false);
				}
			}
		});
		
		ll_mybook = (LinearLayout) rootView.findViewById(R.id.ll_mybook);
		ll_dcb = (LinearLayout) rootView.findViewById(R.id.ll_dcb);
		
		sentenceDao = new OneSentenceDaoImpl();
		setPf();
		MyUtil.LOG_I(TAG, "词典fragment创建了");
		
//		add_ad = (Button) rootView.findViewById(R.id.add_ad);
//		add_ad.setOnClickListener(this);
		showAd();
		return rootView;
	}
	
	public void showMyBook(){
		if(ll_mybook!=null){
			if(MyApplication.property.showBook){
				ll_mybook.setVisibility(View.VISIBLE);
				if(myBookList== null)
					myBookList = new ArrayList<WordBook>();
				if(dao2 == null)
					dao2 = new MyWordDaoSupport();
				myBookList = dao2.findAllBook();
				addBookView();
			}else{
				ll_mybook.setVisibility(View.GONE);
			}
		}
	}
	
	public void addBookView(){
		ll_dcb.removeAllViews();
		if(myBookList!=null&&myBookList.size()>0){
			for(int i=0;i<myBookList.size();i++){
				final WordBook book = myBookList.get(i);
				View view = LayoutInflater.from(mContext).inflate(R.layout.item_cidian_mybook, null);
				RelativeLayout rl_pic = (RelativeLayout) view.findViewById(R.id.rl_pic);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
				rl_pic.setBackgroundResource(MyApplication.getApplication().pf.main_bookitem_bg);
				tv_name.setText(book.getName());
				tv_num.setText("共"+book.num+"个单词");
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, ActivityMyWord.class);
						intent.putExtra("book", book);
						getActivity().startActivityForResult(intent, MainActivity.REQUEST_COED_MYWORD);
						getActivity().overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
					}
				});
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
				if(i!=0)
					params.topMargin = MyUtil.dp2px(12, mContext);
				params.topMargin = MyUtil.dp2px(12, mContext);
				ll_dcb.addView(view, params);
			}
		}else{
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_cidian_mybook, null);
			RelativeLayout rl_pic = (RelativeLayout) view.findViewById(R.id.rl_pic);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
			rl_pic.setBackgroundResource(MyApplication.getApplication().pf.main_bookitem_bg);
			iv_icon.setImageResource(R.drawable.open_cidian_mybook_add_icon);
			tv_name.setText("点击添加单词本");
			tv_num.setVisibility(View.GONE);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().startActivityForResult(new Intent(mContext, ActivityMyWordBook.class), MainActivity.REQUEST_COED_MYWORD);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
				}
			});
			ll_dcb.addView(view);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		updateData();
		getOneSentence(MyUtil.date2Str(new Date(), Constant.DATE_JS));
	}
	public void showAd(){
		if(mContext!=null){
			if(MyApplication.property.initAd()&&MyApplication.showAd){
				// 迷你广告调用方式
				AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
				AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.ad_text_color));//设置迷你广告文字颜色
				LinearLayout miniLayout = (LinearLayout) rootView.findViewById(R.id.miniAdLinearLayout1);
				AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
			}
		}
	}
	
	String befordate;
	public void getOneSentence(final String date) {
		//从数据库获取制定日期的每日一句
		List<OneSentence> sentences = sentenceDao.findByCondition(WordDBHelper.TABLE_SENTENCE_DATE+" = ? ", new String[]{date}, null);
		if(sentences!=null&&sentences.size()>0){
			MyUtil.LOG_D(TAG, "从数据库查询每日一句："+sentences.get(0));
			showOneSentence(sentences.get(0));
			return;
		}
		if(!NetWorkUtil.isNetworkAvailable(mContext)){
			MyUtil.showToast(getActivity(), R.string.no_net, "");
		}
//		传入参数：file	//数据格式，默认（json），可选xml；date	//标准化日期格式 如：2013-05-06， 如：http://open.iciba.com/dsapi/?date=2013-05-03
//		如果 date为空 则默认取当日的，当日为空 取前一日的
//		type(可选)	// last 和 next 你懂的，以date日期为准的，last返回前一天的，next返回后一天的
		//金山词霸每日一句接口问题：向前推两年
//		String url = Constant.URL_JS_ONESENTENCE+"?date="+MyUtil.getBeforDate(date, Calendar.YEAR, 1);
		String url = Constant.URL_JS_ONESENTENCE+"?date="+date;
		MyUtil.LOG_D(TAG, "获取每日一句："+url);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,url,
			    new RequestCallBack<String>(){
			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			        }
			        @Override
			        public void onSuccess(ResponseInfo<String> responseInfo) {
//			        	dao1.deleteAll();
			        	try{
			        		MyUtil.LOG_E(TAG, "服务器获取每日一句："+responseInfo.result);
				        	OneSentence one = JSON.parseObject(responseInfo.result, OneSentence.class);
				        	//金山词霸每日一句接口问题：向前推两年
				        	one.setDateline(date);
				        	sentenceDao.insert(one);
				        	showOneSentence(one);
				        	return;
			        	}catch(Exception e){
			        		//服务器获取失败，获取前一天的
			        		befordate = MyUtil.getBeforDate(date, Calendar.DATE, 1);
		        			int total = sentenceDao.getTotalCount();
		        			if(total>0){
		        				while(true){
		        					MyUtil.LOG_I(TAG, "从数据库获取前一天每日一句："+befordate);
			        				List<OneSentence> sentences = sentenceDao.findByCondition(WordDBHelper.TABLE_SENTENCE_DATE+" = ? ", new String[]{befordate}, null);
					        		if(sentences!=null&&sentences.size()>0){
					        			MyUtil.LOG_D(TAG, "从数据库查询每日一句："+sentences.get(0));
					        			showOneSentence(sentences.get(0));
					        			return;
					        		}
					        		getOneSentence(befordate);
		        				}
		        			}else{
		        				getOneSentence(befordate);
		        			}
			        	}
			        }
			        @Override
			        public void onStart() {
			        }
			        @Override
			        public void onFailure(HttpException error, String msg) {
			        	MyUtil.LOG_E(TAG, "服务器获取每日一句失败："+msg);
			        	//服务器获取失败，获取前一天的
		        		befordate = MyUtil.getBeforDate(date, Calendar.DATE, 1);
	        			int total = sentenceDao.getTotalCount();
	        			if(total>0){
	        				while(true){
	        					MyUtil.LOG_I(TAG, "从数据库获取前一天每日一句："+befordate);
		        				List<OneSentence> sentences = sentenceDao.findByCondition(WordDBHelper.TABLE_SENTENCE_DATE+" = ? ", new String[]{befordate}, null);
				        		if(sentences!=null&&sentences.size()>0){
				        			MyUtil.LOG_D(TAG, "从数据库查询每日一句："+sentences.get(0));
				        			showOneSentence(sentences.get(0));
				        			return;
				        		}
				        		getOneSentence(befordate);
	        				}
	        			}else{
	        				getOneSentence(befordate);
	        			}
			        }
			});
	}

	private void showOneSentence(OneSentence sentence) {
		
		MyUtil.LOG_D(TAG, "加载图片："+sentence.getPicture2());
//		ImageLoader.getInstance().displayImage(sentence.getPicture(), iv_icon);
		ImageLoader.getInstance().displayImage(sentence.getPicture2(), iv_icon, MyApplication.getApplication().sentensOptions);
		tv_one_eng.setText(sentence.getContent());
		tv_one_chi.setText(sentence.getNote());
	}

	public void setAd(){
		MyUtil.LOG_I(TAG, "词典fragment，加广告"+MyApplication.property.initAd());
		if(null!=rootView&&!MyApplication.property.initAd())
			rootView.findViewById(R.id.miniAdLinearLayout1).setVisibility(View.GONE);
	}
	public void updateData() {
		showMyBook();
		
		DAO<OpenWord> wordDao = BookUtils.getDaoImpl();

		int count = wordDao.getTotalCount();
		if(tv_all_num==null)
			tv_all_num = (TextView) rootView.findViewById(R.id.tv_all_num);
		tv_all_num.setText(count+"");
		int remenber = wordDao.getTotalCount(WordDBHelper.BOOLEAN_TRUE);
		tv_re_num.setText(remenber+"");
		rb_progress.setText(BookUtils.getLevelLable());
		rb_progress.setMax(count);
		rb_progress.setProgress(remenber);
		tv_renwu_num.setText(MyApplication.property.renwu_num+"");
		//复习
		if(MyApplication.property.fxms == Property.VALUE_FXMS_SJ){
			//随机模式有数量设置
			if(remenber<MyApplication.property.fx_num)
				tv_fuxi_num.setText(remenber+"");  
			else
				tv_fuxi_num.setText(MyApplication.property.fx_num+"");  
		}else{
			//复习前一日的单词
			RenwuJiluDao fuxidao = new RenwuJiluDao();
			List<RenwuJilu> renwus = fuxidao.getFuxiWord();
			int fuxiNum = 0;
			if(renwus.size()>1){
				fuxi = renwus.get(1);
				fuxiNum = fuxi.getCount();
			}
			tv_fuxi_num.setText(fuxiNum+"");
		}
	
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_sentence:
			intent = new Intent(mContext, ActivitySentence.class);
			getActivity().startActivity(intent);
			break;
		case R.id.ll_renwujilu:
//			startActivity(new Intent(mContext, ActivityRenwuJilu.class));
			getActivity().startActivityForResult(new Intent(mContext, ActivitySortWord.class), MainActivity.REQUEST_COED_CIDIAN);
			break;
		case R.id.rl_renwu:
			intent = new Intent(mContext, ActivityXuexi.class);
			intent.putExtra("action", 1);
			getActivity().startActivityForResult(intent, MainActivity.REQUEST_COED_CIDIAN);
			break;
		case R.id.rl_fuxi:
			intent = new Intent(mContext, ActivityFuxi.class);
			getActivity().startActivityForResult(intent, MainActivity.REQUEST_COED_CIDIAN);
			if(MyApplication.property.fxms == Property.VALUE_FXMS_SJ){
			}else{
				if(fuxi!=null)
					intent.putExtra("date", fuxi.getDate());
			}
			break;
		default:
			break;
		}
	}
	
	
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); 
	}


}
