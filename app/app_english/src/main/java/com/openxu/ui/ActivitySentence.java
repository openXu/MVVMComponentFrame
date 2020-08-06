package com.openxu.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.my.ShareModel;
import cn.sharesdk.my.SharePopupWindow;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.waps.AppConnect;
import cn.waps.AppListener;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.OneSentence;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.db.impl.OneSentenceDaoImpl;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.HttpUtil;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.PlayerManager;
import com.openxu.view.XlistView.PullListView;
import com.openxu.view.XlistView.PullListView.IXListViewListener;

/**
 * @author openXu
 */
public class ActivitySentence extends BaseActivity implements
		IXListViewListener ,PlatformActionListener{

	private PullListView lv_list;
	private TextView tv_null;
	private OneSentenceDaoImpl dao;
	private List<OneSentence> sentenceList;
	private MyAdapter adapter;
    private SharePopupWindow share;
    
    private LinearLayout ll_loding;
	private ImageView iv_loding;
	private Animation stateAnim;
    private RelativeLayout rl_content;
    
	@Override
	protected void initView() {
		setContentView(R.layout.sentence_activity);
		TAG = "ActivitySentence";
		ll_loding = (LinearLayout) findViewById(R.id.ll_loding);
		iv_loding = (ImageView) findViewById(R.id.iv_loding);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		lv_list = (PullListView) findViewById(R.id.lv_list);
		tv_null = (TextView) findViewById(R.id.tv_null);
		tv_null.setVisibility(View.GONE);

		rl_content.setVisibility(View.GONE);
		ll_loding.setVisibility(View.VISIBLE);
		
		if (MyApplication.property.initAd()&&MyApplication.showAd) {
			// 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
			AppConnect.getInstance(mContext).setBannerAdNoDataListener(
					new AppListener() {
						@Override
						public void onBannerNoData() {
							MyUtil.LOG_W(TAG, "banner广告暂无可用数据");
						}
					});
			// 互动广告调用方式
			LinearLayout layout = (LinearLayout) findViewById(R.id.miniAdLinearLayout2);
			AppConnect.getInstance(mContext).showBannerAd(mContext, layout);
		}
	}

	@Override
	protected void setPf() {
		super.setPf();
	}

	@Override
	protected void initData() {
		
		// 进度条转圈圈
		stateAnim = AnimationUtils.loadAnimation(mContext,R.anim.open_listviewloding_anim);
		LinearInterpolator lin = new LinearInterpolator();
		stateAnim.setInterpolator(lin);
		iv_loding.startAnimation(stateAnim);
		
		dao = new OneSentenceDaoImpl();
		adapter = new MyAdapter(this);
		lv_list.setAdapter(adapter);
		lv_list.setRefreshEnable(true);
		lv_list.setXListViewListener(this);
		sentenceList = new ArrayList<OneSentence>();
		date = MyUtil.date2Str(new Date(), Constant.DATE_JS);
		onLoadMore();

		// Intent intent = new Intent(this,CoreService.class);
		// startService(intent);
		// conn = new MyConn();
		// bindService(intent, conn, BIND_AUTO_CREATE);
	}

	private boolean isFillingData = false;// 正在加载数据
	private final int findNum = 5;
	private String date; // 今天
	private boolean isFirst = true;
	private boolean showNoNet = false;

	@SuppressLint("NewApi") @SuppressWarnings("unchecked")
	@Override
	public void onLoadMore() {
		if (isFillingData)
			return;
		isFillingData = true;
		AsyncTask task = new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				int i = 0;
				do {
					MyUtil.LOG_I(TAG, "获取" + date + "每日一句");
					// 从数据库获取每日一句
					List<OneSentence> sentences = dao.findByCondition(WordDBHelper.TABLE_SENTENCE_DATE + " = ? ",new String[] { date }, null);
					if (sentences != null && sentences.size() > 0) {
						MyUtil.LOG_D(TAG, "从数据库查询每日一句：" + sentences.get(0));
						sentenceList.add(0, sentences.get(0));
						i++;
					} else {
						if (!NetWorkUtil.isNetworkAvailable(mContext)) {
							if (!showNoNet) {
								publishProgress(1);
								showNoNet = true;
							}
							return null;
						}
						// 传入参数：file //数据格式，默认（json），可选xml；date //标准化日期格式
						// 如：2013-05-06，
						// 如：http://open.iciba.com/dsapi/?date=2013-05-03
						// 如果 date为空 则默认取当日的，当日为空 取前一日的
						// type(可选) // last 和 next
						// 你懂的，以date日期为准的，last返回前一天的，next返回后一天的
//						String url = Constant.URL_JS_ONESENTENCE + "?date="+ MyUtil.getBeforDate(date, Calendar.YEAR, 1);提前一年
						String url = Constant.URL_JS_ONESENTENCE + "?date="+ date;
						String result = HttpUtil.doStringRequest(url);
						MyUtil.LOG_D(TAG, "服务器获取每日一句：" + url);
						MyUtil.LOG_I(TAG, "服务器获取每日一句：" + result);
						if (TextUtils.isEmpty(result)) {
							i++;
						} else {
							OneSentence one = JSON.parseObject(result,OneSentence.class);
							one.setDateline(date);
							MyUtil.LOG_E(TAG, "服务器获取每日一句：" + one);
							date = one.getDateline(); // 如果获取到了前一日的，要更新时间，避免重复
							dao.insert(one);
							i++;
							sentenceList.add(0, one);
						}
					}
					date = MyUtil.getBeforDate(date, Calendar.DATE, 1);
				} while (i < findNum);
				if (!isFirst) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				return null;
			};

			protected void onProgressUpdate(Integer... values) {
				MyUtil.showToast(mContext, R.string.no_net, "");
			};

			protected void onPostExecute(Void result) {
				rl_content.setVisibility(View.VISIBLE);
				ll_loding.setVisibility(View.GONE);
				lv_list.stopLoad(); // 停止加载，影藏刷新头
				if (sentenceList != null && sentenceList.size() != 0) {
					if (!isFirst)
						updataShow(true);
					else
						updataShow(false);
				} else {
					tv_null.setVisibility(View.VISIBLE);
					lv_list.setVisibility(View.VISIBLE);
				}
				isFirst = false;
				isFillingData = false;
			}
		};
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		}else{
			task.execute();
		}
	}

	/**
	 * 刷新ListView
	 * @param isSelect
	 *            是否定位到原来显示的位置
	 */
	private void updataShow(boolean isSelect) {
		// 保存当前第一个可见的item的索引和偏移量
		int nowCount = lv_list.getCount() - 1;
		View v = lv_list.getChildAt(0);
		int top = (v == null) ? 0 : v.getHeight();

		nowCount = sentenceList.size() - nowCount + 1;
		adapter.setData(sentenceList);

		if (isSelect) {
			// 根据上次保存的index和偏移量恢复上次的位置
			lv_list.setSelectionFromTop(nowCount, top);
		}
	}

	private void showShare(OneSentence sentence) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("www.baidu.com");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(sentence.getContent() + "\r\n" + sentence.getNote());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath(sentence.getPicture());//确保SDcard下面存在此张图片
		// 设置分享照片的url地址，如果没有可以不设置
		oks.setImageUrl(sentence.getPicture2());
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(sentence.getPicture2());
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用//程序的名称或者是站点名称的链接地址
		oks.setSiteUrl(sentence.getPicture2());

		// 启动分享GUI
		oks.show(this);

	}

	class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<OneSentence> list;

		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			list = new ArrayList<OneSentence>();
		}

		public void setData(List<OneSentence> sentences) {
			this.list.clear();
			this.list.addAll(sentences);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			return list == null ? null : list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final OneSentence sentence = list.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.sentence_item, null);
				holder = new ViewHolder();
				holder.iv_sentenceicon = (ImageView) convertView
						.findViewById(R.id.iv_sentenceicon);
				holder.tv_one_eng = (TextView) convertView
						.findViewById(R.id.tv_one_eng);
				holder.tv_one_time = (TextView) convertView
						.findViewById(R.id.tv_one_time);
				holder.tv_one_chi = (TextView) convertView
						.findViewById(R.id.tv_one_chi);
				holder.ll_langdu = (LinearLayout) convertView
						.findViewById(R.id.ll_langdu);
				holder.ll_share = (LinearLayout) convertView
						.findViewById(R.id.ll_share);
				holder.ll_share.setBackgroundResource(MyApplication.pf.item_selector);
				holder.ll_langdu.setBackgroundResource(MyApplication.pf.item_selector);
				holder.tv_share = (TextView) convertView
						.findViewById(R.id.tv_share);
				holder.tv_langdu = (TextView) convertView
						.findViewById(R.id.tv_langdu);
				holder.tv_share.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
				holder.tv_langdu.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			ImageLoader.getInstance().displayImage(sentence.getPicture2(),
					holder.iv_sentenceicon,
					MyApplication.getApplication().sentensOptions);
			holder.tv_one_eng.setText(sentence.getContent());
			holder.tv_one_chi.setText(sentence.getNote());
			holder.tv_one_time.setText(sentence.getDateline());
			holder.ll_langdu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!NetWorkUtil.isNetworkAvailable(mContext)) {
						MyUtil.showToast(mContext, R.string.no_net, "");
						return;
					}
					// 讯飞语音播报
					// PLAY_STATE state =
					// MyApplication.getApplication().player.getState();
					// if (state != PLAY_STATE.STOPED) {
					// Log.e(TAG, "上次正在合成，先结束");
					// MyApplication.getApplication().player.cancel();
					// }
					// MyApplication.getApplication().player.play(sentence.getContent());
					// 使用音频文件
					PlayerManager.getInstance().play(sentence.getTts());
				}
			});
			holder.ll_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!NetWorkUtil.isNetworkAvailable(mContext)) {
						MyUtil.showToast(mContext, R.string.no_net, "");
						return;
					}
					share = new SharePopupWindow(mContext, Platform.SHARE_IMAGE);
	                share.setPlatformActionListener(ActivitySentence.this);
	                ShareModel model = new ShareModel();
	                model.setTitle(mContext.getResources().getString(R.string.app_name));
	                model.setText(sentence.getContent()+"   \r\n"+sentence.getNote());
	                model.setImageUrl(sentence.getPicture2());
	                model.setUrl(Constant.downLoadUrl);   //万普
	                share.setShareModel(model);
	                share.showShareWindow();
	                // 显示窗口 (设置layout在PopupWindow中显示的位置)
	                share.showAtLocation(ActivitySentence.this.findViewById(R.id.rootView),
	                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		public ImageView iv_sentenceicon;
		public TextView tv_one_eng, tv_one_chi, tv_one_time, tv_share, tv_langdu;
		public LinearLayout ll_share, ll_langdu;
	}
	/**
	 * 分享回调
	 */
	@Override
	public void onCancel(Platform arg0, int arg1) {
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		MyUtil.LOG_V(TAG, "分享成功");
		MyUtil.showToast(mContext, -1, "分享成功");
		MyApplication.property.rewardJy(Constant.REWARD_JY_SHARE, mContext, false);
		showRewardJyPo("分享美句", Constant.REWARD_JY_SHARE);
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		MyUtil.showToast(mContext, -1, "分享失败"+arg2.getMessage());
		MyUtil.LOG_E(TAG, arg1+"分享失败"+arg2.getMessage());
	}
	@Override
	protected void onDestroy() {
		// MyApplication.getApplication().player.cancel();
		PlayerManager.getInstance().stop();
		super.onDestroy();
	}



}
