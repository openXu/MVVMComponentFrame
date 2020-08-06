package com.openxu.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.waps.AppConnect;

import com.openxu.db.base.DAO;
import com.openxu.db.base.MyWordDaoSupport;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.bean.WordBook;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.BookUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.OpenWordUtil;
import com.openxu.utils.PlayerManager;
import com.openxu.utils.Property;
import com.openxu.view.AddWordDialog;
import com.openxu.view.AddWordDialog.Listener;
import com.openxu.view.slidingfinish.SildingFinishLayout1;
import com.openxu.view.slidingfinish.SildingFinishLayout1.OnSildingFinishListener;

/**
 * @author openXu
 */
public class ActivityXuexi extends BaseActivity implements OnClickListener {

	private LinearLayout ll_loding;
	private ImageView iv_loding;
	private TextView tv_jinyan;
	private RelativeLayout rl_over;    //测试完成显示
	private RelativeLayout rl_content;    //单词展示区域
	private LinearLayout ll_finish;       //单词学习完毕，展示
	private TextView tv_test, tv_xuexi;   //去测试按钮
	private TextView tv_num;   //数标
	//单词展示
	private LinearLayout ll_content,ll_ph, ll_china;
	private TextView tv_english,tv_ph_en, tv_ph_am, tv_parts, tv_exchange, tv_sent;
	private ImageView iv_local_voice, iv_ph_en,iv_ph_am, iv_love;
	//控制按钮
	private LinearLayout ll_pre, ll_paly, ll_next;
	private TextView tv_play;
	private ImageView iv_play;
	
	private DAO<OpenWord> wordDao;
	
	private ArrayList<OpenWord> dbWords1;
	private ArrayList<OpenWord> dbWords2;
	private int count;
	
	private Timer timer;
	private int index = 0;
	private int play_sp;

