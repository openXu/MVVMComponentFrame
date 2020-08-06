package com.openxu.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * @author openXu
 */
public class ActivityShowOneWord extends BaseActivity implements OnClickListener {

	private OpenWord word;
	private WordBook book;
	//单词展示
	private LinearLayout ll_content,ll_ph, ll_china;
	private TextView tv_english,tv_ph_en, tv_ph_am, tv_parts, tv_exchange, tv_sent;
	private ImageView iv_local_voice, iv_ph_en,iv_ph_am, iv_love;
	private int action;//1 测试错误  ，  //2查看收藏单词   //3、s所有单词
	private boolean love;
	
	private List<WordBook> books;
	private MyWordDaoSupport dao2;
	private List<WordBook> mybook;
	@Override
	protected void initView() {
		setContentView(R.layout.activity_show_oneword);
		action = getIntent().getIntExtra("action", 1);
		
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
		
		dao2 = new MyWordDaoSupport();
		if(action==1 || action==3){
//			iv_love.setImageResource(R.drawable.love_1);
			books = dao2.findAllBook();
			mybook = new ArrayList<WordBook>();
		}else if(action==2){
			iv_love.setImageResource(R.drawable.love_2);
			book = getIntent().getParcelableExtra("book");
			love = true;
		}
		iv_local_voice.setOnClickListener(this);
		iv_love.setOnClickListener(this);
		
		if(MyApplication.property.initAd()&&MyApplication.showAd){
			// 迷你广告调用方式
			AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
			AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.title_bg));//设置迷你广告文字颜色
			LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
		}
		
	}
	
	@Override
	protected void setPf() {
		super.setPf();
	}

	@Override
	protected void initTitleView() {
		super.initTitleView();
		titleView.setTitle(BookUtils.getBookName());
	}
	
	@Override
	protected void initData() {
		word = getIntent().getParcelableExtra("word");
		titleView.setTitle(word.getEnglish());
		if(action==1 || action==2){
			showWord(word);
		}else if(action==3){
			DAO<OpenWord> wordDao = BookUtils.getDaoImpl();
			List<OpenWord> words = wordDao.findByCondition(WordDBHelper.TABLE_ID + "=?", new String[]{word.get_id()+""}, null);
			if(words!=null&&words.size()>0){
				word = words.get(0);
				showWord(word);
			}
		}
		
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
		
		if(action==1 || action==3){
			//单词收藏
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
		}else if(action==2){
		}
		
	}
	
	//发音（英式美式）
	private void voice(final String url){
		if(!NetWorkUtil.isNetworkAvailable(mContext)){
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		PlayerManager.getInstance().play(url);
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
			boolean isSport = MyApplication.getApplication().player.setVoiceMan("en");
			MyApplication.getApplication().player.play(eng);
			break;
		case R.id.iv_love:
			if(action==2){  //我的单词
				MyWordDaoSupport dao = new MyWordDaoSupport();
				//删除
				if(love){
					int id = dao.delete(book.getWordTable(), word.get_id());
					iv_love.setImageResource(R.drawable.love_1);
					love = false;
				}else{
					word.setLev(book.getName());
					word.setAddDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
					dao.insert(book.getWordTable(), word);
					iv_love.setImageResource(R.drawable.love_2);
					love = true;
				}
			}else{
				
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
			break;
		default:
			break;
		}
	}

	
	@Override
	protected void onDestroy() {
		PlayerManager.getInstance().stop();
		super.onDestroy();
	}
	
}
