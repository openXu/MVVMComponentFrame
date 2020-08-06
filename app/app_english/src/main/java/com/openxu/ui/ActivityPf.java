package com.openxu.ui;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.utils.Property;
import com.openxu.view.ClearAdDialog;
import com.openxu.view.ClearAdDialog.ClearAdListener;

/**
 * @author openXu
 */
public class ActivityPf extends BaseActivity implements OnClickListener , UpdatePointsNotifier{

	private LinearLayout ll_pf1, ll_pf2, ll_pf3, ll_lock2, ll_lock3;
	private ImageView iv_ck1, iv_ck2, iv_ck3;
	private TextView tv_1, tv_2, tv_3, tv_lock2, tv_lock3;
	private int oldPf;
	
	private int lock2_point = 30;
	private int lock3_point = 40;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_pf);
		ll_pf1 = (LinearLayout) findViewById(R.id.ll_pf1);
		ll_pf2 = (LinearLayout) findViewById(R.id.ll_pf2);
		ll_pf3 = (LinearLayout) findViewById(R.id.ll_pf3);
		ll_lock2 = (LinearLayout) findViewById(R.id.ll_lock2);
		ll_lock3 = (LinearLayout) findViewById(R.id.ll_lock3);
		tv_lock2 = (TextView) findViewById(R.id.tv_lock2);
		tv_lock3= (TextView) findViewById(R.id.tv_lock3);
		iv_ck1 = (ImageView) findViewById(R.id.iv_ck1);
		iv_ck2 = (ImageView) findViewById(R.id.iv_ck2);
		iv_ck3 = (ImageView) findViewById(R.id.iv_ck3);
		tv_1 = (TextView) findViewById(R.id.tv_1);
		tv_2 = (TextView) findViewById(R.id.tv_2);
		tv_3 = (TextView) findViewById(R.id.tv_3);
		ll_pf1.setOnClickListener(this);
		ll_pf2.setOnClickListener(this);
		ll_pf3.setOnClickListener(this);
		
		tv_lock2.setText(lock2_point+"积分解锁");
		tv_lock3.setText(lock3_point+"积分解锁");
		if(!MyApplication.showAd){
			ll_lock2.setVisibility(View.GONE);
			ll_lock3.setVisibility(View.GONE);
		}else{
			if(MyApplication.property.getPfLock2()){
				ll_lock2.setVisibility(View.GONE);
			}else{
				ll_lock2.setVisibility(View.VISIBLE);
			}
			//朋友免费第三套皮肤
			if(Constant.isFriends){
				ll_lock3.setVisibility(View.GONE);
			}else{
				if(MyApplication.property.getPfLock3()){
					ll_lock3.setVisibility(View.GONE);
				}else{
					ll_lock3.setVisibility(View.VISIBLE);
				}
			}
		}
		
		
		oldPf = MyApplication.property.getPf();
		
		refreshSelect(oldPf);
		
	}
	
	private void refreshSelect(int selected) {
		iv_ck1.setImageResource(R.drawable.open_hf_icon_check_no);
		iv_ck2.setImageResource(R.drawable.open_hf_icon_check_no);
		iv_ck3.setImageResource(R.drawable.open_hf_icon_check_no);
		switch (selected) {
		case Property.VALUE_PF_1:
			iv_ck1.setImageResource(R.drawable.open_hf_icon_check_yes);
			break;
		case Property.VALUE_PF_2:
			iv_ck2.setImageResource(R.drawable.open_hf_icon_check_yes);
			break;
		case Property.VALUE_PF_3:
			iv_ck3.setImageResource(R.drawable.open_hf_icon_check_yes);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(MyApplication.showAd){
			// 从服务器端获取当前用户的虚拟货币.
			// 返回结果在回调函数getUpdatePoints(...)中处理
			AppConnect.getInstance(this).getPoints(this);
		}
	}
	
	@Override
	protected void initData() {
	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_pf1:
			if (MyApplication.property.getPf() != Property.VALUE_PF_1) {
				MyApplication.property.setPf(Property.VALUE_PF_1); // 存储皮肤ID
			}
			refreshSelect(MyApplication.property.getPf());
			setPf();
			break;
		case R.id.ll_pf2:
			setPfLock2();
			break;
		case R.id.ll_pf3:
			//朋友免费第三套皮肤
			if(Constant.isFriends){
				MyApplication.property.setPf(Property.VALUE_PF_3);
				refreshSelect(MyApplication.property.getPf());
				setPf();
			}else{
				setPfLock3();
			}
			break;
		default:
			break;
		}
		
	}
	
	private void setPfLock2(){
		if(MyApplication.property.getPfLock2()||!MyApplication.showAd){
			if (MyApplication.property.getPf() != Property.VALUE_PF_2) {
				MyApplication.property.setPf(Property.VALUE_PF_2);
				refreshSelect(MyApplication.property.getPf());
				setPf();
			}
			return;
		}else{
			ll_lock2.setVisibility(View.VISIBLE);
		}
		if(pointNum>=0){
			if(pointNum>=lock2_point){
				ClearAdDialog clearsdialog = new ClearAdDialog(mContext);
				clearsdialog.setText("您确定要解锁这套皮肤吗?这将耗费您"+lock2_point+"积分");
				clearsdialog.setOk("解锁");
				clearsdialog.setListener(new ClearAdListener() {
					@Override
					public void clear() {
						MyApplication.property.setPfLock2();
						ll_lock2.setVisibility(View.GONE);
						if(MyApplication.user!=null){
							MyApplication.user.setPoint(MyApplication.user.getPoint()-lock2_point);
						}
						// 消费虚拟货币.
						AppConnect.getInstance(mContext).spendPoints(lock2_point, ActivityPf.this);
						
						MyApplication.property.setPf(Property.VALUE_PF_2);
						refreshSelect(MyApplication.property.getPf());
						setPf();
					}
				});
				clearsdialog.show();
			}else{
				MyUtil.showToast(mContext, -1, "您的积分不够哦");
				// 显示推荐列表（综合）
				AppConnect.getInstance(this).showOffers(this);
			}
		}else{
			MyUtil.showToast(mContext, -1, "您的积分不够哦");
			// 显示推荐列表（综合）
			AppConnect.getInstance(this).showOffers(this);
		}
	}
	private void setPfLock3(){
		if(MyApplication.property.getPfLock3()||!MyApplication.showAd){
			if (MyApplication.property.getPf() != Property.VALUE_PF_3) {
				MyApplication.property.setPf(Property.VALUE_PF_3);
				refreshSelect(MyApplication.property.getPf());
				setPf();
			}
			return;
		}else{
			ll_lock3.setVisibility(View.VISIBLE);
		}
		if(pointNum>=0){
			if(pointNum>=lock3_point){
				ClearAdDialog clearsdialog = new ClearAdDialog(mContext);
				clearsdialog.setText("您确定要解锁这套皮肤吗?这将耗费您"+lock3_point+"积分");
				clearsdialog.setOk("解锁");
				clearsdialog.setListener(new ClearAdListener() {
					@Override
					public void clear() {
						MyApplication.property.setPfLock3();
						ll_lock3.setVisibility(View.GONE);
						if(MyApplication.user!=null){
							MyApplication.user.setPoint(MyApplication.user.getPoint()-lock3_point);
						}
						// 消费虚拟货币.
						AppConnect.getInstance(mContext).spendPoints(lock3_point, ActivityPf.this);
						
						MyApplication.property.setPf(Property.VALUE_PF_3);
						refreshSelect(MyApplication.property.getPf());
						setPf();
					}
				});
				clearsdialog.show();
			}else{
				MyUtil.showToast(mContext, -1, "您的积分不够哦");
				// 显示推荐列表（综合）
				AppConnect.getInstance(this).showOffers(this);
			}
		}else{
			MyUtil.showToast(mContext, -1, "您的积分不够哦");
			// 显示推荐列表（综合）
			AppConnect.getInstance(this).showOffers(this);
		}
	}
	

	private void doFinish() {
		Intent intent = new Intent();
		intent.putExtra("change", oldPf==MyApplication.property.getPf()? false : true);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		doFinish();
	}
	@Override
	protected boolean onBackSelected() {
		doFinish();
		return true;
	}
	
	
	//获取积分展示
	final Handler mHandler = new Handler();
	private int pointNum = -1;
	private String pointErr;
	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (titleView != null) {
				if(pointNum>=0){
					titleView.setTitle("皮肤    (我的积分:"+pointNum+")");
				}else{
					titleView.setTitle("皮肤");
				}
			}
		}
	};

	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		pointNum = pointTotal;
		mHandler.post(mUpdateResults);
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {
		pointErr = error;
		mHandler.post(mUpdateResults);
	}
	
}
