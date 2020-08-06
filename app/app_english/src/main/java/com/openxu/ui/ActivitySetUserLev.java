package com.openxu.ui;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.view.LineProgressBar;

/**
 * 经验等级
 * @author openXu
 */
public class ActivitySetUserLev extends BaseActivity{

	private TextView tv_next_jy, tv_pro, tv_count;  //我的经验值
	private TextView tv_lev, tv_tx, tv_jyz;  
	private LineProgressBar linebar;
	private ListView lv_how, lv_lev_gx;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_set_user_lev);
		tv_next_jy = (TextView) findViewById(R.id.tv_next_jy);
		tv_pro = (TextView) findViewById(R.id.tv_pro);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_lev = (TextView) findViewById(R.id.tv_lev);
		tv_tx = (TextView) findViewById(R.id.tv_tx);
		tv_jyz = (TextView) findViewById(R.id.tv_jyz);
		linebar = (LineProgressBar) findViewById(R.id.linebar);
		lv_how = (ListView) findViewById(R.id.lv_how);
		lv_lev_gx = (ListView) findViewById(R.id.lv_lev_gx);
		
	}

	@Override
	protected void setPf() {
		super.setPf();
		tv_next_jy.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
	}
	
	 /**
	public static int LV_1 = 199;        //幼儿园  0-199        199
	public static int LV_2 = 699;        //小学     200-699      499
	public static int LV_3 = 1499;       //初中     700-1499     799
	public static int LV_4 = 2799;       //高中     1500-2799    1299
	public static int LV_5 = 4699;       //大学     2800-4699    1899
	public static int LV_6 = 7199;       //研究     4700-7199    2499
	public static int LV_7 = 10000;       //博士     7200-10000
		*/
	@Override
	protected void initData() {
		int jy = MyApplication.property.getLocalJy();
		int pro = jy%100;
		int lev = jy/100+1;
		tv_pro.setText(pro+"");
		tv_count.setText(100+"");
		linebar.setMaxCount(100);
		linebar.setCurrentCount(pro);
		tv_jyz.setText(jy+"");
		tv_next_jy.setText((100-pro)+"");
		//等级
		tv_lev.setText("LV."+lev);
		//头衔
		if(jy<=Constant.LV_JY1){
			tv_tx.setText("幼儿园");
		}else if(jy<=Constant.LV_JY2){
			tv_tx.setText("小学生");
		}else if(jy<=Constant.LV_JY3){
			tv_tx.setText("初中生");
		}else if(jy<=Constant.LV_JY4){
			tv_tx.setText("高中生");
		}else if(jy<=Constant.LV_JY5){
			tv_tx.setText("大学生");
		}else if(jy<=Constant.LV_JY6){
			tv_tx.setText("研究生");
		}else if(jy<=Constant.LV_JY7){
			tv_tx.setText("博士生");
		}else{
			tv_tx.setText("博士后");
		}
		howList = new ArrayList<How>();
	    gxList = new ArrayList<GX>();
	    /**
	     * 	public static int REWARD_JY_REGIST = 100;   //注册加经验
			public static int REWARD_JY_ICON = 50;      //上传图像
			public static int REWARD_JY_OPEN = 5;       //启动应用
			public static int REWARD_JY_SHARE = 10;     //分享
			public static int REWARD_JY_XUE = 10;       //学习任务(多少个单词+1)
			public static int REWARD_JY_TEST = 30;      //测试任务(多少个单词+1)
			public static int REWARD_JY_FUXI = 30;      //复习任务(多少个单词+1)
			public static int REWARD_JY_FANKUI = 10;    //反馈
	     */
	    howList.add(new How("注册用户", Constant.REWARD_JY_REGIST));
	    howList.add(new How("上传图像", Constant.REWARD_JY_ICON));
	    howList.add(new How("启动应用", Constant.REWARD_JY_OPEN));
	    howList.add(new How("分享美句", Constant.REWARD_JY_SHARE));
	    howList.add(new How("完成学习任务", Constant.REWARD_JY_XUE));
	    howList.add(new How("完成测试任务", Constant.REWARD_JY_TEST));
	    howList.add(new How("完成复习任务", Constant.REWARD_JY_FUXI));
	    /**
	     * 	public static int LV_JY1 = 199;        //幼儿园  0-199        199
			public static int LV_JY2 = 699;        //小学     200-699      499
			public static int LV_JY3 = 1499;       //初中     700-1499     799
			public static int LV_JY4 = 2799;       //高中     1500-2799    1299
			public static int LV_JY5 = 4699;       //大学     2800-4699    1899
			public static int LV_JY6 = 7199;       //研究     4700-7199    2499
			public static int LV_JY7 = 10000;      //博士     7200-10000
			public static int LV_1 = 2;        //幼儿园  0-199        199
			public static int LV_2 = 7;        //小学     200-699      499
			public static int LV_3 = 15;       //初中     700-1499     799
			public static int LV_4 = 28;       //高中     1500-2799    1299
			public static int LV_5 = 47;       //大学     2800-4699    1899
			public static int LV_6 = 72;       //研究     4700-7199    2499
	     */
	    gxList.add(new GX(R.drawable.open_user_lv_1, "幼儿园", "1-2", "0-199"));
	    gxList.add(new GX(R.drawable.open_user_lv_2, "小学生", "3-7", "200-699"));
	    gxList.add(new GX(R.drawable.open_user_lv_3, "初中生", "8-15", "700-1499"));
	    gxList.add(new GX(R.drawable.open_user_lv_4, "高中生", "16-28", "1500-2799"));
	    gxList.add(new GX(R.drawable.open_user_lv_5, "大学生", "29-47", "2800-4699"));
	    gxList.add(new GX(R.drawable.open_user_lv_6, "研究生", "48-72", "4700-7199"));
	    gxList.add(new GX(R.drawable.open_user_lv_7, "博士生", "72-100", "7200-10000"));
	    gxList.add(new GX(R.drawable.open_user_lv_7, "教授", "100~", "10000~"));
	    
		lv_how.setAdapter(new MyHowAdapter());
		lv_lev_gx.setAdapter(new MyGxAdapter());
		setListViewHeightBasedOnChildren(lv_how);
		setListViewHeightBasedOnChildren(lv_lev_gx);
	}
	
	
	private List<How> howList;
	class How{
		String action;
		int num;
		public How(String action, int num) {
			super();
			this.action = action;
			this.num = num;
		}
	}
	class Holder{
		TextView tv_action, tv_jljy;
		ImageView iv_xz;
		TextView tv_tx, tv_lev, tv_jyz;
	}
	class MyHowAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return howList == null ? 0 : howList.size();
		}
		@Override
		public Object getItem(int position) {
			return howList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			How how = howList.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_lev_how, null);
				holder = new Holder();
				holder.tv_action = (TextView) convertView.findViewById(R.id.tv_action);
				holder.tv_jljy = (TextView) convertView.findViewById(R.id.tv_jljy);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.tv_action.setText(how.action);
			holder.tv_jljy.setText(how.num+"");
			holder.tv_jljy.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
			return convertView;
		}
	}

	private List<GX> gxList;
	class GX{
		int icon;
		String tx, lev, jy;
		public GX(int icon, String tx, String lev, String jy) {
			super();
			this.icon = icon;
			this.tx = tx;
			this.lev = lev;
			this.jy = jy;
		}
	}
	class MyGxAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return gxList == null ? 0 : gxList.size();
		}
		@Override
		public Object getItem(int position) {
			return gxList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			GX gx = gxList.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_lev_gx, null);
				holder = new Holder();
				holder.iv_xz = (ImageView) convertView.findViewById(R.id.iv_xz);
				holder.tv_tx = (TextView) convertView.findViewById(R.id.tv_tx);
				holder.tv_lev = (TextView) convertView.findViewById(R.id.tv_lev);
				holder.tv_jyz = (TextView) convertView.findViewById(R.id.tv_jyz);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.iv_xz.setImageResource(gx.icon);
			holder.tv_tx.setText(gx.tx);
			holder.tv_lev.setText(gx.lev);
			holder.tv_jyz.setText(gx.jy);
			holder.tv_lev.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
			holder.tv_jyz.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
			return convertView;
		}
	}
	
	
	public void setListViewHeightBasedOnChildren(ListView listView) {   
        // 获取ListView对应的Adapter   
		BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();   
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);   
            // 计算子项View 的宽高   
            listItem.measure(0, 0);    
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);   
    }   

}
