package com.openxu.ui;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.waps.AppConnect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.openxu.db.bean.Fanyi;
import com.openxu.english.R;
import com.openxu.ui.adapter.MyspinnerAdapter;
import com.openxu.utils.Constant;
import com.openxu.utils.MD5Utils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.SystemBarTintManager.SystemBarConfig;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class FragmentFanyi extends Fragment implements OnClickListener {

	private String TAG = "FragmentFanyi";
	private Context mContext;
	
	public LinearLayout ll_top;
	private View rootView;
	private TextView tv_type;
	private String[] types;
	private LinearLayout ll_go, ll_clear, ll_china, ll_voice, ll_copy,
			ll_share;
	private EditText et_text;
	private TextView tv_yw, tv_text;
	private Map<String, String> typeMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MyUtil.LOG_I(TAG, "翻译Fragment创建了");
		mContext = getActivity();
		rootView = inflater.inflate(R.layout.fragment_fanyi, container, false);
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
		tv_type = (TextView) rootView.findViewById(R.id.tv_type);
		ll_go = (LinearLayout) rootView.findViewById(R.id.ll_go);
		ll_china = (LinearLayout) rootView.findViewById(R.id.ll_china);
		ll_voice = (LinearLayout) rootView.findViewById(R.id.ll_voice);
		ll_copy = (LinearLayout) rootView.findViewById(R.id.ll_copy);
		ll_share = (LinearLayout) rootView.findViewById(R.id.ll_share);
		et_text = (EditText) rootView.findViewById(R.id.et_text);
		ll_clear = (LinearLayout) rootView.findViewById(R.id.ll_clear);
		tv_yw  = (TextView) rootView.findViewById(R.id.tv_yw);
		tv_text = (TextView) rootView.findViewById(R.id.tv_text);

		ll_china.setVisibility(View.INVISIBLE);
		
		types = mContext.getResources().getStringArray(R.array.fanyiType);
		adapter = new MyspinnerAdapter(mContext, null, types , MyspinnerAdapter.TYPE_2);  
        tv_type.setText((CharSequence) adapter.getItem(0));  
        typeMap = new HashMap<String, String>();
        typeMap.put("中文", "zh");
        typeMap.put("粤语", "yue");
        typeMap.put("英语", "en");
        typeMap.put("日语", "jp");
        typeMap.put("韩语", "kor");
        typeMap.put("西班牙语", "spa");
        typeMap.put("法语", "fra");
        typeMap.put("泰语", "th");
        typeMap.put("阿拉伯语", "ara");
        typeMap.put("俄罗斯语", "ru");
        typeMap.put("葡萄牙语", "pt");
        typeMap.put("文言文", "wyw");
        typeMap.put("白话文", "zh");
        typeMap.put("德语", "de");
        typeMap.put("意大利语", "it");
        typeMap.put("荷兰语", "nl");
        typeMap.put("希腊语", "el");

        tv_type.setOnClickListener(this);
		ll_go.setOnClickListener(this);
		ll_voice.setOnClickListener(this);
		ll_copy.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		ll_clear.setOnClickListener(this);
		
		et_text.setOnKeyListener(onKeyListener); 
		et_text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(et_text.getText().toString().trim()))
					ll_clear.setVisibility(View.INVISIBLE);
				else
					ll_clear.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		setPf();
		showAd();
		return rootView;
	}
	@Override
	public void onStart() {
		super.onStart();
		
	}
	public void showAd(){
		if(mContext!=null){
			if(MyApplication.property.initAd()&&MyApplication.showAd){
				// 迷你广告调用方式
				AppConnect.getInstance(mContext).setAdBackColor(mContext.getResources().getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
				AppConnect.getInstance(mContext).setAdForeColor(mContext.getResources().getColor(MyApplication.pf.ad_text_color));//设置迷你广告文字颜色
				LinearLayout miniLayout = (LinearLayout) rootView.findViewById(R.id.miniAdLinearLayout3);
				AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout, 10);// 10秒刷新一次
			}
		}
	}
	public void setAd(){
		MyUtil.LOG_I(TAG, "翻译fragment加广告");
		if(null!=rootView&&!MyApplication.property.initAd())
			rootView.findViewById(R.id.miniAdLinearLayout3).setVisibility(View.GONE);
	}
	
	protected void setPf() {
		if(rootView!=null){
			tv_type.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		}
	}

	public void updateData() {
	}

	
	//监听软键盘按键
	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String fromStr = et_text.getText().toString().trim();
				if (TextUtils.isEmpty(fromStr))
					return true;
				/* 隐藏软键盘 */
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(
							v.getApplicationWindowToken(), 0);
				}
