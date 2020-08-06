package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.AppListener;

import com.openxu.db.base.DAO;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.english.R;
import com.openxu.utils.BookUtils;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.OpenWordUtil;
import com.openxu.utils.PlayerManager;
import com.openxu.utils.Property;
import com.openxu.view.LineProgressBar;

/**
 * @author openXu
 */
public class ActivityTest extends BaseActivity{
	private DAO<OpenWord> wordDao;
	
	private LinearLayout ll_line;
	private LineProgressBar linebar;
	private TextView tv_pro, tv_count;
	
	private RelativeLayout rl_over;
	private ScrollView sv_content;
	private TextView tv_jy, tv_word;
	private ImageView iv_voice;
	private LinearLayout ll_a, ll_b, ll_c, ll_d;
	private TextView btn_tishi, tv_tishi, tv_a, tv_b, tv_c, tv_d, tv_zw, tv_brs;
	private ImageView iv_a, iv_b, iv_c, iv_d;
	private ArrayList<OpenWord> dbWords1;
	private ArrayList<OpenWord> dbWords2;
	private int count;
	@Override
	protected void initView() {
		setContentView(R.layout.activity_text_answer);
		TAG = "ActivityTest";
		rl_over = (RelativeLayout) findViewById(R.id.rl_over);
		sv_content = (ScrollView) findViewById(R.id.sv_content);
		tv_jy = (TextView) findViewById(R.id.tv_jy);
		linebar = (LineProgressBar) findViewById(R.id.linebar);
		ll_line = (LinearLayout) findViewById(R.id.ll_line);
		tv_pro = (TextView) findViewById(R.id.tv_pro);
		tv_count = (TextView) findViewById(R.id.tv_count);
		rl_over.setVisibility(View.GONE);
		sv_content.setVisibility(View.GONE);
		ll_line.setVisibility(View.GONE);
		
		tv_word = (TextView) findViewById(R.id.tv_word);
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		btn_tishi = (TextView) findViewById(R.id.btn_tishi);
		tv_tishi = (TextView) findViewById(R.id.tv_tishi);
		ll_a = (LinearLayout) findViewById(R.id.ll_a);
		ll_b = (LinearLayout) findViewById(R.id.ll_b);
		ll_c = (LinearLayout) findViewById(R.id.ll_c);
		ll_d = (LinearLayout) findViewById(R.id.ll_d);
		tv_a = (TextView) findViewById(R.id.tv_a);
		tv_b = (TextView) findViewById(R.id.tv_b);
		tv_c = (TextView) findViewById(R.id.tv_c);
		tv_d = (TextView) findViewById(R.id.tv_d);
		iv_a = (ImageView) findViewById(R.id.iv_a);
		iv_b = (ImageView) findViewById(R.id.iv_b);
		iv_c = (ImageView) findViewById(R.id.iv_c);
		iv_d = (ImageView) findViewById(R.id.iv_d);
		
		tv_zw = (TextView) findViewById(R.id.tv_zw);
		tv_brs = (TextView) findViewById(R.id.tv_brs);
		iv_voice.setOnClickListener(this);
		btn_tishi.setOnClickListener(this);
		ll_a.setOnClickListener(this);
		ll_b.setOnClickListener(this);
		ll_c.setOnClickListener(this);
		ll_d.setOnClickListener(this);
		tv_zw.setOnClickListener(this);
		tv_brs.setOnClickListener(this);
	}
	@Override
	protected void setPf() {
		super.setPf();
	}
	
	@Override
	protected void initData() {
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
			AppConnect.getInstance(mContext).setBannerAdNoDataListener(new AppListener() {
				@Override
				public void onBannerNoData() {
					MyUtil.LOG_W(TAG, "banner广告暂无可用数据");
				}
			});
			// 互动广告调用方式
			LinearLayout layout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(mContext).showBannerAd(mContext, layout);
		}
		
