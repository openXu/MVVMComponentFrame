package com.openxu.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.waps.AppConnect;

import com.openxu.db.base.DAO;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.BookUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.OpenWordUtil;
import com.openxu.utils.PlayerManager;
import com.openxu.utils.Property;

/**
 * @author openXu
 */
public class ActivityRenwuPlay extends BaseActivity implements OnClickListener {

	private TextView tv_num;   //数标
	private LinearLayout ll_lable_ok, ll_lable_no;   //空提示
	//显示中文和忘记了按钮
	private LinearLayout ll_remenber, ll_showchina;
	private ImageView iv_remenber;
	private TextView tv_remenber, tv_showchina;
	//单词展示
	private LinearLayout ll_content,ll_ph, ll_china;
	private TextView tv_english,tv_ph_en, tv_ph_am, tv_parts, tv_exchange, tv_sent;
	private ImageView iv_local_voice, iv_ph_en,iv_ph_am;
	//控制按钮
	private LinearLayout ll_pre, ll_paly, ll_next;
	private TextView tv_play;
	private ImageView iv_play;
	
	private DAO<OpenWord> wordDao;
	
	private int action = 0;
	private ArrayList<OpenWord> dbWords1;
	private ArrayList<OpenWord> dbWords2;
	private int count;
	
	private Timer timer;
	private int index = 0;

