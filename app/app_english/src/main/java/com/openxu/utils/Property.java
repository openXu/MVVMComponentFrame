package com.openxu.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.bmob.v3.listener.UpdateListener;

import com.openxu.db.helper.WordDBHelper;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.ui.MyApplication;

public class Property {

	private String TAG = "Property";
	private final String SP_NAME = "english_sp";
	//KEY
	private final String KEY_PF = "pf";  //皮肤
	private final String KEY_PF_LOCK2 = "pf2";  //皮肤
	private final String KEY_PF_LOCK3 = "pf3";  //皮肤
	private final String KEY_DBCOPY = "dbCopy";  //拷贝数据库
	private final String KEY_LEVEL = "level";     //单词库等级
	
	private final String KEY_JCMS = "jcms";     //记单词模式（顺序还是随机）
	private final String KEY_FXMS = "fxms";     //复习模式（顺序还是随机）
	private final String KEY_AD_TIME = "ad_time";          //去广告到期时间
	private final String KEY_JINGYAN = "jingyan";          //经验值
	private final String KEY_JINGYANDAY = "jingyanday";    //同步经验日期
	public static final String KEY_JY_XUEXIDAY = "jyXuexiday";   //学习奖励经验日期
	public static final String KEY_JY_TESTDAY = "jyTestday";     //测试奖励经验日期
	public static final String KEY_JY_FUXIDAY = "jyFuxiday";     //复习奖励经验日期
	//消息通知设置
	private final String KEY_VOICE = "voice";      //声音
	private final String KEY_VIBRATE = "vibrate";  //震动
	private final String KEY_OPENXU_NUM = "openXuNum";  //开发者消息数量
	//每日单词任务
	private final String KEY_DATE_G = "dateg";     //高中单词最后一次时间
	private final String KEY_DATE_4 = "date4";     //四级单词最后一次时间
	private final String KEY_DATE_6 = "date6";     //6级单词最后一次时间
	private final String KEY_DATE_8 = "date8";     //8级单词最后一次时间
	private final String KEY_RENWU_NUM = "renwuNum";     //每日新词数量
	//复习
	private final String KEY_FX_NUM = "fxNum";         //每日复习数量
	private final String KEY_FXDATE_G = "fxdateg";     //高中单词最后一次时间
	private final String KEY_FXDATE_4 = "fxdate4";     //四级单词最后一次时间
	private final String KEY_FXDATE_6 = "fxdate6";     //6级单词最后一次时间
	private final String KEY_FXDATE_8 = "fxdate8";     //8级单词最后一次时间
	
	private final String KEY_PLAY_SP = "playsp";     //播放单词速度
	
	private final String KEY_SHOW_MYBOOK_SP = "showMyBook";     //主页显示我的单词本
	
	//查词记录
	private final String KEY_SEARCH_HOS = "search_word"; 
	//单词库下载记录
	private final String KEY_DOWNLOAD_DB = "download_db"; 
	
	
	//VALUE
	public static final int VALUE_PF_1 = 1;   //天蓝
	public static final int VALUE_PF_2 = 2;   //草绿
	public static final int VALUE_PF_3 = 3;   //草绿
	public static final int VALUE_LEVEL_G = 10;
	public static final int VALUE_LEVEL_4 = 20;
	public static final int VALUE_LEVEL_6 = 30;
	public static final int VALUE_LEVEL_Z4 = 35;
	public static final int VALUE_LEVEL_Z8 = 40;
	public static final int VALUE_LEVEL_TF = 50;
	public static final int VALUE_LEVEL_YS_TL = 60;
	public static final int VALUE_LEVEL_YS_YD = 70;
	public static final String BOOK_NAME_G = "高中词汇精选";
	public static final String BOOK_NAME_4 = "四级核心词汇";
	public static final String BOOK_NAME_6 = "六级核心词汇";
	public static final String BOOK_NAME_8 = "专八核心词汇";
	
	private final int VALUE_NUM_DEFULT = 20;     
	private final int VALUE_PLAY_SP = 5; 
	//模式
	public static final int VALUE_JCMS_SJ = 1;
	public static final int VALUE_JCMS_SX = 2;
	//模式
	public static final int VALUE_FXMS_SJ = 1;
	public static final int VALUE_FXMS_SX = 2;
	
	private SharedPreferences sp;
	private String oldDate_key;
	private String fxDate_key;
	
	public int level;
	public String old_date;
	public String fx_date;
	public String tableName;
	public boolean dbCopy;
	public int local_jy;
	public String tbJyDay;   //同步经验值（今天同步昨天的）
	