		wordDao = BookUtils.getDaoImpl();
		dbWords1 = getIntent().getParcelableArrayListExtra("dbWords1");
		dbWords2 = getIntent().getParcelableArrayListExtra("dbWords2");
		count = dbWords1.size()+dbWords2.size();
		linebar.setMaxCount(count);
		shwoTest();
	}

	private void shwoTest() {
		iv_a.setVisibility(View.INVISIBLE);
		iv_b.setVisibility(View.INVISIBLE);
		iv_c.setVisibility(View.INVISIBLE);
		iv_d.setVisibility(View.INVISIBLE);
		ll_a.setBackgroundResource(R.drawable.view_text_answer);
		ll_b.setBackgroundResource(R.drawable.view_text_answer);
		ll_c.setBackgroundResource(R.drawable.view_text_answer);
		ll_d.setBackgroundResource(R.drawable.view_text_answer);
		isOk = false;
		hasTest = false;
		List<OpenWord> list = getTestList();
		if(list==null||list.size()!=4){
			rl_over.setVisibility(View.VISIBLE);
			sv_content.setVisibility(View.GONE);
			ll_line.setVisibility(View.GONE);
			//加经验
			tv_jy.setText(Constant.REWARD_JY_TEST+"");
			if(MyApplication.property.showAddJy(Property.KEY_JY_TESTDAY)){
				MyApplication.property.rewardJy(Constant.REWARD_JY_TEST, mContext, false);
				showRewardJyPo("完成单词测试", Constant.REWARD_JY_TEST);
			}
		}else{
			rl_over.setVisibility(View.GONE);
			sv_content.setVisibility(View.VISIBLE);
			ll_line.setVisibility(View.VISIBLE);
			tv_tishi.setVisibility(View.GONE);
			btn_tishi.setVisibility(View.VISIBLE);
			tv_pro.setText((count-dbWords1.size())+"");
			tv_count.setText(count+"");
			linebar.setCurrentCount((count-dbWords1.size()));
			Random random = new Random();
			int a = (random.nextInt(100))%2;
			if(a==1){
				//展示英语单词
				tv_word.setText(word.getEnglish());
				iv_voice.setVisibility(View.VISIBLE);
				tv_a.setText(OpenWordUtil.getOnePart(list.get(0).getParts()));
				tv_b.setText(OpenWordUtil.getOnePart(list.get(1).getParts()));
				tv_c.setText(OpenWordUtil.getOnePart(list.get(2).getParts()));
				tv_d.setText(OpenWordUtil.getOnePart(list.get(3).getParts()));
			}else{
				//展示英语单词
				tv_word.setText(OpenWordUtil.getOnePart(word.getParts()));
				iv_voice.setVisibility(View.GONE);
				tv_a.setText(list.get(0).getEnglish());
				tv_b.setText(list.get(1).getEnglish());
				tv_c.setText(list.get(2).getEnglish());
				tv_d.setText(list.get(3).getEnglish());
			}
		}
	}

	private int okPosition;   //四个选项中，正确选项的索引
	private int position;     //当前测试单词的索引
	private OpenWord word;    //当前测试单词
	private boolean hasTest;   //当前选项是否已点击了
	private List<OpenWord> getTestList(){
		if(dbWords1==null || dbWords1.size()<=0)
			return null;
		Random random = new Random();
		position = random.nextInt(dbWords1.size());
		word = dbWords1.get(position);
		//获取三个错误答案
		List<OpenWord> list = wordDao.findTestRandom(word.get_id());
		okPosition = random.nextInt(4);
		//将正确答案单词随机插入到集合中
		if(list==null||list.size()!=3)
			return null;
		list.add(okPosition, word);
		return list;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.iv_voice:
			/*PLAY_STATE state = MyApplication.getApplication().player.getState();
			if (state != PLAY_STATE.STOPED) {
				Log.e(TAG, "上次正在合成，先结束");
				MyApplication.getApplication().player.cancel();
			}*/
			if(!NetWorkUtil.isNetworkAvailable(mContext)){
				MyUtil.showToast(mContext, R.string.no_net, "");
				return;
			}
			String am = word.getPh_am_mp3();  //美式
			String en = word.getPh_en_mp3();  //英式
			if(TextUtils.isEmpty(am)){  
				if(TextUtils.isEmpty(en)){  
					String eng = word.getEnglish();
					MyUtil.LOG_I(TAG, "使用讯飞播报："+eng);
					boolean isSport = MyApplication.getApplication().player.setVoiceMan("en");
					MyApplication.getApplication().player.play(eng);
				}else{
					MyUtil.LOG_I(TAG, "播放英式发音："+word.ph_en_mp3);
					PlayerManager.getInstance().play(word.ph_en_mp3);
				}
			}else{
				MyUtil.LOG_I(TAG, "播放美式发音："+word.ph_en_mp3);
				PlayerManager.getInstance().play(word.getPh_am_mp3());
			}
			break;
		case R.id.btn_tishi:
			tv_tishi.setText(OpenWordUtil.getSent(word.getSents()));
			tv_tishi.setVisibility(View.VISIBLE);
			btn_tishi.setVisibility(View.GONE);
			break;
		case R.id.ll_a:
			if(!hasTest)
				showAns(0);
			break;
		case R.id.ll_b:
			if(!hasTest)
				showAns(1);
			break;
		case R.id.ll_c:
			if(!hasTest)
				showAns(2);
			break;
		case R.id.ll_d:
			if(!hasTest)
				showAns(3);
			break;
		case R.id.tv_zw:
			if(isOk){
				//记住了
				word.setRemenber(WordDBHelper.BOOLEAN_TRUE);
				wordDao.updata(word);
				dbWords1.remove(position);
				dbWords2.add(word);
				shwoTest();
			}else{
				MyUtil.showToast(mContext, -1, "请先答题");
			}
			break;
		case R.id.tv_brs:
			Intent intent = new Intent(mContext, ActivityShowOneWord.class);
			intent.putExtra("action", 1);
			intent.putExtra("word", word);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						shwoTest();
					}
				});
			}
		}, 1000);
	}
	
	private boolean isOk;
	private void showAns(int index) {
		hasTest = true;
		if(index == okPosition){  
			isOk = true;
			//正确
			switch (index) {
			case 0:
				ll_a.setBackgroundResource(R.drawable.answerright);
				iv_a.setImageResource(R.drawable.right);
				iv_a.setVisibility(View.VISIBLE);
				break;
			case 1:
				ll_b.setBackgroundResource(R.drawable.answerright);
				iv_b.setImageResource(R.drawable.right);
				iv_b.setVisibility(View.VISIBLE);
				break;
			case 2:
				ll_c.setBackgroundResource(R.drawable.answerright);
				iv_c.setImageResource(R.drawable.right);
				iv_c.setVisibility(View.VISIBLE);
				break;
			case 3:
				ll_d.setBackgroundResource(R.drawable.answerright);
				iv_d.setImageResource(R.drawable.right);
				iv_d.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}else{
			switch (index) {
			case 0:
				ll_a.setBackgroundResource(R.drawable.answerwrong);
				iv_a.setImageResource(R.drawable.wrong);
				iv_a.setVisibility(View.VISIBLE);
				break;
			case 1:
				ll_b.setBackgroundResource(R.drawable.answerwrong);
				iv_b.setImageResource(R.drawable.wrong);
				iv_b.setVisibility(View.VISIBLE);
				break;
			case 2:
				ll_c.setBackgroundResource(R.drawable.answerwrong);
				iv_c.setImageResource(R.drawable.wrong);
				iv_c.setVisibility(View.VISIBLE);
				break;
			case 3:
				ll_d.setBackgroundResource(R.drawable.answerwrong);
				iv_d.setImageResource(R.drawable.wrong);
				iv_d.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			switch (okPosition) {
			case 0:
				iv_a.setImageResource(R.drawable.right);
				iv_a.setVisibility(View.VISIBLE);
				break;
			case 1:
				iv_b.setImageResource(R.drawable.right);
				iv_b.setVisibility(View.VISIBLE);
				break;
			case 2:
				iv_c.setImageResource(R.drawable.right);
				iv_c.setVisibility(View.VISIBLE);
				break;
			case 3:
				iv_d.setImageResource(R.drawable.right);
				iv_d.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(mContext, ActivityShowOneWord.class);
							intent.putExtra("word", word);
							startActivityForResult(intent, 0);
						}
					});
				}
			}, 500);
		}
	}
	
	private void setData(){
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra("dbWords1", dbWords1);
		intent.putParcelableArrayListExtra("dbWords2", dbWords2);
		setResult(0, intent);
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
		super.onDestroy();
	}
}
