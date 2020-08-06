package com.openxu.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.openxu.db.bean.FaceText;
import com.openxu.english.R;
import com.openxu.receiver.MyMessageReceiver;
import com.openxu.ui.adapter.EmoViewPagerAdapter;
import com.openxu.ui.adapter.EmoteAdapter;
import com.openxu.ui.adapter.MessageChatAdapter;
import com.openxu.ui.adapter.NewRecordPlayClickListener;
import com.openxu.utils.Constant;
import com.openxu.utils.FaceTextUtils;
import com.openxu.utils.FileUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.view.EmoticonsEditText;
import com.openxu.view.SystemBarTintManager;
import com.openxu.view.TitleView;
import com.openxu.view.XlistView.PullListView;
import com.openxu.view.XlistView.PullListView.IXListViewListener;
import com.openxu.view.dialog.DialogTips;
import com.openxu.view.slidingfinish.SildingFinishLayout;
import com.openxu.view.slidingfinish.SildingFinishLayout.OnSildingFinishListener;
import com.umeng.message.PushAgent;
/**
 * 聊天界面
 * 用户消息反馈
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author openXu
 * @date 2015-11-23 下午3:28:49
 */
public class ChatActivityToDevelop extends Activity implements OnClickListener,
		IXListViewListener, EventListener {
	protected SystemBarTintManager tintManager;
	protected Context mContext;
	protected TitleView titleView;
	protected String TAG;
	
	private Button btn_chat_emo, btn_chat_send, btn_chat_add,btn_chat_keyboard, btn_speak, btn_chat_voice;
	PullListView mListView;
	EmoticonsEditText edit_user_comment;
	private static int MsgPagerNum;
	private LinearLayout layout_more, layout_emo, layout_add;
	private ViewPager pager_emo;
	private TextView tv_picture, tv_camera, tv_location;
	// 语音有关
	RelativeLayout layout_record;
	TextView tv_voice_tips;
	ImageView iv_record;
	private Drawable[] drawable_Anims;// 话筒动画

	BmobRecordManager recordManager;
	BmobUserManager userManager;
	BmobChatManager manager;
	BmobChatUser targetUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = getClass().getSimpleName();
		mContext = this;
		PushAgent.getInstance(mContext).onAppStart();
		MyApplication.getApplication().addActivity(this);
		//开启透明标题栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
    		tintManager.setStatusBarTintEnabled(true);
		}
		initData();
		initView();
		initTitleView();
		View view = findViewById(R.id.rootView);
		if(view instanceof SildingFinishLayout){
			SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) view;
			mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
				@Override
				public void onSildingFinish() {
					finish();
				}
			});
		}
		setPf();
	}
	private void initView() {
		setContentView(R.layout.activity_chat);
		manager = BmobChatManager.getInstance(this);
		MsgPagerNum = 0;
		initXListView();
		initBottomView();
		initVoiceView();
	}
	protected void setPf(){
		//设置title
		titleView.setTitleBackground(MyApplication.getApplication().pf.title_bg);
		titleView.setTitleMenuItemBack(MyApplication.getApplication().pf.title_item_selecter);
		titleView.setTitleTextColorResources(MyApplication.getApplication().pf.text_color);
	}
	protected void initTitleView() {
		titleView = (TitleView)findViewById(R.id.titleView);
		titleView.setTitle("我要提建议");
		titleView.setListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_title_back:
						finish();
					break;
				case R.id.ll_title_menu:
					break;
				default:
					break;
				}
			}
		});
	}
	
	boolean userGet = false;
	private void initData(){
		//清空开发者信息
		MyApplication.property.setOpneXuNum(0);
		
		userManager = BmobUserManager.getInstance(mContext);
		userManager.bindInstallationForRegister(MyApplication.user.getChatName());
		//①、查询openxu账户
		manager = BmobChatManager.getInstance(mContext);
		userManager.queryUserByName(Constant.openName, new FindListener<BmobChatUser>() {
	        @Override
	        public void onError(int arg0, String arg1) {
	        	MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
	            MyUtil.LOG_E(TAG, "未找到对方账号"+arg1);
	        }
	        @Override
	        public void onSuccess(List<BmobChatUser> arg0) {
	            if(arg0!=null && arg0.size()>0){
	            	targetUser = arg0.get(0);
	            	MyUtil.LOG_I(TAG, "找到对方账号了"+targetUser);
	            	userGet = true;
	            }else{
	            	MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
	            	MyUtil.LOG_E(TAG, "对方账户不存在");
	            }
	        }
	    });
	
	}
	
	private void initRecordManager(){
		// 语音相关管理器
		recordManager = BmobRecordManager.getInstance(this);
		// 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
			@Override
			public void onVolumnChanged(int value) {
				iv_record.setImageDrawable(drawable_Anims[value]);
			}
			@Override
			public void onTimeChanged(int recordTime, String localPath) {
				BmobLog.i("voice", "已录音长度:" + recordTime);
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
					// 需要重置按钮
					btn_speak.setPressed(false);
					btn_speak.setClickable(false);
					// 取消录音框
					layout_record.setVisibility(View.INVISIBLE);
					// 发送语音消息
					sendVoiceMessage(localPath, recordTime);
					//是为了防止过了录音时间后，会多发一条语音出去的情况。
					handler.postDelayed(new Runnable() {
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							btn_speak.setClickable(true);
						}
					}, 1000);
				}else{
				}
			}
		});
	}


	/**
	 * 初始化语音布局
	 * @Title: initVoiceView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceView() {
		layout_record = (RelativeLayout) findViewById(R.id.layout_record);
		tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		btn_speak.setOnTouchListener(new VoiceTouchListen());
		initVoiceAnimRes();
		initRecordManager();
	}

	/**
	 * 长按说话
	 * @ClassName: VoiceTouchListen
	 * @Description: TODO
	 * @author smile
	 * @date 2014-7-1 下午6:10:16
	 */
	class VoiceTouchListen implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!FileUtils.checkSdCard()) {
					MyUtil.showToast(mContext, -1, "发送语音需要sdcard支持！");
					return false;
				}
				try {
					v.setPressed(true);
					layout_record.setVisibility(View.VISIBLE);
					tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
					// 开始录音
					MyUtil.LOG_I(TAG, "开始录音"+targetUser.getObjectId());
					recordManager.startRecording(targetUser.getObjectId());
				} catch (Exception e) {
				}
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
					tv_voice_tips.setTextColor(Color.RED);
				} else {
					tv_voice_tips.setText(getString(R.string.voice_up_tips));
					tv_voice_tips.setTextColor(Color.WHITE);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				layout_record.setVisibility(View.INVISIBLE);
				try {
					if (event.getY() < 0) {// 放弃录音
						recordManager.cancelRecording();
						MyUtil.LOG_V(TAG, "放弃发送语音");
					} else {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) {
							// 发送语音文件
							MyUtil.LOG_V(TAG, "发送语音");
							sendVoiceMessage(recordManager.getRecordFilePath(targetUser.getObjectId()),recordTime);
						} else {// 录音时间过短，则提示录音过短的提示
							layout_record.setVisibility(View.GONE);
							showShortToast().show();
						}
					}
				} catch (Exception e) {
				}
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * 发送语音消息
	 * @Title: sendImageMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendVoiceMessage(String local, int length) {
		if(!userGet){
			MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
			return;
		}
			
		MyUtil.LOG_V(TAG, "给开发者发送语音："+targetUser.getObjectId());
		manager.sendVoiceMessage(targetUser, local, length,
				new UploadListener() {
					@Override
					public void onStart(BmobMsg msg) {
						refreshMessage(msg);
					}
					@Override
					public void onSuccess() {
						mAdapter.notifyDataSetChanged();
					}
					@Override
					public void onFailure(int error, String arg1) {
						MyUtil.LOG_E(TAG, "上传语音失败 -->arg1：" + arg1);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	Toast toast;

	/**
	 * 显示录音时间过短的Toast
	 * @Title: showShortToast
	 * @return void
	 * @throws
	 */
	private Toast showShortToast() {
		if (toast == null) {
			toast = new Toast(this);
		}
		View view = LayoutInflater.from(this).inflate(
				R.layout.include_chat_voice_short, null);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(50);
		return toast;
	}

	/**
	 * 初始化语音动画资源
	 * @Title: initVoiceAnimRes
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceAnimRes() {
		drawable_Anims = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6) };
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(Constant.openID,MsgPagerNum);
		return list;
	}

	/**
	 * 界面刷新
	 * @Title: initOrRefresh
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
				int size = initMsgData().size();
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
				}
				mListView.setSelection(mAdapter.getCount() - 1);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		} else {
			mAdapter = new MessageChatAdapter(this, initMsgData());
			mListView.setAdapter(mAdapter);
		}
	}

	private void initAddView() {
		tv_picture = (TextView) findViewById(R.id.tv_picture);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		tv_camera.setOnClickListener(this);
	}

	private void initBottomView() {
		// 最左边
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
		btn_chat_add.setOnClickListener(this);
		btn_chat_emo.setOnClickListener(this);
		// 最右边
		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		btn_chat_voice.setOnClickListener(this);
		btn_chat_keyboard.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		// 最下面
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		initAddView();
		initEmoView();

		// 最中间
		// 语音框
		btn_speak = (Button) findViewById(R.id.btn_speak);
		// 输入框
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.VISIBLE);
						btn_chat_send.setVisibility(View.GONE);
						btn_chat_keyboard.setVisibility(View.GONE);
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

	}

	List<FaceText> emos;

	/**
	 * 初始化表情布局
	 * @Title: initEmoView
	 * @Description: TODO
	 * @return void
	 * @throws
	 */
	private void initEmoView() {
		pager_emo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; ++i) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
	}

	private View getGridView(final int i) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivityToDevelop.this,list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
						int start = edit_user_comment.getSelectionStart();
						CharSequence content = edit_user_comment.getText()
								.insert(start, key);
						edit_user_comment.setText(content);
						// 定位光标位置
						CharSequence info = edit_user_comment.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}

			}
		});
		return view;
	}

	MessageChatAdapter mAdapter;
	
	private void initXListView() {
		mListView = (PullListView) findViewById(R.id.mListView);
		mListView.setRefreshEnable(true);
		mListView.setXListViewListener(this);
		mListView.setDividerHeight(0);
		initOrRefresh();
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				btn_chat_voice.setVisibility(View.VISIBLE);
				btn_chat_keyboard.setVisibility(View.GONE);
				btn_chat_send.setVisibility(View.GONE);
				return false;
			}
		});
		// 重发按钮的点击事件
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
				new MessageChatAdapter.onInternalClickListener() {
					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// 重发消息
						if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
								|| ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
							resendFileMsg(parentV, values);
						} else {
							resendTextMsg(parentV, values);
						}
					}
				});
	}

	@Override
	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				MsgPagerNum++;
				int total = BmobDB.create(ChatActivityToDevelop.this).queryChatTotalCount(Constant.openID);
				BmobLog.i("记录总数：" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					MyUtil.showToast(mContext, -1, "聊天记录加载完了哦!");
				} else {
					List<BmobMsg> msgList = initMsgData();
					updataShow(false, msgList);
				}
				mListView.stopLoad();
			}
		}, 1000);
	}
	
	/**
	 * 刷新ListView
	 * @param isFirst是否定位到原来显示的位置
	 */
	private void updataShow(boolean isFirst, List<BmobMsg> list) {
		// 保存当前第一个可见的item的索引和偏移量
		int nowCount = mListView.getCount() - 1;
		View v = mListView.getChildAt(0);
		int top = (v == null) ? 0 : v.getHeight();
		nowCount = list.size() - nowCount + 1;
		mAdapter.setList(list);
		// 根据上次保存的index和偏移量恢复上次的位置
		if (!isFirst) {
			mListView.setSelectionFromTop(nowCount, top);
		}
	}
	
	/**
	 * 重发文本消息
	 */
	private void resendTextMsg(final View parentV, final Object values) {
		if(!userGet){
			MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
			return;
		}
		MyUtil.LOG_V(TAG, "给开发者重发文本消息："+targetUser.getObjectId());
		BmobChatManager.getInstance(ChatActivityToDevelop.this).resendTextMessage(
				targetUser, (BmobMsg) values, new PushListener() {
					@Override
					public void onSuccess() {
						MyUtil.LOG_V(TAG, "发送成功");
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.VISIBLE);
						((TextView) parentV.findViewById(R.id.tv_send_status))
								.setText("已发送");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						MyUtil.LOG_E(TAG, "发送失败:" + arg1);
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 重发图片消息
	 * @Title: resendImageMsg
	 * @Description: TODO
	 * @param @param parentV
	 * @param @param values
	 * @return void
	 * @throws
	 */
	private void resendFileMsg(final View parentV, final Object values) {
		if(!userGet){
			MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
			return;
		}
		MyUtil.LOG_V(TAG, "给开发者重发图片："+targetUser.getObjectId());
		BmobChatManager.getInstance(ChatActivityToDevelop.this).resendFileMessage(
				targetUser, (BmobMsg) values, new UploadListener() {
					@Override
					public void onStart(BmobMsg msg) {
					}
					@Override
					public void onSuccess() {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.INVISIBLE);
						if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
							parentV.findViewById(R.id.tv_send_status)
									.setVisibility(View.GONE);
							parentV.findViewById(R.id.tv_voice_length)
									.setVisibility(View.VISIBLE);
						} else {
							parentV.findViewById(R.id.tv_send_status)
									.setVisibility(View.VISIBLE);
							((TextView) parentV
									.findViewById(R.id.tv_send_status))
									.setText("已发送");
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_emo:// 点击笑脸图标
			if (layout_more.getVisibility() == View.GONE) {
				showEditState(true);
			} else {
				if (layout_add.getVisibility() == View.VISIBLE) {
					layout_add.setVisibility(View.GONE);
					layout_emo.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
			if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.GONE);
				hideSoftInputView();
			} else {
				if (layout_emo.getVisibility() == View.VISIBLE) {
					layout_emo.setVisibility(View.GONE);
					layout_add.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.btn_chat_voice:// 语音按钮
			edit_user_comment.setVisibility(View.GONE);
			layout_more.setVisibility(View.GONE);
			btn_chat_voice.setVisibility(View.GONE);
			btn_chat_keyboard.setVisibility(View.VISIBLE);
			btn_speak.setVisibility(View.VISIBLE);
			hideSoftInputView();
			break;
		case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
			showEditState(false);
			break;
		case R.id.btn_chat_send:// 发送文本
			if(!userGet){
				MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
				return;
			}
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				MyUtil.showToast(mContext, -1, "请输入发送消息!");
				return;
			}
			boolean isNetConnected = NetWorkUtil.isNetworkAvailable(this);
			if (!isNetConnected) {
				MyUtil.showToast(mContext, R.string.network_tips, "");
				// return;
			}
			// 组装BmobMessage对象
			BmobMsg message = BmobMsg.createTextSendMsg(this, targetUser.getObjectId(), msg);
			message.setExtra("Bmob");
			// 默认发送完成，将数据保存到本地消息表和最近会话表中
			MyUtil.LOG_V(TAG, "给开发者发送文本："+targetUser.getObjectId());
			manager.sendTextMessage(targetUser, message);
			// 刷新界面
			refreshMessage(message);

			break;
		case R.id.tv_camera:// 拍照
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		default:
			break;
		}
	}

	private String localCameraPath = "";// 拍照后得到的图片地址

	/**
	 * 启动相机拍照 startCamera
	 * 
	 * @Title: startCamera
	 * @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(Constant.BMOB_PICTURE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				Constant.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * 选择图片
	 * @Title: selectImage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, Constant.REQUESTCODE_TAKE_LOCAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constant.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				MyUtil.LOG_I(TAG, "本地图片的地址：" + localCameraPath);
				imageEdit(localCameraPath);
//				sendImageMessage(localCameraPath);
				break;
			case Constant.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						if(cursor==null){
							MyUtil.showToast(mContext, -2, "找不到您想要的图片");
							return;
						}
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null
								|| localSelectPath.equals("null")) {
							MyUtil.showToast(mContext, -2, "找不到您想要的图片");
							return;
						}
						imageEdit(localSelectPath);
//						sendImageMessage(localSelectPath);
					}
				}
				break;
			case Constant.REQUESTCODE_TAKE_IMAGE_EDIT:   //图片编辑
				if (data != null) {
					String imagePath = data.getStringExtra("imagePath");
					MyUtil.LOG_V(TAG, "编辑后图片地址:" + imagePath);
				    sendImageMessage(imagePath);
				}
				break;
			}
		}
	}
	private void imageEdit(String imagePath) {
		Intent intent = new Intent(mContext, ActivityTyImage.class);
		intent.putExtra("imagePath", imagePath);
		startActivityForResult(intent,  Constant.REQUESTCODE_TAKE_IMAGE_EDIT);
	}
	/**
	 * 默认先上传本地图片，之后才显示出来 sendImageMessage
	 * @Title: sendImageMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendImageMessage(String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		if(!userGet){
			MyUtil.showToast(mContext, -1, "获取开发者信息失败,请检查网络");
			return;
		}
		MyUtil.LOG_V(TAG, "给开发者发送图片："+targetUser.getObjectId());
		manager.sendImageMessage(targetUser, local, new UploadListener() {

			@Override
			public void onStart(BmobMsg msg) {
				MyUtil.LOG_I(TAG, "开始上传onStart：" + msg.getContent() + ",状态："
						+ msg.getStatus());
				refreshMessage(msg);
			}

			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int error, String arg1) {
				MyUtil.LOG_E(TAG, "上传失败 -->arg1：" + arg1);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 根据是否点击笑脸来显示文本输入框的状态
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: 用于区分文字和表情
	 * @return void
	 * @throws
	 */
	private void showEditState(boolean isEmo) {
		edit_user_comment.setVisibility(View.VISIBLE);
		btn_chat_keyboard.setVisibility(View.GONE);
		btn_chat_voice.setVisibility(View.VISIBLE);
		btn_speak.setVisibility(View.GONE);
		edit_user_comment.requestFocus();
		if (isEmo) {
			layout_more.setVisibility(View.VISIBLE);
			layout_more.setVisibility(View.VISIBLE);
			layout_emo.setVisibility(View.VISIBLE);
			layout_add.setVisibility(View.GONE);
			hideSoftInputView();
		} else {
			layout_more.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	// 显示软键盘
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(edit_user_comment, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(Constant.openID);
		//清空消息未读数-这个要在刷新之后
		MyMessageReceiver.mNewNum=0;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 监听推送的消息
		// 停止录音
		if (recordManager.isRecording()) {
			recordManager.cancelRecording();
			layout_record.setVisibility(View.GONE);
		}
		// 停止播放录音
		if (NewRecordPlayClickListener.isPlaying
				&& NewRecordPlayClickListener.currentPlayListener != null) {
			NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				BmobMsg m = BmobChatManager.getInstance(ChatActivityToDevelop.this).getMessage(message.getConversationId(), message.getMsgTime());
				if (!uid.equals(targetUser.getObjectId()))// 如果不是当前正在聊天对象的消息，不处理
					return;
				mAdapter.add(m);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivityToDevelop.this).resetUnread(Constant.openID);
			}
		}
	};

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	
	/**
	 * 刷新界面
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(BmobMsg msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}

	
	@Override
	public void onMessage(BmobMsg message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		MyUtil.LOG_V(TAG, "用户聊天收到消息：getToId:"+message.getToId()+"  belongId:"+message.getBelongId());
		handler.sendMessage(handlerMsg);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected) {
			MyUtil.showToast(mContext, R.string.network_tips,"");
		}
	}

	@Override
	public void onAddUser(BmobInvitation invite) {
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
		if (conversionId.split("&")[1].equals(Constant.openID)) {
			// 修改界面上指定消息的阅读状态
			for (BmobMsg msg : mAdapter.getList()) {
				if (msg.getConversationId().equals(conversionId)
						&& msg.getMsgTime().equals(msgTime)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_more.getVisibility() == 0) {
				layout_more.setVisibility(View.GONE);
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hideSoftInputView();
		
	}
	
	/** 隐藏软键盘
	  * hideSoftInputView
	  * @Title: hideSoftInputView
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/** 显示下线的对话框
	  * showOfflineDialog
	  * @return void
	  * @throws
	  */
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this,"您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				BmobUserManager.getInstance(getApplicationContext()).logout();
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

}