	private SildingFinishLayout1 mSildingFinishLayout;
	private int adNum;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_xuexi);
		ll_loding = (LinearLayout) findViewById(R.id.ll_loding);
		iv_loding = (ImageView) findViewById(R.id.iv_loding);
		rl_over = (RelativeLayout) findViewById(R.id.rl_over);
		tv_jinyan = (TextView) findViewById(R.id.tv_jinyan);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		ll_finish = (LinearLayout) findViewById(R.id.ll_finish);
		tv_xuexi = (TextView) findViewById(R.id.tv_xuexi);
		tv_test = (TextView) findViewById(R.id.tv_test);
		tv_num = (TextView) findViewById(R.id.tv_num);
		
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		ll_ph = (LinearLayout) findViewById(R.id.ll_ph);
		ll_china = (LinearLayout) findViewById(R.id.ll_china);
		ll_content.setVisibility(View.VISIBLE);
		tv_english = (TextView) findViewById(R.id.tv_english);
		tv_ph_en = (TextView) findViewById(R.id.tv_ph_en);
		tv_ph_am = (TextView) findViewById(R.id.tv_ph_am);
		tv_parts = (TextView) findViewById(R.id.tv_parts);
		tv_sent = (TextView) findViewById(R.id.tv_sent);
		tv_exchange = (TextView) findViewById(R.id.tv_exchange);
		iv_local_voice = (ImageView) findViewById(R.id.iv_local_voice);
		iv_ph_en = (ImageView) findViewById(R.id.iv_ph_en);
		iv_ph_am = (ImageView) findViewById(R.id.iv_ph_am);
		iv_love = (ImageView) findViewById(R.id.iv_love);
		
		ll_pre = (LinearLayout) findViewById(R.id.ll_pre);
		ll_paly = (LinearLayout) findViewById(R.id.ll_paly);
		ll_next = (LinearLayout) findViewById(R.id.ll_next);
		tv_play = (TextView) findViewById(R.id.tv_play);
		iv_play = (ImageView) findViewById(R.id.iv_play);

		tv_test.setClickable(true);
		tv_xuexi.setClickable(true);
		tv_test.setOnClickListener(this);
		tv_xuexi.setOnClickListener(this);
		iv_local_voice.setOnClickListener(this);
		ll_pre.setOnClickListener(this);
		ll_paly.setOnClickListener(this);
		ll_next.setOnClickListener(this);
		
		wordDao = BookUtils.getDaoImpl();
		
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 迷你广告调用方式
			AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
			AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.ad_text_color));//设置迷你广告文字颜色
			LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
			
			// 预加载插屏广告内容（仅在使用到插屏广告的情况，才需要添加）
			AppConnect.getInstance(this).initPopAd(this);
			AppConnect.getInstance(this).setPopAdBack(true);   //按返回键退出广告
		}
		
		mSildingFinishLayout = (SildingFinishLayout1) findViewById(R.id.rootView);
		mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
			@Override
			public void onSildingFinish() {
				boolean reuslt = onBackSelected();
				MyUtil.LOG_V(TAG, "已经花过头了。要finish了"+reuslt);
				if(!reuslt)
					finish();
			}
			@Override
			public void showPreWord() {
				showPre();
			}

			@Override
			public void showNextWord() {
				showNext();
			}
		});
		
	}
	
	//刷新播放按钮状态
	private void updatePlay(int state) {
		if(state==1){          //正在播放时
			tv_play.setText("暂停");
			iv_play.setImageResource(R.drawable.open_play_pause_selector);
			
		}else if(state==2){    //点击上一个  下一个或者  暂停后
			tv_play.setText("继续播放");
			iv_play.setImageResource(R.drawable.open_play_selector);
			
		}else if(state==3){    //播放完毕
			tv_play.setText("重新播放");
			iv_play.setImageResource(R.drawable.open_play_selector);
		}
	}

	@Override
	protected void setPf() {
		super.setPf();
		tv_test.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
		tv_xuexi.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
	}

	@Override
	protected void initData() {
		updatePlay(1);
		play_sp = MyApplication.property.play_sp;
		if(dbWords1!=null&&dbWords1.size()>0){
			ll_loding.setVisibility(View.GONE);
			rl_content.setVisibility(View.VISIBLE);
			ll_finish.setVisibility(View.GONE);
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							playWord(++index, true);
						}
					});
				}
			}, 0, play_sp*1000);
		}else{
			ll_loding.setVisibility(View.VISIBLE);
			rl_content.setVisibility(View.GONE);
			ll_finish.setVisibility(View.GONE);
			// 进度条转圈圈
			final Animation stateAnim = AnimationUtils.loadAnimation(mContext,R.anim.open_listviewloding_anim);
			LinearInterpolator lin = new LinearInterpolator();
			stateAnim.setInterpolator(lin);
			iv_loding.startAnimation(stateAnim);
			new AsyncTask<Void, Void, Integer>(){
				@Override
				protected Integer doInBackground(Void... params) {
					dbWords1 = new ArrayList<OpenWord>();
					dbWords2 = new ArrayList<OpenWord>();
					List<OpenWord> dbWords = null;
					if(MyApplication.property.jcms == Property.VALUE_JCMS_SJ){
						dbWords = wordDao.findRandom(MyApplication.property.renwu_num);
					}else{
						dbWords = wordDao.findShunxu(MyApplication.property.renwu_num);
					}
					//插入时间
					for(OpenWord word : dbWords){
						word.setDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
					}
					wordDao.updataAll(dbWords);
					
					for(OpenWord word : dbWords){
						if(word.getRemenber()==WordDBHelper.BOOLEAN_FALSE){
							dbWords1.add(word);
						}else{
							dbWords2.add(word);
						}
					}
					count = dbWords1.size();
					return 1;
				}
				protected void onPostExecute(Integer result) {
					stateAnim.cancel();
					if(dbWords1.size()==0){
						//全部完成了
						rl_over.setVisibility(View.VISIBLE);
						tv_jinyan.setText(Constant.REWARD_JY_TEST+"");
						ll_loding.setVisibility(View.GONE);
						rl_content.setVisibility(View.GONE);
					}else{
						rl_over.setVisibility(View.GONE);
						ll_loding.setVisibility(View.GONE);
						rl_content.setVisibility(View.VISIBLE);
						timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										playWord(++index, true);
									}
								});
							}
						}, 0,  play_sp*1000);
					}
				};
			}.execute();
		}
	}
	
	@Override
	protected boolean onMenuSelected(){
		if(dbWords1==null||dbWords1.size()<=0){
			return true;
		}
		if(timer!=null){
			//正在播放
			timer.cancel();
			timer = null;
			updatePlay(2);
		}
		Intent intent = new Intent(mContext, ActivityTest.class);
		intent.putParcelableArrayListExtra("dbWords1", dbWords1);
		intent.putParcelableArrayListExtra("dbWords2", dbWords2);
		startActivityForResult(intent, 1);
		return false;
	}
	
	//展示单词
	private void showWord(final OpenWord word) {
		if(index==count)
			mSildingFinishLayout.index = 1;
		else
			mSildingFinishLayout.index = index;
		mSildingFinishLayout.scrollOrigin();
		ll_content.setVisibility(View.VISIBLE);
		tv_english.setText(word.english);   //英文
		iv_love.setImageResource(R.drawable.love_1);
		//发音
		if(TextUtils.isEmpty(word.ph_en)&&TextUtils.isEmpty(word.ph_am)){
			ll_ph.setVisibility(View.GONE);
			iv_local_voice.setVisibility(View.VISIBLE);
		}else{
			ll_ph.setVisibility(View.VISIBLE);
			iv_local_voice.setVisibility(View.GONE);
			if(!TextUtils.isEmpty(word.ph_en)){   //英式
				tv_ph_en.setText(Html.fromHtml("<font color=\"#6EB0E5\">英</font> "+word.ph_en));
				tv_ph_en.setVisibility(View.VISIBLE);
				iv_ph_en.setVisibility(View.VISIBLE);
			}else{
				tv_ph_en.setVisibility(View.GONE);
				iv_ph_en.setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(word.ph_am)){
				tv_ph_am.setText(Html.fromHtml("<font color=\"#6EB0E5\">美</font> "+word.ph_am));
				tv_ph_am.setVisibility(View.VISIBLE);
				iv_ph_am.setVisibility(View.VISIBLE);
			}else{
				tv_ph_am.setVisibility(View.GONE);
				iv_ph_am.setVisibility(View.GONE);
			}
		}
		
		//中文tv_parts
		if(!TextUtils.isEmpty(word.parts)){
			String parts = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatParts(word.parts);
			tv_parts.setText(Html.fromHtml(parts));
			tv_parts.setVisibility(View.VISIBLE);
		}else{
			tv_parts.setVisibility(View.GONE);
		}
		//形式
		if(!TextUtils.isEmpty(word.exchange)){
			String exchangeStr = "<font color=\"#6EB0E5\">其他形式</font>"+"<br>" +word.exchange;
			tv_exchange.setText(Html.fromHtml(exchangeStr));
			tv_exchange.setVisibility(View.VISIBLE);
		}else{
			tv_exchange.setVisibility(View.GONE);
		}
		//例句tv_sent
		if(!TextUtils.isEmpty(word.sents)){
			String sentStr = OpenWordUtil.formatSent(word.sents, word.english);
			if(!TextUtils.isEmpty(sentStr)){
				tv_sent.setText(Html.fromHtml(sentStr));
				tv_sent.setVisibility(View.VISIBLE);
			}else
				tv_sent.setVisibility(View.GONE);
		}else{
			tv_sent.setVisibility(View.GONE);
		}
		//网络单词英式
		iv_ph_en.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voice(word.ph_en_mp3);
			}
		});
		//网络单词美式
		iv_ph_am.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voice(word.ph_am_mp3);
			}
		});
		
		//单词收藏
		final MyWordDaoSupport dao2 = new MyWordDaoSupport();
		final List<WordBook> books = dao2.findAllBook();
		final List<WordBook> mybook = new ArrayList<WordBook>();
		if(books==null||books.size()<=0){  //没有单词本
			iv_love.setImageResource(R.drawable.love_1);
		}else{
			dao2.searchWord(word.getEnglish(), mybook);  //从单词本中查询
			if(mybook!=null&&mybook.size()>0){
				iv_love.setImageResource(R.drawable.love_2);  //已经收藏
			}else{
				iv_love.setImageResource(R.drawable.love_1);
			}
		}
		iv_love.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(timer!=null){
					//正在播放
					timer.cancel();
					timer = null;
					updatePlay(2);
				}
				if(mybook!=null&&mybook.size()>0){
					//已经收藏的取消收藏
					for(WordBook book : mybook){
						int id = dao2.delete(book.getWordTable(), word.getEnglish());
						mybook.remove(book);
						iv_love.setImageResource(R.drawable.love_1);
					}
				}else{
					if(books==null||books.size()<=0){
						MyUtil.showToast(mContext, -1, "你还没有自己的单词本，先去添加单词本吧");
						return;
					}
					AddWordDialog dialog = new AddWordDialog(mContext, mybook, books);
					dialog.setTitle(word.getEnglish());
					dialog.setListener(new Listener() {
						@Override
						public void add(WordBook book) {
							word.setLev(book.getName());
							word.setAddDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
							dao2.insert(book.getWordTable(), word);
							mybook.add(book);
							iv_love.setImageResource(R.drawable.love_2);
						}
					});
					dialog.show();
				}
				
			}
		});
	}
	
	//发音（英式美式）
	private void voice(final String url){
		if(!NetWorkUtil.isNetworkAvailable(mContext)){
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		PlayerManager.getInstance().play(url);
	}
	
	private void playWord(int index, boolean showTost){
		if(count<=0){
			rl_content.setVisibility(View.GONE);
			ll_finish.setVisibility(View.VISIBLE);
			if(MyApplication.property.showAddJy(Property.KEY_JY_XUEXIDAY)){
				MyApplication.property.rewardJy(Constant.REWARD_JY_XUE, mContext, false);
				showRewardJyPo("完成新词学习", Constant.REWARD_JY_XUE);
			}
			return;
		}
		MyUtil.LOG_V(TAG, "一共"+count+"个单词,正在播放第"+index+"个");
		if(index <= 0){ //没有上一个了
			if(timer!=null)
				timer.cancel();
			updatePlay(3);
			if(showTost)
				MyUtil.showToast(mContext, -1, "已经是第一个单词");
			this.index = 1;
		}else if(index > count){
//			if(showTost)
//				MyUtil.showToast(mContext, -1, "已经是最后一个单词");
			if(timer!=null)
				timer.cancel();
			rl_content.setVisibility(View.GONE);
			ll_finish.setVisibility(View.VISIBLE);
			if(MyApplication.property.showAddJy(Property.KEY_JY_XUEXIDAY)){
				MyApplication.property.rewardJy(Constant.REWARD_JY_XUE, mContext, false);
				showRewardJyPo("完成新词学习", Constant.REWARD_JY_XUE);
			}
			this.index = count;
			updatePlay(3);
		}
		tv_num.setText(this.index+" / "+count+"  已完成"+dbWords2.size());
		showWord(dbWords1.get(this.index-1));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_local_voice:
			/*PLAY_STATE state = MyApplication.getApplication().player.getState();
			if (state != PLAY_STATE.STOPED) {
				Log.e(TAG, "上次正在合成，先结束");
				MyApplication.getApplication().player.cancel();
			}*/
			if(!NetWorkUtil.isNetworkAvailable(mContext)){
				MyUtil.showToast(mContext, R.string.no_net, "");
				return;
			}
			String eng = tv_english.getText().toString().trim();
			MyUtil.LOG_I(TAG, "使用讯飞播报："+eng);
			MyApplication.getApplication().player.play(eng);
			break;
		case R.id.ll_pre:    //上一个
			showPre();
			break;
		case R.id.ll_paly:   //播放、暂停
			if(timer!=null){
				//正在播放
				timer.cancel();
				timer = null;
				updatePlay(2);
				
				if(MyApplication.property.initAd()&&MyApplication.showAd&&adNum<=0){
					// 显示插屏广告
					AppConnect.getInstance(this).showPopAd(this);
					adNum ++;
				}
			}else{
				if(index >= count)   //重新播放
					index = 0;
				initData();
				updatePlay(1);
			}
			break;
		case R.id.ll_next:   //下一个
			showNext();
			break;
		case R.id.tv_test:
			Intent intent = new Intent(mContext, ActivityTest.class);
			intent.putParcelableArrayListExtra("dbWords1", dbWords1);
			intent.putParcelableArrayListExtra("dbWords2", dbWords2);
			startActivityForResult(intent, 1);
			break;
		case R.id.tv_xuexi:   //再学一遍
			index = 0;
			adNum = -1;
			initData();
			updatePlay(1);
			break;
		default:
			break;
		}
	}
	
	private void showPre(){
//		if(timer!=null)
//			timer.cancel();
//		timer = null;
//		updatePlay(2);
		index--;
		if(index == 1)  //到了第一个单词
			updatePlay(3);
		playWord(index, true);
	}
	private void showNext(){
		/*if(timer!=null)
			timer.cancel();
		timer = null;
		updatePlay(2);*/
		playWord(++index, true);
	}

	private void setData(){
		Intent intent = new Intent(); 
		intent.putParcelableArrayListExtra("words1", dbWords1);
		intent.putParcelableArrayListExtra("words2", dbWords2);		
		setResult(10, intent);
		finish();
	}
	@Override
	protected boolean onBackSelected() {
		setData();                    
		return true;
	}
	
	@Override
	public void onBackPressed() {
		setData();
	}
	
	@Override
	protected void onDestroy() {
		if(timer!=null)
			timer.cancel();
		timer = null;
		PlayerManager.getInstance().stop();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dbWords1 = data.getParcelableArrayListExtra("dbWords1");
		dbWords2 = data.getParcelableArrayListExtra("dbWords2");
		if(dbWords1==null||dbWords1.size()<=0){
			finish();
		}else{
			index = 0;
			count = dbWords1.size();
			initData();
		}
	}
	
}
