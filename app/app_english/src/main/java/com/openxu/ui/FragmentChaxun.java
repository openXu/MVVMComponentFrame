package com.openxu.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.AppListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.openxu.db.base.MyWordDaoSupport;
import com.openxu.db.bean.OpenPart;
import com.openxu.db.bean.OpenSent;
import com.openxu.db.bean.OpenSymbol;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.bean.WordBook;
import com.openxu.chaxun.bean.zhongwen.Mean;
import com.openxu.chaxun.bean.zhongwen.Part;
import com.openxu.chaxun.bean.zhongwen.Symbol;
import com.openxu.chaxun.bean.zhongwen.Zhongwen;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.db.impl.GetWordDaoImpl;
import com.openxu.db.impl.WordDaoImpl;
import com.openxu.english.R;
import com.openxu.ui.adapter.ChaciHintAdapter;
import com.openxu.utils.Constant;
import com.openxu.utils.HttpUtil;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.OpenWordUtil;
import com.openxu.view.AddWordDialog;
import com.openxu.view.AddWordDialog.Listener;
import com.openxu.view.LineBreakLayout;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.SystemBarTintManager.SystemBarConfig;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class FragmentChaxun extends Fragment implements OnClickListener {

	private String TAG = "FragmentChaxun";
	private Context mContext;
	private View rootView;
	public LinearLayout ll_top;
	
	private WordDaoImpl dao1;
	private GetWordDaoImpl dao3;
	
	private MyWordDaoSupport dao2;
	private List<WordBook> books;
	private MyBookReceiver receiver;
	private EditText et_text;
	private ImageView iv_xx;
	
	private String clickStr = "";
	private ListView lv_hint;
	private ChaciHintAdapter hintAdapter;
	
	private LineBreakLayout ll_history;
	
	private LinearLayout ll_search, ll_searching, ll_content_english, ll_content_china,ll_ph;
	private TextView tv_searching, tv_english,tv_ph_en, tv_ph_line,tv_ph_am, tv_parts, tv_exchange, tv_sent;
	private ImageView iv_local_voice, iv_ph_en,iv_ph_am, iv_love;
	
	private ImageView iv_zh_voice;
	private TextView tv_zh_name,tv_zh_symbols, tv_zh_sent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		rootView = inflater.inflate(R.layout.fragment_chaci, container, false);
		ll_top = (LinearLayout) rootView.findViewById(R.id.ll_top);
		//将title上面流出提示栏的高度
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
			SystemBarConfig config = tintManager.getConfig();
			LayoutParams ll = (LayoutParams) ll_top.getLayoutParams();
	        ll.height = config.getStatusBarHeight();
	        ll_top.setLayoutParams(ll);
//					titleView.setPadding(0, config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
		}
		
		et_text = (EditText) rootView.findViewById(R.id.et_text);
		iv_xx = (ImageView) rootView.findViewById(R.id.iv_xx);
		ll_search  = (LinearLayout) rootView.findViewById(R.id.ll_search);
		lv_hint = (ListView) rootView.findViewById(R.id.lv_hint);
		hintAdapter = new ChaciHintAdapter(mContext,new String[]{});
		lv_hint.setAdapter(hintAdapter);
		lv_hint.setVisibility(View.GONE);
		lv_hint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/* 隐藏软键盘 */
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(et_text.getApplicationWindowToken(), 0);
				}
				clickStr = (String) hintAdapter.getItem(position);
				et_text.setText(clickStr);
				et_text.setSelection(clickStr.length());
				lv_hint.setVisibility(View.GONE);
				ll_searching.setVisibility(View.VISIBLE);
				ll_content_english.setVisibility(View.GONE);
				jsSearch(clickStr);
			}
		});
		
		iv_xx.setVisibility(View.INVISIBLE);
		et_text.setOnKeyListener(onKeyListener); 
		et_text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = et_text.getText().toString().trim();
				if(TextUtils.isEmpty(text)){
					ll_searching.setVisibility(View.GONE);
					ll_content_english.setVisibility(View.GONE);
					ll_content_china.setVisibility(View.GONE);
					iv_xx.setVisibility(View.INVISIBLE);
					lv_hint.setVisibility(View.GONE);
					showHistory();
				}else{
					ll_history.setVisibility(View.GONE);
					iv_xx.setVisibility(View.VISIBLE);
					ll_content_china.setVisibility(View.GONE);
					if(text.matches("[a-zA-Z]+")){
//						jsSearch(text, true, true);
						if(!clickStr.equals(text)){
							getWords(text);
						}
					}else{
						showHistory();
					}
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		iv_xx.setOnClickListener(this);
		ll_search.setOnClickListener(this);
		
		ll_history = (LineBreakLayout) rootView.findViewById(R.id.ll_history);
		
		ll_searching = (LinearLayout) rootView.findViewById(R.id.ll_searching);
		ll_searching.setVisibility(View.GONE);
		tv_searching =  (TextView) rootView.findViewById(R.id.tv_searching);
		ll_content_english = (LinearLayout) rootView.findViewById(R.id.ll_content_english);
		ll_content_english.setVisibility(View.GONE);
		ll_ph = (LinearLayout) rootView.findViewById(R.id.ll_ph);
		tv_english = (TextView) rootView.findViewById(R.id.tv_english);
		tv_ph_en = (TextView) rootView.findViewById(R.id.tv_ph_en);
		tv_ph_line = (TextView) rootView.findViewById(R.id.tv_ph_line);
		tv_ph_am = (TextView) rootView.findViewById(R.id.tv_ph_am);
		tv_parts = (TextView) rootView.findViewById(R.id.tv_parts);
		tv_sent = (TextView) rootView.findViewById(R.id.tv_sent);
		tv_exchange = (TextView) rootView.findViewById(R.id.tv_exchange);
		iv_local_voice = (ImageView) rootView.findViewById(R.id.iv_local_voice);
		iv_ph_en = (ImageView) rootView.findViewById(R.id.iv_ph_en);
		iv_ph_am = (ImageView) rootView.findViewById(R.id.iv_ph_am);
		iv_love = (ImageView) rootView.findViewById(R.id.iv_love);
		tv_searching.setOnClickListener(this);
		
		//中文展示区
		ll_content_china = (LinearLayout) rootView.findViewById(R.id.ll_content_china);
		ll_content_china.setVisibility(View.GONE);
		
		tv_zh_name = (TextView) rootView.findViewById(R.id.tv_zh_name);
		tv_zh_symbols = (TextView) rootView.findViewById(R.id.tv_zh_symbols);
		tv_zh_sent = (TextView) rootView.findViewById(R.id.tv_zh_sent);
		iv_zh_voice = (ImageView) rootView.findViewById(R.id.iv_zh_voice);
		
		dao1 = new WordDaoImpl();
		dao2 = new MyWordDaoSupport();
		dao3 = new GetWordDaoImpl();
		books = dao2.findAllBook();
		setPf();
		
		receiver = new MyBookReceiver();
		IntentFilter filter = new IntentFilter(Constant.RECEIVER_BOOK_CHANGED);
		getActivity().registerReceiver(receiver, filter);
		
		showAd();
		return rootView;
	}
	@Override
	public void onStart() {
		super.onStart();
//		et_text.setText("");
//		ll_searching.setVisibility(View.INVISIBLE);
//		ll_content.setVisibility(View.INVISIBLE);
		String text = et_text.getText().toString().trim();
		if(TextUtils.isEmpty(text)){
			showHistory();
		}
		
	}
	public void showAd(){
		if(mContext!=null){
			if(MyApplication.property.initAd()&&MyApplication.showAd){
				// 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
				AppConnect.getInstance(mContext).setBannerAdNoDataListener(new AppListener() {
					@Override
					public void onBannerNoData() {
						MyUtil.LOG_W(TAG, "banner广告暂无可用数据");
					}
				});
				// 互动广告调用方式
				LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.miniAdLinearLayout2);
				AppConnect.getInstance(mContext).showBannerAd(mContext, layout);
			}
		}
	}
	
	public void setAd(){
		MyUtil.LOG_I(TAG, "查询fragment，加广告"+MyApplication.property.initAd());
		if(null!=rootView&&!MyApplication.property.initAd())
			rootView.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
	}

	protected void setPf() {
	}

	//监听软键盘按键
	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				/* 隐藏软键盘 */
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(
							v.getApplicationWindowToken(), 0);
				}
				search();
				return true;
			}
			return false;
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_search:
			search();
			break;
		case R.id.tv_searching:
			String text = tv_searching.getText().toString().trim();
			if(text.contains("..."))
				return;
			else if(text.contains(">>"))
				search();
			break;
		case R.id.iv_xx:
			et_text.setText("");
			break;
		
		default:
			break;
		}
	}

	private void search(){
		lv_hint.setVisibility(View.GONE);
		ll_searching.setVisibility(View.VISIBLE);
		ll_content_english.setVisibility(View.GONE);
		
		String fromStr = et_text.getText().toString().trim();
		if (TextUtils.isEmpty(fromStr))
			return;
		tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">正在查询中...</font>"));
		InputMethodManager imm = (InputMethodManager )getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     
        if (imm.isActive())     
            imm.hideSoftInputFromWindow(et_text.getApplicationWindowToken() , 0 );   
        String from = "";
        String to = "";
        try {
			if(fromStr.matches("[a-zA-Z]+")){
				jsSearch(fromStr);
				return;
			}else{
				from = "zh";
				to = "en";
			}
			fromStr = URLEncoder.encode(fromStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
//        searchZhForBaidu(fromStr, from, to);
        jsSearchZh(fromStr);
	}
	

	private void showHistory(){
		ll_history.removeAllViews();
		List<String> history = MyApplication.property.getSearchHistory();
		if(history!=null&&history.size()>0){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			for(final String h : history){
				View view = inflater.inflate(R.layout.item_search_history, null);
				final TextView tv_word = (TextView) view.findViewById(R.id.tv_word);
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						 MyApplication.property.deleteHistory(h);
						 showHistory();
					}
				});
				tv_word.setText(h);
				tv_word.setBackgroundResource(MyApplication.pf.search_bag);
				tv_word.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						et_text.setText(tv_word.getText().toString().trim());
						et_text.setSelection(tv_word.getText().toString().trim().length());
						lv_hint.setVisibility(View.GONE);
						ll_searching.setVisibility(View.VISIBLE);
						ll_content_english.setVisibility(View.GONE);
						
						search();
					}
				});
				ll_history.addView(view);
			}
			ll_history.setVisibility(View.VISIBLE);
		}else{
			ll_history.setVisibility(View.GONE);
		}
	}
	
	private int hintNum = 6;
	private void getWords(final String text){
		List<OpenWord> words = dao1.searchWords(text, hintNum);
		List<OpenWord> words1 = null;
		if(words.size()<hintNum){
			words1 = dao3.searchWords(text, hintNum-words.size());
		}
		int size = words.size();
		if(words1!=null&&words1.size()>0){
			size += words1.size();
		}
		String[] strs = new String[size];
		for(int i = 0; i<words.size(); i++){
			strs[i] = words.get(i).getEnglish();
		}
		if(words1!=null&&words1.size()>0){
			for(int i = 0; i<words1.size(); i++){
				strs[words.size()+i] = words1.get(i).getEnglish();
			}
		}
		if(strs.length>0){
			ll_searching.setVisibility(View.GONE);
			ll_content_english.setVisibility(View.GONE);
			lv_hint.setVisibility(View.VISIBLE);
			hintAdapter.setStrs(strs);
			ll_history.setVisibility(View.GONE);
		}else{
			lv_hint.setVisibility(View.GONE);
			tv_searching.setText(Html.fromHtml("<font color=\""+MyApplication.getApplication().pf.sents_en_color+"\">本地词库无结果，联网查询>></font>"));
			ll_content_english.setVisibility(View.GONE);
			ll_searching.setVisibility(View.VISIBLE);
		
			showHistory();
		}
	}
	
	/**
	 * 英语单词（金山词霸）
	 * 英语单词-->中文
	 */
	private void jsSearch(final String text) {
		ll_content_china.setVisibility(View.GONE);
		ll_history.setVisibility(View.GONE);

		List<WordBook> mybook = new ArrayList<WordBook>();
		OpenWord word = dao2.searchWord(text, mybook);  //从单词本中查询
		if(null==word){
			word = dao1.searchWord(text);     //从词库中查询
			MyUtil.LOG_V(TAG, "我的单词本中没有，从本地词库中查询"+word);
			if(null==word){
				word = dao3.searchWord(text);     //从下载词库中查询
			}
		}
		if(null!=word){
			showWord(word, mybook);
			return;
		}

		//先到本地查询，如果没有在联网
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(getActivity(), R.string.no_net, "");
			ll_searching.setVisibility(View.GONE);
			return;
		}
		
		new AsyncTask<Void, Void, Boolean>() {
			OpenWord word = new OpenWord();
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					String urlStr = Constant.URL_JS_CIDIAN + "&w=" + text;
					MyUtil.LOG_D(TAG, urlStr);
					String wordStr = HttpUtil.doStringRequest(urlStr);
					
					JSONObject json = JSON.parseObject(wordStr);
					MyUtil.LOG_D(TAG, "JSON:"+wordStr);
					word.setFirst(text.substring(0, 1).toUpperCase());   //首字母
					word.setEnglish(json.getString("word_name"));        //英文
//					word.setLev(MyApplication.property.tableName);       //级数
					word.setIsLocal(WordDBHelper.BOOLEAN_FALSE);  //是否本地
					word.setAddDate(MyUtil.date2Str(new Date(), Constant.DATE_DB));
					word.setRemenber(WordDBHelper.BOOLEAN_FALSE);//是否记住
					word.setDate("");     //日期
			        //复数： goes 过去式： went 过去分词： gone 现在分词： going 第三人称单数： goes
			        String exchange  = "";
			        if(json.containsKey("exchange")){
			        	JSONObject exchangeJson = JSON.parseObject(json.getString("exchange"));
			        	MyUtil.LOG_I(TAG, "形式："+json.getString("exchange"));
				        if(exchangeJson.containsKey("word_pl")){
				        	String word_pl = exchangeJson.getString("word_pl");
				        	if(!TextUtils.isEmpty(word_pl)){
				        		exchange += ("复数:"+word_pl+",");
				        	}
				        }
				        if(exchangeJson.containsKey("word_past")){
				        	String word_past = exchangeJson.getString("word_past");
				        	if(!TextUtils.isEmpty(word_past)){
				        		exchange += ("过去式:"+word_past+",");
				        	}
				        }
				        if(exchangeJson.containsKey("word_done")){
				        	String word_done = exchangeJson.getString("word_done");
				        	if(!TextUtils.isEmpty(word_done)){
				        		exchange += ("过去分词:"+word_done+",");
				        	}
				        }
				        if(exchangeJson.containsKey("word_ing")){
				        	String word_ing = exchangeJson.getString("word_ing");
				        	if(!TextUtils.isEmpty(word_ing)){
				        		exchange += ("现在分词:"+word_ing+",");
				        	}
				        }
				        if(exchangeJson.containsKey("word_third")){
				        	String word_third = exchangeJson.getString("word_third");
				        	if(!TextUtils.isEmpty(word_third)){
				        		exchange += ("第三人称单数:"+word_third+",");
				        	}
				        }
				        if(exchange.endsWith(",")){
				        	exchange = exchange.substring(0, exchange.lastIndexOf(","));
				        }
				        MyUtil.LOG_I(TAG, "形式："+exchange);
			        }
			        word.setExchange(exchange);
			        
			        //发音//中文
	        		String partsStr = "";
			        if(json.containsKey("symbols")){
			        	String symbolsStr = json.getString("symbols");
			        	MyUtil.LOG_I(TAG, "发音："+symbolsStr);
			        	List<OpenSymbol> symbols = JSON.parseArray(symbolsStr, OpenSymbol.class);
			        	if(symbols!=null&&symbols.size()>0){
			        		for(OpenSymbol symbol : symbols){
				        		if(!TextUtils.isEmpty(symbol.getPh_en())){
				        			word.setPh_en(symbol.getPh_en());
					        	}
				        		if(!TextUtils.isEmpty(symbol.getPh_en_mp3())){
				        			word.setPh_en_mp3(symbol.getPh_en_mp3());
					        	}
				        		if(!TextUtils.isEmpty(symbol.getPh_am())){
				        			word.setPh_am(symbol.getPh_am());
					        	}
				        		if(!TextUtils.isEmpty(symbol.getPh_am_mp3())){
				        			word.setPh_am_mp3(symbol.getPh_am_mp3());
					        	}
			        			List<OpenPart> parts = JSON.parseArray(symbol.getParts(), OpenPart.class);
			        			if(parts!=null&&parts.size()>0){
			        				for(OpenPart part : parts){
			        					partsStr +=(part.getPart()+part.getMeans()+",");
			        				}
			        			}
				        	}
			        		
			        		if(partsStr.endsWith(",")){
	        					partsStr = partsStr.substring(0, partsStr.lastIndexOf(","));
					        }
					        MyUtil.LOG_I(TAG, "中文："+partsStr);
					        word.setParts(partsStr);
			        	}
			        }
			        
			      //例句
			        //③从服务器获取单词
					InputStream inputStream = null;  
				    HttpURLConnection conn = null;  
			        String urlStr1 = Constant.URL_JS_CIDIAN_XML+"&w="+text;
			        try {  
			            URL url = new URL(urlStr1);  
			            if (url != null) {  
			            	conn = (HttpURLConnection) url.openConnection();  
			                // 设置连接网络的超时时间  
			            	conn.setConnectTimeout(5000);  
			            	conn.setDoInput(true);  
			                // 表示设置本次http请求使用GET方式请求  
			            	conn.setRequestMethod("GET");  
			                int responseCode = conn.getResponseCode();  
							if (responseCode == 200) {
								inputStream = conn.getInputStream();
								List<OpenSent> sents = new ArrayList<OpenSent>();
								OpenSent sent = null;
								XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
								parser.setInput(inputStream, "UTF-8"); // 设置输入流
								int eventType = parser.getEventType();
								while (eventType != XmlPullParser.END_DOCUMENT) {
									switch (eventType) {
									case XmlPullParser.START_TAG:
										if (parser.getName().equals("sent")) {
											sent = new OpenSent();
										} else if (parser.getName().equals("orig")) {
											eventType = parser.next();
											sent.setOrig(parser.getText());
										} else if (parser.getName().equals("trans")) {
											eventType = parser.next();
											sent.setTrans(parser.getText());
										} 
										break;
									case XmlPullParser.END_TAG:
										if (parser.getName().equals("sent")) {
											if(sents.size()<=2){
												sents.add(sent);
												sent = null;
											}
										}
										break;
									}
									eventType = parser.next();
								}
								//[{"":"","":""},{}]
								if(sents.size()>0){
									String sentStrs = "[";
									for(OpenSent sentaa : sents){
										String sentStr = "{\"orig\":\""+sentaa.getOrig()+"\","+"\"trans\":\""+sentaa.getTrans()+"\"}";
										sentStrs+=(sentStr+",");
									}
									if(sentStrs.endsWith(","))
										sentStrs = sentStrs.substring(0, sentStrs.lastIndexOf(","));
									sentStrs += "]"; 
									word.setSents(sentStrs);
								}
							}  
			            }  
			        } catch (Exception e) {  
			            e.printStackTrace();  
			        } finally {
						if (conn != null) {
							conn.disconnect();
						}
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				MyUtil.LOG_D(TAG, "爬出单词："+word); 
				if(null!=word&&!TextUtils.isEmpty(word.parts)&&!TextUtils.isEmpty(word.english))
		        	return true;
		        else 
		        	return false;
				  
			}
			
			protected void onPostExecute(Boolean result) {
				if(result)
					showWord(word, new ArrayList<WordBook>());
				else{
					MyUtil.showToast(mContext, -1, "未查询到结果");
					tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果</font>"));
					ll_content_english.setVisibility(View.GONE);
					showHistory();
				}
					
			};
		}.execute();
	}

	/**
	 * 展示英语单词
	 * @param word
	 */
	private void showWord(final OpenWord word, final List<WordBook> mybook) {
		ll_history.setVisibility(View.GONE);
		MyApplication.property.addHistory(word.getEnglish());
		try{
			ll_searching.setVisibility(View.GONE);
			ll_content_english.setVisibility(View.VISIBLE);
//			if(word.get_id()>0&&word.getIsLocal()==WordDBHelper.BOOLEAN_TRUE){  //本地
//				iv_addword.setVisibility(View.GONE);
//			}else if(word.getIsLocal()==WordDBHelper.BOOLEAN_FALSE){
//				iv_addword.setVisibility(View.VISIBLE);
//			}
			iv_love.setVisibility(View.VISIBLE);
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
					tv_ph_line.setVisibility(View.GONE);
					tv_ph_en.setVisibility(View.GONE);
					iv_ph_en.setVisibility(View.GONE);
				}
				if(!TextUtils.isEmpty(word.ph_am)){
					tv_ph_am.setText(Html.fromHtml("<font color=\"#6EB0E5\">美</font> "+word.ph_am));
					tv_ph_am.setVisibility(View.VISIBLE);
					iv_ph_am.setVisibility(View.VISIBLE);
				}else{
					tv_ph_line.setVisibility(View.GONE);
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
			//本地单词发音
			iv_local_voice.setOnClickListener(new OnClickListener() {  
				@Override
				public void onClick(View v) {
					if(!NetWorkUtil.isNetworkAvailable(mContext)){
						MyUtil.showToast(mContext, R.string.no_net, "");
						return;
					}
					MyUtil.LOG_I(TAG, "使用讯飞播报："+word.getEnglish());
					boolean isSport = MyApplication.getApplication().player.setVoiceMan("en");
					MyApplication.getApplication().player.play(word.getEnglish());
				}
			});
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
			if(books==null||books.size()<=0){  //没有单词本
				iv_love.setImageResource(R.drawable.love_1);
			}else{
				if(mybook!=null&&mybook.size()>0){
					iv_love.setImageResource(R.drawable.love_2);  //已经收藏
				}else{
					iv_love.setImageResource(R.drawable.love_1);
				}
			}
			iv_love.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mybook!=null&&mybook.size()>0){
						//已经收藏的取消收藏
						for(WordBook book : mybook){
							int id = dao2.delete(book.getWordTable(), word.getEnglish());
							mybook.remove(book);
							iv_love.setImageResource(R.drawable.love_1);
						}
					}else{
						if(books==null||books.size()<=0){
							((MainActivity)getActivity()).getoMyBook();
							MyUtil.showToast(mContext, -1, "你还没有自己的单词本，快去添加吧");
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
			
		}catch(Exception e){
		}
		
	}
	private void voice(final String url){
		if(!NetWorkUtil.isNetworkAvailable(mContext)){
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		new AsyncTask<Void, Void, Integer>() {
			MediaPlayer mp = new MediaPlayer();
			@Override
			protected Integer doInBackground(Void... params) {
				try {
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
	                 mp.setOnCompletionListener(new OnCompletionListener(){
	                    @Override
	                    public void onCompletion(MediaPlayer mp) {
	                        mp.release();
	                    }
	                 });
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	
	/**
	 * 百度词典
	 * 中文-->英语
	 * @param text
	 * @param from
	 * @param to
	 */
	private void searchZhForBaidu(String text, String from, String to) {
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(getActivity(), R.string.no_net, "");
			return;
		}
		tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">查询中...</font>"));
		ll_searching.setVisibility(View.VISIBLE);
		String urlStr = Constant.URL_BAIDU_CIDIAN + "&q=" + text+ "&from="+from+"&to="+to;
		MyUtil.LOG_D(TAG, "百度词典查中文"+urlStr);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, urlStr,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						MyUtil.LOG_D(TAG, "查询结果：" + responseInfo.result);
						try{
							JSONObject json = JSON.parseObject(responseInfo.result);
							String data = json.getString("data");
							if(!TextUtils.isEmpty(data)){
								OpenWord word = new OpenWord();
								if(data.startsWith("[")){
									JSONArray jsonArray = JSON.parseArray(data);
									if(jsonArray!=null&&jsonArray.size()>0){
										String pinyin = "";
										String partsStr = "";
										for(int i=0;i<jsonArray.size();i++){
											json = jsonArray.getJSONObject(i);
											pinyin += (json.getString("word_name"));   //拼音
									        if(json.containsKey("symbols")){
									        	String symbolsStr = json.getString("symbols");
									        	List<OpenSymbol> symbols = JSON.parseArray(symbolsStr, OpenSymbol.class);
									        	if(symbols!=null&&symbols.size()>0){
									        		for(OpenSymbol symbol : symbols){
										        		if(!TextUtils.isEmpty(symbol.ph_zh)){
										        			word.setPh_en(symbol.ph_zh);
											        	}
									        			List<OpenPart> parts = JSON.parseArray(symbol.getParts(), OpenPart.class);
									        			if(parts!=null&&parts.size()>0){
									        				for(OpenPart part : parts){
									        					partsStr +=(part.getMeans()+",");
									        				}
									        			}
										        	}
									        		if(partsStr.endsWith(",")){
							        					partsStr = partsStr.substring(0, partsStr.lastIndexOf(","));
											        }
									        	}
									        }
										}
										word.setEnglish(pinyin);
										word.setParts(partsStr);
										showChinaWordBaidu(word);
									}
								}else if(data.startsWith("{")){
									json = JSON.parseObject(data);
									word.setEnglish(json.getString("word_name"));
									String partsStr = "";
							        if(json.containsKey("symbols")){
							        	String symbolsStr = json.getString("symbols");
							        	List<OpenSymbol> symbols = JSON.parseArray(symbolsStr, OpenSymbol.class);
							        	if(symbols!=null&&symbols.size()>0){
							        		for(OpenSymbol symbol : symbols){
								        		if(!TextUtils.isEmpty(symbol.ph_zh)){
								        			word.setPh_en(symbol.ph_zh);
									        	}
							        			List<OpenPart> parts = JSON.parseArray(symbol.getParts(), OpenPart.class);
							        			if(parts!=null&&parts.size()>0){
							        				for(OpenPart part : parts){
							        					partsStr +=(part.getMeans()+",");
							        				}
							        			}
								        	}
							        		if(partsStr.endsWith(",")){
					        					partsStr = partsStr.substring(0, partsStr.lastIndexOf(","));
									        }
									        word.setParts(partsStr);
							        	}
							        	showChinaWordBaidu(word);
							        }
								}
							}
						}catch(Exception e){
							tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果"+e.getMessage()+"</font>"));
						}
						JSONObject json = JSON.parseObject(responseInfo.result);
						String errno = json.getString("errno");
						String errmsg = json.getString("errmsg");
						if(TextUtils.isEmpty(errno)){
							//{"errno":52305,"data":[],"to":"en","from":"zh","errmsg":"Service_exception"}
							tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果:"+errno+errmsg+"</font>"));
						}else{
							tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果</font>"));
						}
					}
					@Override
					public void onStart() {
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						MyUtil.showToast(mContext, -1, "未查询到结果");
						tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果"+msg+"</font>"));
					}
				});
	}

	/**
	 * 展示中文
	 * @param word
	 */
	private void showChinaWordBaidu(final OpenWord word) {
		ll_history.setVisibility(View.GONE);
		MyApplication.property.addHistory(word.english);
		 MyUtil.LOG_I(TAG, "中文词典："+word);
		ll_searching.setVisibility(View.GONE);
		ll_content_english.setVisibility(View.VISIBLE);
		iv_love.setVisibility(View.GONE);
		tv_exchange.setVisibility(View.GONE);
		tv_sent.setVisibility(View.GONE);
		tv_english.setText(word.english);   //中文
		//发音
		if(TextUtils.isEmpty(word.ph_en)){
			ll_ph.setVisibility(View.GONE);
			iv_local_voice.setVisibility(View.GONE);
		}else{
			ll_ph.setVisibility(View.VISIBLE);
			iv_local_voice.setVisibility(View.GONE);
			tv_ph_en.setText(Html.fromHtml("<font color=\"#6EB0E5\">拼音 </font> "+word.ph_en));
			tv_ph_en.setVisibility(View.VISIBLE);
			iv_ph_en.setVisibility(View.GONE);
			tv_ph_line.setVisibility(View.GONE);
			iv_ph_am.setVisibility(View.GONE);
			tv_ph_am.setVisibility(View.GONE);
		}
		//中文tv_parts
		if(!TextUtils.isEmpty(word.parts)){
			String parts = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatParts(word.parts);
			tv_parts.setText(Html.fromHtml(parts));
			tv_parts.setVisibility(View.VISIBLE);
		}else{
			tv_parts.setVisibility(View.GONE);
		}
		
	}
	
	
	static class Sent{
		public String orig;
		public String trans;
		public String getOrig() {
			return orig;
		}
		public void setOrig(String orig) {
			this.orig = orig;
		}
		public String getTrans() {
			return trans;
		}
		public void setTrans(String trans) {
			this.trans = trans;
		}
	}
	private Zhongwen zhongwen;
	private void jsSearchZh(final String text){
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(getActivity(), R.string.no_net, "");
			return;
		}
		ll_content_english.setVisibility(View.GONE);
		ll_content_china.setVisibility(View.GONE);
		tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">查询中...</font>"));
		ll_searching.setVisibility(View.VISIBLE);
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					zhongwen = new Zhongwen();
					String urlStr = Constant.URL_JS_CIDIAN + "&w=" + text;
					MyUtil.LOG_D(TAG, "金山词霸查中文:"+urlStr);
					String wordStr = HttpUtil.doStringRequest(urlStr);
					JSONObject json = JSON.parseObject(wordStr);
					zhongwen.word_name = json.getString("word_name");   //中文名
					zhongwen.symbols = new ArrayList<Symbol>();
					JSONArray symbolArray = json.getJSONArray("symbols");  //释义
					if(symbolArray!=null){
						 Iterator<Object> symbolit = symbolArray.iterator();
						 while(symbolit.hasNext()){
							 Symbol symbol = new Symbol();
							 JSONObject symbolJson = (JSONObject) symbolit.next();
							 symbol.word_symbol = symbolJson.getString("word_symbol");   //拼音
							 symbol.parts = new ArrayList<Part>();
							 JSONArray partArray = symbolJson.getJSONArray("parts");   //英语
							if(partArray!=null){
								 Iterator<Object> partit = partArray.iterator();
								 while(partit.hasNext()){
									 Part part = new Part();
									 JSONObject partJson = (JSONObject) partit.next();
									 part.part_name = partJson.getString("part_name");  //词性
									 part.means = new ArrayList<Mean>();
									 JSONArray meanArray = partJson.getJSONArray("means");   //英语
									if(meanArray!=null){
										 Iterator<Object> meanit = meanArray.iterator();
										 while(meanit.hasNext()){
											 Mean mean = new Mean();
											 JSONObject meanJson = (JSONObject) meanit.next();
											 mean.word_mean = meanJson.getString("word_mean");  //词性
											 part.means.add(mean);
										 }
									}
									symbol.parts.add(part);
								 }
							}
							zhongwen.symbols.add(symbol);
						 }
					}
					
					
			        //例句
			        //③从服务器获取单词
					InputStream inputStream = null;  
				    HttpURLConnection conn = null;  
			        String urlStr1 = Constant.URL_JS_CIDIAN_XML+"&w="+text;
			        String wordSent = HttpUtil.doStringRequest(urlStr1);
			        MyUtil.LOG_D(TAG, "中文:"+wordSent);
			        try {  
			            URL url = new URL(urlStr1);  
			            if (url != null) {  
			            	conn = (HttpURLConnection) url.openConnection();  
			                // 设置连接网络的超时时间  
			            	conn.setConnectTimeout(5000);  
			            	conn.setDoInput(true);  
			                // 表示设置本次http请求使用GET方式请求  
			            	conn.setRequestMethod("GET");  
			                int responseCode = conn.getResponseCode();  
							if (responseCode == 200) {
								inputStream = conn.getInputStream();
								List<Sent> sents = new ArrayList<Sent>();
								Sent sent = null;
								XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
								parser.setInput(inputStream, "UTF-8"); // 设置输入流
								int eventType = parser.getEventType();
								while (eventType != XmlPullParser.END_DOCUMENT) {
									switch (eventType) {
									case XmlPullParser.START_TAG:
										if (parser.getName().equals("sent")) {
											sent = new Sent();
										} else if (parser.getName().equals("orig")) {
											eventType = parser.next();
											sent.setOrig(parser.getText());
										} else if (parser.getName().equals("trans")) {
											eventType = parser.next();
											sent.setTrans(parser.getText());
										} 
										break;
									case XmlPullParser.END_TAG:
										if (parser.getName().equals("sent")) {
											if(sents.size()<=2){
												sents.add(sent);
												sent = null;
											}
										}
										break;
									}
									eventType = parser.next();
								}
								//[{"":"","":""},{}]
								if(sents.size()>0){
									String sentStrs = "[";
									for(Sent sentaa : sents){
										String sentStr = "{\"orig\":\""+sentaa.getOrig()+"\","+"\"trans\":\""+sentaa.getTrans()+"\"}";
										sentStrs+=(sentStr+",");
									}
									if(sentStrs.endsWith(","))
										sentStrs = sentStrs.substring(0, sentStrs.lastIndexOf(","));
									sentStrs += "]"; 
									zhongwen.sents = sentStrs;
								}
							}  
			            }  
			        } catch (Exception e) {  
			            e.printStackTrace();  
			        } finally {
						if (conn != null) {
							conn.disconnect();
						}
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				MyUtil.LOG_D(TAG, "爬出单词："+zhongwen); 
				if(null!=zhongwen&&!TextUtils.isEmpty(zhongwen.word_name))
		        	return true;
		        else 
		        	return false;
				  
			}
			
			protected void onPostExecute(Boolean result) {
				if(result)
					showChinaWordJS(zhongwen);
				else{
					MyUtil.showToast(mContext, -1, "未查询到结果");
					tv_searching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果</font>"));
					ll_content_china.setVisibility(View.GONE);
					showHistory();
				}
					
			};
		}.execute();
	
	}
	
	
	/**
	 * 展示中文
	 * @param word
	 */
	private void showChinaWordJS(final Zhongwen zhongwen) {
		ll_history.setVisibility(View.GONE);
		MyApplication.property.addHistory(zhongwen.word_name);
		ll_searching.setVisibility(View.GONE);
		ll_content_china.setVisibility(View.VISIBLE);
		tv_zh_name.setText(zhongwen.word_name);
		String symbols = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatZhongwenSymbols(zhongwen.symbols);
		tv_zh_symbols.setText(Html.fromHtml(symbols));
		iv_zh_voice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!NetWorkUtil.isNetworkAvailable(mContext)){
					MyUtil.showToast(mContext, R.string.no_net, "");
					return;
				}
				MyUtil.LOG_I(TAG, "使用讯飞播报："+zhongwen.word_name);
				boolean isSport = MyApplication.getApplication().player.setVoiceMan("zh");
				MyApplication.getApplication().player.play(zhongwen.word_name);
			}
		});
		MyApplication.property.addHistory(zhongwen.word_name);
		//例句tv_sent
		if(!TextUtils.isEmpty(zhongwen.sents)){
			String sentStr = OpenWordUtil.formatSentZh(zhongwen.sents, zhongwen.word_name);
			if(!TextUtils.isEmpty(sentStr)){
				tv_zh_sent.setText(Html.fromHtml(sentStr));
				tv_zh_sent.setVisibility(View.VISIBLE);
			}else
				tv_zh_sent.setVisibility(View.GONE);
		}else{
			tv_zh_sent.setVisibility(View.GONE);
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
	
	@Override
	public void onDestroy() {
		if(receiver!=null){
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	
	class MyBookReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			MyUtil.LOG_V(TAG, "监听到单词本变化了");
			books = dao2.findAllBook();
		}

	}
}
