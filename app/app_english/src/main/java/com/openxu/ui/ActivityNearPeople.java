package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.bean.ChatUser;
import com.openxu.english.R;
import com.openxu.ui.adapter.ViewHolder;
import com.openxu.utils.CollectionUtils;
import com.openxu.utils.MyUtil;
import com.openxu.view.WaterDrop.WaterDropListView;

/**
 *  附近的人列表
 */
public class ActivityNearPeople extends BaseActivity implements WaterDropListView.IWaterDropListViewListener, OnItemClickListener {

	WaterDropListView mListView;
	NearPeopleAdapter adapter;
	String from = "";

	List<ChatUser> nears = new ArrayList<ChatUser>();

	private double QUERY_KILOMETERS = 10;//默认查询10公里范围内的人
	@Override
	protected void initView() {
		setContentView(R.layout.activity_near_user);
	}
	
	@Override
	protected void setPf() {
		super.setPf();
	}
	@Override
	protected void initData() {
		initXListView();
	}

	private void initXListView() {
		mListView = (WaterDropListView) findViewById(R.id.mListView);
		mListView.setOnItemClickListener(this);
		mListView.setWaterDropListViewListener(this);
		mListView.setPullLoadEnable(true);
		
		// 设置监听器
		adapter = new NearPeopleAdapter(this, nears);
		mListView.setAdapter(adapter);
		initNearByList(false);
	}

	
	int curPage = 0;
	private void initNearByList(final boolean isUpdate){
		if(!isUpdate){
			dialog.setShowText("正在查询附近的人...");
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}
		
		if(!mApplication.getLatitude().equals("")&&!mApplication.getLongtitude().equals("")){
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double.parseDouble(mApplication.getLongtitude());
		/*queryKiloMetersListByPage(boolean isRefreshAction,
                    int page,
                    java.lang.String locationProperty,
                    double longtitude,
                    double latitude,
                    boolean isShowFriends,
                    double kilometers,
                    java.lang.String equalProperty,
                    java.lang.Object equalObj,
                    cn.bmob.v3.listener.FindListener<T> findCallback)
分页加载指定公里范围内用户：排除自己，是否排除好友由开发者决定，可以添加额外查询条件
参数:
isPull：是否是属于刷新动作：如果是上拉或者下拉动作则设在为true,其他设在为false -
page：当前查询页码 -
property：查询条件，自己定义的位置属性 -
longtitude：经度 -
latitude：纬度 -
isShowFriends：是否显示附近的好友 -
kilometers - ：公里数
equalProperty：自己定义的其他属性：使用方法AddWhereEqualTo对应的属性名称 -
equalObj：查询equalProperty属性对应的属性值 -
findCallback - ：回调*/
			//封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
			//此方法默认每页查询10条数据,若想查询多于10条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
			// 此方法是新增的查询指定10公里内的性别为女性的用户列表，默认包含好友列表
			//如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可
			userManager.queryKiloMetersListByPage(isUpdate,0,"location", longtitude, latitude, true,QUERY_KILOMETERS,"sex",null,new FindListener<ChatUser>() {
			//此方法默认查询所有带地理位置信息的且性别为女的用户列表，如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行
//			userManager.queryNearByListByPage(isUpdate,0,"location", longtitude, latitude, true,"sex",false,new FindListener<User>() {
				@Override
				public void onSuccess(List<ChatUser> arg0) {
					if (CollectionUtils.isNotNull(arg0)) {
						if(isUpdate){
							nears.clear();
						}
						nears.addAll(arg0);
						updataShow(false, nears);
						if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
							MyUtil.LOG_I(TAG,"附近的人搜索完成!");
						}else{
						}
					}else{
						MyUtil.LOG_E(TAG, "暂无附近的人!");
					}
					dialog.cancel();
					mListView.stopRefresh();
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					dialog.cancel();
					MyUtil.LOG_E(TAG,arg0+ "暂无附近的人!"+arg1);
					mListView.stopRefresh();
				}

			});
		}else{
			MyUtil.LOG_E(TAG, "获取位置失败");
			dialog.dismiss();
			mListView.stopRefresh();
		}
	}
	/** 查询更多
	  * @Title: queryMoreNearList
	  * @Description: TODO
	  * @param @param page 
	  * @return void
	  * @throws
	  */
	private void queryMoreNearList(int page){
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongtitude());
		//查询10公里范围内的性别为女的用户列表
		userManager.queryKiloMetersListByPage(true,page,"location", longtitude, latitude, true,QUERY_KILOMETERS,"sex",null,new FindListener<ChatUser>() {
		//查询全部地理位置信息且性别为女性的用户列表
//		userManager.queryNearByListByPage(true,page, "location", longtitude, latitude, true,"sex",false,new FindListener<User>() {
			@Override
			public void onSuccess(List<ChatUser> arg0) {
				if (CollectionUtils.isNotNull(arg0)) {
					nears.addAll(arg0);
					updataShow(true, nears);
				}
				mListView.stopRefresh();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				MyUtil.LOG_E(TAG, "查询更多附近的人出错:"+arg1);
				mListView.stopRefresh();
			}
		});
	}
	
	/**
	 * 刷新ListView
	 * @param isFirst是否定位到原来显示的位置
	 */
	private void updataShow(boolean isFirst, List<ChatUser> list) {
		// 保存当前第一个可见的item的索引和偏移量
		int nowCount = mListView.getCount() - 1;
		View v = mListView.getChildAt(0);
		int top = (v == null) ? 0 : v.getHeight();
		nowCount = list.size() - nowCount + 1;
		adapter.setList(list);
		// 根据上次保存的index和偏移量恢复上次的位置
		if (!isFirst) {
			mListView.setSelectionFromTop(nowCount, top);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		ChatUser user = (ChatUser) adapter.getItem(position-1);
		Intent intent =new Intent(this, ActivityFirendInfo.class);
		intent.putExtra("from", "add");
		intent.putExtra("action", 0);
		intent.putExtra("username", user.getUsername());
		startActivity(intent);		
	}
    @Override
    public void onRefresh() {
    	MyUtil.LOG_I(TAG, "刷新");
    	initNearByList(true);
    }

    @Override
    public void onLoadMore() {
    	MyUtil.LOG_I(TAG, "加载更多");
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongtitude());
		//这是查询10公里范围内的性别为女用户总数
		userManager.queryKiloMetersTotalCount(ChatUser.class, "location", longtitude, latitude, true,QUERY_KILOMETERS,"sex",null,new CountListener() {
	    //这是查询附近的人且性别为女性的用户总数
//		userManager.queryNearTotalCount(User.class, "location", longtitude, latitude, true,"sex",false,new CountListener() {
			@Override
			public void onSuccess(int arg0) {
				if(arg0 >nears.size()){
					curPage++;
					queryMoreNearList(curPage);
				}else{
					MyUtil.LOG_V(TAG, "没有更多了，数据加载完成");
				}
				mListView.stopLoadMore();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				MyUtil.LOG_V(TAG, "查询附近的人总数失败"+arg1);
				mListView.stopLoadMore();
			}
		});
	
    }
	class NearPeopleAdapter extends BaseAdapter {

		List<ChatUser> list;
		public NearPeopleAdapter(Context context, List<ChatUser> list) {
			super();
			this.list = list;
		}

		private static final double EARTH_RADIUS = 6378137;

		private double rad(double d) {
			return d * Math.PI / 180.0;
		}

		/**
		 * 根据两点间经纬度坐标（double值），计算两点间距离，
		 * @param lat1
		 * @param lng1
		 * @param lat2
		 * @param lng2
		 * @return 距离：单位为米
		 */
		public double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
			double radLat1 = rad(lat1);
			double radLat2 = rad(lat2);
			double a = radLat1 - radLat2;
			double b = rad(lng1) - rad(lng2);
			double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
					+ Math.cos(radLat1) * Math.cos(radLat2)
					* Math.pow(Math.sin(b / 2), 2)));
			s = s * EARTH_RADIUS;
			s = Math.round(s * 10000) / 10000;
			return s;
		}

		@Override
		public int getCount() {
			return list==null?0:list.size();
		}

		public void setList(List<ChatUser> list){
			this.list = list;
			notifyDataSetChanged();
		}
		public void addList(List<ChatUser> list){
			this.list.addAll(list);
			notifyDataSetChanged();
		}
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_near_people, null);
			}
			final ChatUser contract = list.get(position);
			convertView.setBackgroundResource(MyApplication.getApplication().pf.item_selector);
			TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
			TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
			TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
			ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
			String avatar = contract.getAvatar();
			if (avatar != null && !avatar.equals("")) {
				ImageLoader.getInstance().displayImage(avatar, iv_avatar);
			} else {
				iv_avatar.setImageResource(R.drawable.open_user_icon_def);
			}
			BmobGeoPoint location = contract.getLocation();
			String currentLat = MyApplication.getApplication().getLatitude();
			String currentLong = MyApplication.getApplication().getLongtitude();
			if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
				double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),contract.getLocation().getLatitude(), 
						contract.getLocation().getLongitude());
				tv_distance.setText(String.valueOf(distance)+"米");
			}else{
				tv_distance.setText("未知");
			}
			tv_name.setText(contract.getUsername());
			tv_logintime.setText("最近登录时间:"+contract.getUpdatedAt());
			return convertView;
		
		}

	}	
}