//				fanyi(fromStr);
				fanyi_new(fromStr);
				return true;
			}
			return false;
		}
	};
	
	@Override
	public void onClick(View v) {
		String text = "";
		switch (v.getId()) {
		case R.id.tv_type:
            showWindow(tv_type, tv_type);  
			break;
		case R.id.ll_go:
			// 翻译
			String fromStr = et_text.getText().toString().trim();
			if (TextUtils.isEmpty(fromStr))
				return;
			InputMethodManager imm = (InputMethodManager )v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);     
	        if (imm.isActive())     
	            imm.hideSoftInputFromWindow(v.getApplicationWindowToken() , 0 );   
//			fanyi(fromStr);
	        fanyi_new(fromStr);
			break;
		case R.id.ll_voice:
			text = tv_text.getText().toString().trim();
			if(TextUtils.isEmpty(text))
				return;
			MyUtil.LOG_I(TAG, "读取翻译结果："+to);
			boolean isSport = MyApplication.getApplication().player.setVoiceMan(to);
			if(isSport){
				MyApplication.getApplication().player.play(text);
			}else{
				MyUtil.showToast(mContext, -1, "抱歉~目前不支持"+to_zh+"发音");
			}
			break;
		case R.id.ll_copy:
			text = tv_text.getText().toString().trim();
			if(TextUtils.isEmpty(text))
				return;
			// 复制
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			try {
				int version = Integer.valueOf(Build.VERSION.SDK);
				if (version >= 11) {
					Method method = cmb.getClass().getMethod("setText",CharSequence.class);
					method.invoke(cmb, text);
					MyUtil.showToast(mContext, -1, "已复制到剪切板");
				} else {
					MyUtil.showToast(mContext, -2, "您的系统不支持此功能");
				}
			} catch (Exception e) {
			}
			break;
		case R.id.ll_share:
			text = tv_text.getText().toString().trim();
			if(TextUtils.isEmpty(text))
				return;
			Intent intent=new Intent(Intent.ACTION_SEND); 
			intent.setType("text/plain"); 
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享"); 
			intent.putExtra(Intent.EXTRA_TEXT, text);  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(Intent.createChooser(intent, "选择要使用的应用"));
			break;
		case R.id.ll_clear:
			et_text.setText("");
			tv_text.setText("");
			ll_clear.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}
	
	private LinearLayout drawdownlayout;
	private ListView listView;
	private MyspinnerAdapter adapter;
	private PopupWindow popupWindow;
	private void showWindow(View view, final TextView txt) {
		drawdownlayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.myspinner_dropdown, null);
		listView = (ListView) drawdownlayout.findViewById(R.id.listView);
		listView.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
		listView.setAdapter(adapter);
		listView.setSelector(MyApplication.getApplication().pf.item_selector);
		popupWindow = new PopupWindow(view);
		// 设置弹框的宽度为布局文件的宽
		popupWindow.setWidth(tv_type.getWidth());
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击弹框外部，弹框消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(drawdownlayout);
		// 设置弹框出现的位置，在v的正下方横轴偏移textview的宽度，为了对齐~纵轴不偏移
		popupWindow.showAsDropDown(view, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss() {
//				spinnerlayout.setBackgroundResource(R.drawable.preference_single_item);
			}
		});
		// listView的item点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				txt.setText(types[arg2]);// 设置所选的item作为下拉框的标题
				// 弹框消失
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	private String from = "";
	private String to = "";
	private String to_zh = "";
	//原有的百度翻译开放API服务将保留至2016年1月31日
	private void fanyi(String q) {
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(getActivity(), R.string.no_net, "");
			return;
		}
		ll_china.setVisibility(View.VISIBLE);
		tv_yw.setText("正在进行翻译,请稍后...");
		tv_text.setText("");
		String type = tv_type.getText().toString();
		try {
			if("语言自动检测".equals(type)){
				from = "auto";
				to = "auto";
			}else{
				from = type.substring(0, type.indexOf("-")).trim();
				to_zh = type.substring(type.indexOf(">")+1).trim();
				from = typeMap.get(from);
				to = typeMap.get(to_zh);
			}
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String urlStr = Constant.URL_BAIDU_FANYI + "&q=" + q+ "&from="+from+"&to="+to;
		MyUtil.LOG_D(TAG, urlStr);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, urlStr,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						tv_yw.setText("译文");
						// {"from":"zh",
						// "to":"en",
						// "trans_result":[
						// {"src":"\u4eca\u5929","dst":"Today"},
						// {"src":"\u660e\u5929","dst":"Tomorrow"}]}
						MyUtil.LOG_E(TAG, "翻译结果：" + responseInfo.result);
						JSONObject json = JSON.parseObject(responseInfo.result);
						String trans = json.getString("trans_result");
						if (TextUtils.isEmpty(trans))
							return;
						List<Fanyi> fanyis = JSON.parseArray(trans, Fanyi.class);
						to = json.getString("to");
						showFanyi(fanyis);
					}
					@Override
					public void onStart() {
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						tv_yw.setText("翻译失败：" + msg);
						MyUtil.LOG_E(TAG, "翻译失败：" + msg);
					}
				});
	}
	
	/**
	 * 百度翻译API服务全新升级至百度翻译开放云平台
	 * 支持全球27个语种的高质量互译服务
	 * 新平台服务更稳定，更可靠，SLA高达99.99%
	 * 单次请求字符长度达100万，长文本不必再做字符截断
	 * 迁移步骤简单，接口管理更有序
	 * @param q
	 */
	private void fanyi_new(String q) {
		if (!NetWorkUtil.isNetworkAvailable(mContext)) {
			MyUtil.showToast(getActivity(), R.string.no_net, "");
			return;
		}
		ll_china.setVisibility(View.VISIBLE);
		tv_yw.setText("正在进行翻译,请稍后...");
		tv_text.setText("");
		String type = tv_type.getText().toString();
		try {
			if("语言自动检测".equals(type)){
				from = "auto";
				to = "auto";
			}else{
				from = type.substring(0, type.indexOf("-")).trim();
				to_zh = type.substring(type.indexOf(">")+1).trim();
				from = typeMap.get(from);
				to = typeMap.get(to_zh);
			}
//			q = URLEncoder.encode(q, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//1、将请求参数 APPID (appid), 翻译query(q), 随机数(salt), 按照 appid q salt的顺序拼接得到串1。
		Random random = new Random();
		int salt = random.nextInt(1000000000);
		String sign = Constant.APP_ID+q+salt;
		MyUtil.LOG_V(TAG, sign);
		//2、在串1后拼接由平台分配的私钥(secret key) 得到串2。
		sign += Constant.KEY_P;
		MyUtil.LOG_V(TAG, sign);
		//3、对串2做md5，得到sign。
		sign = MD5Utils.md5(sign);
		MyUtil.LOG_V(TAG, sign);
		//http://api.fanyi.baidu.com/api/trans/vip/translate?q=hello&appid=2015063000000001&salt=1435660288&from=en&to=zh&sign=2f7b6bfb034a64a978707bd303d20cce
		String urlStr = Constant.URL_BAIDU_FANYI_NEW +"&salt="+salt+"&sign="+sign+ "&q=" + q+ "&from="+from+"&to="+to;
		MyUtil.LOG_D(TAG, urlStr);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, urlStr,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						tv_yw.setText("译文");
						// {"from":"zh",
						// "to":"en",
						// "trans_result":[
						// {"src":"\u4eca\u5929","dst":"Today"},
						// {"src":"\u660e\u5929","dst":"Tomorrow"}]}
						MyUtil.LOG_E(TAG, "翻译结果：" + responseInfo.result);
						JSONObject json = JSON.parseObject(responseInfo.result);
						String trans = json.getString("trans_result");
						if (TextUtils.isEmpty(trans))
							return;
						List<Fanyi> fanyis = JSON.parseArray(trans, Fanyi.class);
						to = json.getString("to");
						showFanyi(fanyis);
					}
					@Override
					public void onStart() {
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						tv_yw.setText("翻译失败：" + msg);
						MyUtil.LOG_E(TAG, "翻译失败：" + msg);
					}
				});
	}
	

	private void showFanyi(List<Fanyi> fanyis) {
		if (fanyis != null && fanyis.size() > 0) {
			String china = "";
			if(fanyis.size()>1)
				for(Fanyi fanyi : fanyis)
					china += (fanyi.getDst()+"\n");
			else
				china = fanyis.get(0).getDst();
			MyUtil.LOG_E(TAG, "翻译结果：" + china);
			tv_text.setText(china);
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
