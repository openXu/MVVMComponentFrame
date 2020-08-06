package com.openxu.ui;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.waps.AdInfo;
import cn.waps.AppConnect;

import com.openxu.english.R;
import com.openxu.view.DownLoadAdDialog;
import com.openxu.view.DownLoadAdDialog.DownLoadAdListener;

/**
 * @author openXu
 * 自定义广告
 */
public class ActivityMyAd extends BaseActivity{


	private TextView tv_no;
	private ListView lv_list;
	private MyAdAdapter adapter;
	private List<AdInfo> adInfoList;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_my_ad);
		tv_no = (TextView) findViewById(R.id.tv_no);
		lv_list = (ListView) findViewById(R.id.lv_list);
		adapter = new MyAdAdapter();
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AdInfo adInfo = (AdInfo) adapter.getItem(position);
				//当广告被点击时，显示广告详情
				AppConnect.getInstance(mContext).clickAd(mContext, adInfo.getAdId());
			}
		});
		
		tv_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_no.setVisibility(View.GONE);
				initData();
			}
		});
	}
	
	@Override
	protected void initData() {
		adInfoList = AppConnect.getInstance(this).getAdInfoList();
		if(adInfoList!=null&&adInfoList.size()>0){
			lv_list.setVisibility(View.VISIBLE);
			tv_no.setVisibility(View.GONE);
		}else{
			lv_list.setVisibility(View.GONE);
			tv_no.setVisibility(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();
	}
	
	class Holder{
		LinearLayout ll_root;
		ImageView iv_icon;
		TextView tv_name, tv_type, tv_size, tv_detial, tv_click;
	}
	
	class MyAdAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return adInfoList==null?0:adInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return adInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			final AdInfo info = adInfoList.get(position);
			if(convertView==null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_ad, null);
				holder = new Holder();
				holder.ll_root = (LinearLayout) convertView.findViewById(R.id.ll_root);
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.tv_detial = (TextView) convertView.findViewById(R.id.tv_detial);
				holder.tv_click = (TextView) convertView.findViewById(R.id.tv_click);
				holder.ll_root.setBackgroundResource(MyApplication.pf.item_selector);
				holder.tv_name.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.iv_icon.setImageBitmap(info.getAdIcon());  //广告图标
			holder.tv_name.setText(info.getAdName());         //广告标题
			holder.tv_type.setText("("+info.getProvider()+")");    //应用提供商
			holder.tv_size.setText(info.getFilesize()+"M");       //安装包大小
			holder.tv_detial.setText(info.getAdText());       //广告语文字
			holder.tv_click.setText(info.getAction());        //动作字段
			
			holder.tv_click.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DownLoadAdDialog downLoad = new DownLoadAdDialog(mContext);
					downLoad.setName(info.getAdName());
					downLoad.setListener(new DownLoadAdListener() {
						@Override
						public void downLoad() {
							AppConnect.getInstance(mContext).downloadAd(mContext, info.getAdId());
						}
					});
					downLoad.show();
				}
			});
			return convertView;
		}
	}
	
	
}