	//设置值
	public int renwu_num;   //新词量
	public int play_sp;  //播放单词速度
	public int jcms;   //模式
	public int fxms;   //模式
	public int fx_num;   //复习量
	public boolean showBook;
	//广告
	public String adTime;
	public int clearPoint = 40;
	public int clearDay = 30;
	//消息设置
	public boolean voice;
	public boolean vibrate;
	public int openXuNum;
	//词库下载记录
	public String download_db;
	
	
	
	public Property(){
		sp = MyApplication.getApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE); 
		newData();
	}
	
	public int getPf(){
		return sp.getInt(KEY_PF, VALUE_PF_1);
	}
	public void setPf(int pfID){
		saveSp(KEY_PF, pfID);
		MyApplication.getApplication().pf.loadPf();//加载皮肤
	}
	public boolean getPfLock2(){
		return sp.getBoolean(KEY_PF_LOCK2, false);
	}
	public boolean getPfLock3(){
		return sp.getBoolean(KEY_PF_LOCK3, false);
	}
	public void setPfLock2(){
		saveSp(KEY_PF_LOCK2, true);
	}
	public void setPfLock3(){
		saveSp(KEY_PF_LOCK3, true);
	}
	private void newData(){
		this.level = sp.getInt(KEY_LEVEL, VALUE_LEVEL_G); // 默认四级
		MyUtil.LOG_V(TAG, "当前词库等级"+level);
		switch (level) {
		case VALUE_LEVEL_G:
			oldDate_key = KEY_DATE_G;
			fxDate_key = KEY_FXDATE_G;
			tableName = WordDBHelper.TABLE_WORDg;
			break;
		case VALUE_LEVEL_4:
			oldDate_key = KEY_DATE_4;
			fxDate_key = KEY_FXDATE_4;
			tableName = WordDBHelper.TABLE_WORD4;
			break;
		case VALUE_LEVEL_6:
			oldDate_key = KEY_DATE_6;
			fxDate_key = KEY_FXDATE_6;
			tableName = WordDBHelper.TABLE_WORD6;
			break;
		case VALUE_LEVEL_Z4:
			oldDate_key = BookUtils.DB_TABLE_NAMES[0]+"od";
			fxDate_key = BookUtils.DB_TABLE_NAMES[0]+"fx";
			tableName = BookUtils.DB_TABLE_NAMES[0];
			break;
		case VALUE_LEVEL_Z8:
			oldDate_key = KEY_DATE_8;
			fxDate_key = KEY_FXDATE_8;
			tableName = WordDBHelper.TABLE_WORD8;
			break;	
		case VALUE_LEVEL_TF:
			oldDate_key = BookUtils.DB_TABLE_NAMES[1]+"od";
			fxDate_key = BookUtils.DB_TABLE_NAMES[1]+"fx";
			tableName = BookUtils.DB_TABLE_NAMES[1];
			break;
		case VALUE_LEVEL_YS_TL:
			oldDate_key = BookUtils.DB_TABLE_NAMES[2]+"od";
			fxDate_key = BookUtils.DB_TABLE_NAMES[2]+"fx";
			tableName = BookUtils.DB_TABLE_NAMES[2];
			break;
		case VALUE_LEVEL_YS_YD:
			oldDate_key = BookUtils.DB_TABLE_NAMES[3]+"od";
			fxDate_key = BookUtils.DB_TABLE_NAMES[3]+"fx";
			tableName = BookUtils.DB_TABLE_NAMES[3];
			break;
		}
		this.dbCopy = sp.getBoolean(KEY_DBCOPY, false);
		this.play_sp = sp.getInt(KEY_PLAY_SP, VALUE_PLAY_SP);
		
		this.old_date = sp.getString(oldDate_key, "");
		this.jcms = sp.getInt(KEY_JCMS, VALUE_JCMS_SJ);
		this.renwu_num = sp.getInt(KEY_RENWU_NUM, VALUE_NUM_DEFULT);
		this.fx_date = sp.getString(fxDate_key, "");
		this.fxms = sp.getInt(KEY_FXMS, VALUE_FXMS_SJ);
		this.fx_num = sp.getInt(KEY_FX_NUM, VALUE_NUM_DEFULT);
		
		this.adTime = sp.getString(KEY_AD_TIME, "");
		this.local_jy = sp.getInt(KEY_JINGYAN, 0);
		this.tbJyDay = sp.getString(KEY_JINGYANDAY, "");
		
		this.showBook = sp.getBoolean(KEY_SHOW_MYBOOK_SP, true);
		
		this.voice = sp.getBoolean(KEY_VOICE, true);
		this.vibrate = sp.getBoolean(KEY_VIBRATE, true);
		this.openXuNum = sp.getInt(KEY_OPENXU_NUM, 0);
		
		this.download_db = sp.getString(KEY_DOWNLOAD_DB, "");
		
		MyUtil.LOG_V(TAG, "加载配置文件："+this);
	}
	
	
	
	@Override
	public String toString() {
		return "Property [数据库拷贝:"+ dbCopy +",单词等级:" + level + ",记词模式:" + jcms
				+ ", 复习模式:" + fxms + ", 记词数量:" + renwu_num+", 复习数量:"+fx_num
				+ ", 播放速度:" + play_sp +", 显示我的单词："+showBook+",广告时间:"+adTime+"]";
	}

	//设置单词库
	public boolean setLevel(int level) {
		if(this.level!=level){
			saveSp(KEY_LEVEL, level);
			newData();
			return true;
		}else
			return false;
		
	}
	//设置任务数量
	public boolean setRenwu_num(int renwu_num) {
		if(this.renwu_num!=renwu_num){
			saveSp(KEY_RENWU_NUM, renwu_num);
			newData();
			return true;
		}else
			return false;
	}
	//设置播放速度
	public boolean setPlay_sp(int play_sp) {
		if(this.play_sp!=play_sp){
			saveSp(KEY_PLAY_SP, play_sp);
			newData();
			return true;
		}else
			return false;
	}
	//设置任务模式
	public boolean setJCMS(int ms) {
		if(this.jcms!=ms){
			saveSp(KEY_JCMS, ms);
			newData();
			return true;
		}else
			return false;
	}
	//设置复习模式
	public boolean setFXMS(int ms) {
		if(this.fxms!=ms){
			saveSp(KEY_FXMS, ms);
			newData();
			return true;
		}else
			return false;
	}
	//设置复习数量
	public boolean setFx_num(int fx_num) {
		if(this.fx_num!=fx_num){
			saveSp(KEY_FX_NUM, fx_num);
			newData();
			return true;
		}else
			return false;
	}
	//更新最后一次任务时间
	public void update_oldDate() {
		saveSp(oldDate_key, MyUtil.date2Str(new Date(), Constant.DATE_DB));
		newData();
	}
	//更新最后一次复习时间
	public void update_oldfxDate() {
		saveSp(fxDate_key, MyUtil.date2Str(new Date(), Constant.DATE_DB));
		newData();
	}
	public void setDbCopy(boolean dbCopy) {
		this.dbCopy = dbCopy;
		saveSp(KEY_DBCOPY, dbCopy);
	}
	public void setShowBook(boolean showBook) {
		this.showBook = showBook;
		saveSp(KEY_SHOW_MYBOOK_SP, showBook);
	}

	private void saveSp(String key, Object value){
		if(value instanceof String)
			sp.edit().putString(key, (String)value).commit();
		else if(value instanceof Integer)
			sp.edit().putInt(key, (Integer)value).commit();
		else if(value instanceof Boolean)
			sp.edit().putBoolean(key, (Boolean)value).commit();
	}
	
	public boolean isOldData(){
		if(TextUtils.isEmpty(old_date)){
			return false;
		}else{
			Date olddate = MyUtil.str2Date(old_date, Constant.DATE_DB);
			Date newDate = MyUtil.str2Date(MyUtil.date2Str(new Date(), Constant.DATE_DB), Constant.DATE_DB);
			
			MyUtil.LOG_D(TAG, "旧日期:"+old_date);
			MyUtil.LOG_D(TAG, "今天日期:"+MyUtil.date2Str(new Date(), Constant.DATE_DB));
			MyUtil.LOG_D(TAG, "是否为旧日期:"+(newDate.compareTo(olddate)==0?true:false));
			return newDate.compareTo(olddate)==0?true:false;
		}
	}
	public boolean isOldFxData(){
		if(TextUtils.isEmpty(fx_date)){
			return false;
		}else{
			Date olddate = MyUtil.str2Date(fx_date, Constant.DATE_DB);
			Date newDate = MyUtil.str2Date(MyUtil.date2Str(new Date(), Constant.DATE_DB), Constant.DATE_DB);
			
			MyUtil.LOG_D(TAG, "旧日期:"+fx_date);
			MyUtil.LOG_D(TAG, "今天日期:"+MyUtil.date2Str(new Date(), Constant.DATE_DB));
			MyUtil.LOG_D(TAG, "是否为旧日期:"+(newDate.compareTo(olddate)==0?true:false));
			return newDate.compareTo(olddate)==0?true:false;
		}
	}
	
	
	//设置广告到期时间
	public void setAdTime(int bayDay){
		if(bayDay<0){
			saveSp(KEY_AD_TIME, "");
			adTime = "";
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + bayDay);
		this.adTime = MyUtil.date2Str(c.getTime(), Constant.DATE_JS);
		MyUtil.LOG_V(TAG, "购买"+bayDay+"天，广告到期时间"+adTime);
		saveSp(KEY_AD_TIME, adTime);
	}
	public String getAdTime(){
		return adTime;
	}
	
	//是否加载广告
	public boolean initAd(){
		if(TextUtils.isEmpty(adTime))
			return true;
		else{
			Date adDate = MyUtil.str2Date(adTime, Constant.DATE_JS);
			String nowDate = MyUtil.date2Str(new Date(), Constant.DATE_JS);
			Date newDay = MyUtil.str2Date(nowDate, Constant.DATE_JS);
			boolean init = newDay.compareTo(adDate)>0?true:false;
			MyUtil.LOG_V(TAG, "广告到期时间"+adTime+"今天"+nowDate+"加载广告"+init);
			return init;
		}
	}
	/************************************经验值*****************************************/
	//经验值
	public int getLocalJy(){
		return local_jy;
	}
	/**
	 * 奖励经验值
	 * ①、如果当天同步过经验值，将经验值存储本地
	 * ②、如果当天没有同步经验值，但是用户没有登录，将经验值存储本地
	 * ③、如果当天没有同步经验值，用户登录，同步经验到服务器
	 * 	    登录成功后将本地经验和加的经验一起同步到服务器：
	 *         同步失败的村本地；
	 *         同步成功的清空本地经验
	 *    没有登录成功的，村本地。
	 * ②、没有注册的用户将经验存在本地，带注册后一起同步
	 * @param jingy
	 * @param context
	 */
	public void rewardJy(final int jingy, final Context context, boolean clear){
		if(clear){
			local_jy = 0;
			saveSp(KEY_JINGYAN, local_jy);
			MyUtil.LOG_V(TAG, "退出登录，情况本地经验"+local_jy);
			return;
		}
		local_jy += jingy;
		saveSp(KEY_JINGYAN, local_jy);
		MyUtil.LOG_V(TAG, local_jy+"奖励经验"+jingy);
		if(shouldTbJy() && (MyApplication.user!=null&&!TextUtils.isEmpty(MyApplication.user.getObjectId()))){
			MyUtil.LOG_V(TAG, "同步经验值到服务器："+local_jy);
			//将本地经验同步上去
			MyApplication.user.setJingyan(local_jy);
			MyApplication.user.update(context, MyApplication.user.getObjectId(),
					new UpdateListener() {
						@Override
						public void onSuccess() {
							MyUtil.LOG_V(TAG, "同步经验成功，"+MyApplication.user);
							new UserDaoImpl().updata(MyApplication.user);
							updateTbJyDay();
						}
						@Override
						public void onFailure(int code, String msg) {
							MyUtil.LOG_V(TAG, "服务器同步经验失败"+msg+"将经验加载本地");
						}
					});
		
		}
		
	}
	//判断当天有没有同步服务器经验值
	private boolean shouldTbJy(){
		boolean tb = false;
		if(!TextUtils.isEmpty(tbJyDay)){
			Date oldDay = MyUtil.str2Date(tbJyDay, Constant.DATE_DB);
			String newDayStr = MyUtil.date2Str(new Date(), Constant.DATE_DB);
			Date newDay = MyUtil.str2Date(newDayStr, Constant.DATE_DB);
			tb = newDay.compareTo(oldDay)>0?true:false;
			MyUtil.LOG_E(TAG, "上次同步经验"+tbJyDay+"今天"+newDayStr+"是否同步经验"+tb);
		}else
			tb = true;
		MyUtil.LOG_V(TAG, "今天是否应该同步经验值到服务器："+tb);
		return tb;
	}
	/**
	 * 如果今天同步了经验，就存档
	 */
	private void updateTbJyDay(){
		this.tbJyDay = MyUtil.date2Str(new Date(), Constant.DATE_DB);
		saveSp(KEY_JINGYANDAY, MyUtil.date2Str(new Date(), Constant.DATE_DB)); //更新经验时间
		MyUtil.LOG_V(TAG, "更新经验时间"+tbJyDay);
	}
	
	/**
	 * 判断是不是每天第一次加经验
	 * @return
	 */
	public boolean showAddJy(String key){
		boolean tb = false;
		String oldDayStr = sp.getString(key, "");
		String newDayStr = MyUtil.date2Str(new Date(), Constant.DATE_DB);
		if(TextUtils.isEmpty(oldDayStr)){
			tb = true;
		}else{
			Date oldDay = MyUtil.str2Date(oldDayStr, Constant.DATE_DB);
			Date newDay = MyUtil.str2Date(newDayStr, Constant.DATE_DB);
			if(!TextUtils.isEmpty(oldDayStr)){
				tb = newDay.compareTo(oldDay)>0?true:false;
				MyUtil.LOG_E(TAG, "上次"+key+"加经验"+oldDayStr+"今天"+newDayStr+"是否加经验"+tb);
			}else
				tb = true;
		}
		saveSp(key, newDayStr); 
		MyUtil.LOG_V(TAG, "今天是否应该加TEST经验："+tb);
		return tb;
	}
	/************************************经验值*****************************************/
	
	private String splitHistory = ",";
	public List<String> getSearchHistory(){
		String historyStr = sp.getString(KEY_SEARCH_HOS, "");
		List<String> list = new ArrayList<String>();
		if(!TextUtils.isEmpty(historyStr)){
			String[] history = historyStr.split(splitHistory);
			if(history!=null&&history.length>0){
				for(String h : history){
					if(!TextUtils.isEmpty(h))
						list.add(h);
				}
			}
			MyUtil.LOG_V(TAG, "展示历史记录："+list.size());
			return list;
		}else
			return null;
	}
	
	private int HISTORY_NUM = 20;
	public void addHistory(String word){
		word = word.trim();
		String historyStr = sp.getString(KEY_SEARCH_HOS, "");
		if(historyStr.contains(splitHistory)){
			if(historyStr.contains(word+splitHistory)){
				historyStr = historyStr.replace(word+splitHistory, "");
				historyStr = word+splitHistory+historyStr;
				saveSp(KEY_SEARCH_HOS, historyStr); 
				MyUtil.LOG_V(TAG, "添加历史记录："+historyStr);
				return;
			}
		}else{
			if(word.equalsIgnoreCase(historyStr)){
				MyUtil.LOG_V(TAG, "添加历史记录："+word);
				return;
			}
		}
		
		if(!TextUtils.isEmpty(historyStr)){
			String[] history = historyStr.split(splitHistory);
			int cont = 1;
			String newHistoryStr = word+splitHistory;
			for(int i = 0; i<history.length; i++){
				String h = history[i];
				if(!TextUtils.isEmpty(h)){
					newHistoryStr += (h+splitHistory);
					cont ++;
					if(cont>=HISTORY_NUM)
						break;
				}
			}
			saveSp(KEY_SEARCH_HOS, newHistoryStr); 
			MyUtil.LOG_V(TAG, "添加历史记录："+newHistoryStr);
		}else{
			saveSp(KEY_SEARCH_HOS, word); 
			MyUtil.LOG_V(TAG, "添加历史记录："+word);
		}
	}

	public void setOpneXuNum(int num){
		if(num == 0){
			openXuNum = 0;
		}else{
			openXuNum += num;
		}
		MyUtil.LOG_D(TAG, "开发者信息数量："+openXuNum);
		saveSp(KEY_OPENXU_NUM, openXuNum); 
	}
	public void deleteHistory(String word){
		word = word.trim();
		String historyStr = sp.getString(KEY_SEARCH_HOS, "");
		MyUtil.LOG_V(TAG, "删除历史记录之前："+historyStr);
		historyStr = historyStr.replaceAll(word, "");
		historyStr = historyStr.replaceAll(splitHistory+splitHistory, splitHistory);
		MyUtil.LOG_V(TAG, "删除历史记录之后："+historyStr);
		saveSp(KEY_SEARCH_HOS, historyStr); 
	}
	public void setVoice(boolean voice) {
		this.voice = voice;
		saveSp(KEY_VOICE, voice); 
	}
	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
		saveSp(KEY_VIBRATE, vibrate); 
	}
	public void setDownloadDb(String dbName){
		if(TextUtils.isEmpty(this.download_db)){
			this.download_db = dbName;
		}else{
			this.download_db += (","+dbName);
		}
		MyUtil.LOG_V(TAG, "下载词库配置："+download_db);
		saveSp(KEY_DOWNLOAD_DB, download_db); 
	}
	public List<String> getDownloadDb(){
		List<String> list = new ArrayList<String>();
		if(TextUtils.isEmpty(this.download_db)){
		}else{
			String[] strs = download_db.split(",");
			if(strs!=null&&strs.length>0){
				for(String str : strs){
					list.add(str);
				}
			}
		}
		return list;
	}
}