	@Override
	protected void initView() {
		setContentView(R.layout.renwu_play_activity);
		TAG = "ActivityRenwuPlay";
		tv_num = (TextView) findViewById(R.id.tv_num);
		ll_lable_ok = (LinearLayout) findViewById(R.id.ll_lable_ok);
		ll_lable_no = (LinearLayout) findViewById(R.id.ll_lable_no);
		ll_lable_ok.setVisibility(View.GONE);
		ll_lable_no.setVisibility(View.GONE);
		
		ll_showchina = (LinearLayout) findViewById(R.id.ll_showchina);
		tv_showchina  = (TextView) findViewById(R.id.tv_showchina);
		tv_remenber = (TextView) findViewById(R.id.tv_remenber);
		ll_remenber = (LinearLayout) findViewById(R.id.ll_remenber);
		iv_remenber = (ImageView) findViewById(R.id.iv_remenber);
		
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
		
		ll_pre = (LinearLayout) findViewById(R.id.ll_pre);
		ll_paly = (LinearLayout) findViewById(R.id.ll_paly);
		ll_next = (LinearLayout) findViewById(R.id.ll_next);
		tv_play = (TextView) findViewById(R.id.tv_play);
		iv_play = (ImageView) findViewById(R.id.iv_play);

		iv_local_voice.setOnClickListener(this);
		ll_pre.setOnClickListener(this);
		ll_paly.setOnClickListener(this);
		ll_next.setOnClickListener(this);
		
		action = getIntent().getIntExtra("action", 0);
		wordDao = BookUtils.getDaoImpl();
		if(action==1){   //记单词模式
			ll_china.setVisibility(View.VISIBLE);
			ll_showchina.setVisibility(View.GONE);
			tv_remenber.setText("记住了");
			ll_remenber.setVisibility(View.GONE);
			iv_remenber.setImageResource(R.drawable.open_renwu_icon_jzl);
		}else if(action==2){
			ll_china.setVisibility(View.INVISIBLE);
			ll_showchina.setVisibility(View.VISIBLE);
			ll_showchina.setOnClickListener(this);
			tv_remenber.setText("忘记了");
			iv_remenber.setImageResource(R.drawable.open_renwu_icon_wjl);
		}
		ll_remenber.setOnClickListener(this);
		
		updatePlay(1);
		
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 迷你广告调用方式
			AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
			AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.title_bg));//设置迷你广告文字颜色
			LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
		}
		
	}
	
	@Override
	protected void initTitleView() {
		super.initTitleView();
		if(action==1){   //记单词模式
			titleView.setTitle("单词学习");
		}else if(action==2){
		}
		
	}

	//刷新播放按钮状态
	private void updatePlay(int state) {
		if(state==1){          //正在播放时
			tv_play.setText("暂停");
			iv_play.setImageResource(R.drawable.open_play_pause_selector);
			Dialog popAdDialog = AppConnect.getInstance(mContext).getPopAdDialog();
			if(popAdDialog!=null){
				MyUtil.LOG_I(TAG, "继续播放了，关闭广告");
				popAdDialog.dismiss();
			}
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
		ll_showchina.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
//		tv_showchina.setTextColor(getResources().getColor(MyApplication.getApplication().pf.text_color));
		ll_remenber.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
//		tv_remenber.setTextColor(getResources().getColor(MyApplication.getApplication().pf.text_color));
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
				count = (action==1?dbWords1.size() : dbWords2.size());
				return 1;
			}
			protected void onPostExecute(Integer result) {
				dialog.cancel();
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
				}, 0, 3000);
			};
		}.execute();
		
	}
	
	//展示单词
	private void showWord(final OpenWord word) {
		ll_content.setVisibility(View.VISIBLE);
		tv_english.setText(word.english);   //英文
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
			if(action==1){
				ll_content.setVisibility(View.GONE);
				ll_lable_no.setVisibility(View.GONE);
				ll_lable_ok.setVisibility(View.VISIBLE);
				tv_num.setText(count+" / "+count);
			}else if(action==2){
				ll_content.setVisibility(View.GONE);
				ll_lable_ok.setVisibility(View.GONE);
				ll_lable_no.setVisibility(View.VISIBLE);
				tv_num.setText(count+" / "+count);
			}
			return;
		}
		MyUtil.LOG_V(TAG, "一共"+count+"个单词,正在播放第"+index+"个");
		if(index == count){   //到了最后一个单词
			if(timer!=null)
				timer.cancel();
			updatePlay(3);
		}else if(index <= 0){ //没有上一个了
			if(timer!=null)
				timer.cancel();
			updatePlay(3);
			if(showTost)
				MyUtil.showToast(mContext, -1, "已经是第一个单词");
			this.index = 1;
		}else if(index > count){
			if(showTost)
				MyUtil.showToast(mContext, -1, "已经是最后一个单词");
			this.index = count;
			updatePlay(3);
		}
		if(action==1){   //记单词模式
			tv_num.setText(this.index+" / "+count);
			showWord(dbWords1.get(this.index-1));
		}else if(action==2){
			tv_num.setText(this.index+" / "+count);
			showWord(dbWords2.get(this.index-1));
			ll_china.setVisibility(View.GONE);
			ll_showchina.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_local_voice:
		/*	PLAY_STATE state = MyApplication.getApplication().player.getState();
			if (state != PLAY_STATE.STOPED) {
				Log.e(TAG, "上次正在合成，先结束");
				MyApplication.getApplication().player.cancel();
			}*/
			String eng = tv_english.getText().toString().trim();
			MyUtil.TOAST(mContext, "播报："+eng);
//			MyApplication.getApplication().player.play(eng);
			break;
		case R.id.ll_showchina:
			ll_china.setVisibility(View.VISIBLE);
			ll_showchina.setVisibility(View.GONE);
			break;
		case R.id.ll_remenber:
			if(timer!=null)
				timer.cancel();
			timer = null;
			updatePlay(2);
			if(action==1){   //记单词模式
				//记住了
				OpenWord word = dbWords1.get(index-1);
				word.setRemenber(WordDBHelper.BOOLEAN_TRUE);
				wordDao.updata(word);
				dbWords1.remove(index-1);
				dbWords2.add(word);
				count = (action==1?dbWords1.size() : dbWords2.size());
				playWord(index, false);
			}else if(action==2){
				//忘记了
				OpenWord word = dbWords2.get(index-1);
				word.setRemenber(WordDBHelper.BOOLEAN_FALSE);
				wordDao.updata(word);
				dbWords2.remove(index-1);
				dbWords1.add(word);
				count = (action==1?dbWords1.size() : dbWords2.size());
				playWord(index, false);
			}
			break;
		case R.id.ll_pre:    //上一个
			if(timer!=null)
				timer.cancel();
			timer = null;
			updatePlay(2);
			index--;
			if(index == 1)  //到了第一个单词
				updatePlay(3);
			playWord(index, true);
			break;
		case R.id.ll_paly:   //播放、暂停
			if(timer!=null){
				//正在播放
				timer.cancel();
				timer = null;
				updatePlay(2);
			}else{
				if(index >= count)   //重新播放
					index = 0;
				initData();
				updatePlay(1);
			}
			break;
		case R.id.ll_next:   //下一个
			if(timer!=null)
				timer.cancel();
			timer = null;
			updatePlay(2);
			playWord(++index, true);
			break;
		default:
			break;
		}
	}

	private void setData(){
		Intent intent = new Intent(); 
		intent.putExtra("action", action);
		intent.putParcelableArrayListExtra("words1", dbWords1);
		intent.putParcelableArrayListExtra("words2", dbWords2);		
		setResult(action, intent);
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
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		if(timer!=null)
			timer.cancel();
		timer = null;
		PlayerManager.getInstance().stop();
		super.onDestroy();
	}
	
}
