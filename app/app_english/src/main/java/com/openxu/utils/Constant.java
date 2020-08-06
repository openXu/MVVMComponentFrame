package com.openxu.utils;

import java.io.File;

import com.openxu.english.R;

import android.os.Environment;



public class Constant {

	private static String TAG = "Constant";
	public static boolean isFriends = false;   //是否是朋友，如果是朋友，广告少点
	//屏幕宽度
	public static int WIN_WIDTH;
	//数据库文件
	public static final String[] dbs = new String[]{"openword.db"};
	
	//数据备份地址
	private static final String SD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String CATCH_DIR = SD_DIR +File.separator+"haojicidian";
	public static final String CATCH_DIR_DB = CATCH_DIR+File.separator+"databases";
	public static final String CATCH_DIR_SP = CATCH_DIR+File.separator+"shared_prefs";
	public static final String CATCH_DIR_SEND_IMAGE = CATCH_DIR+File.separator+"sendImage";
	public static final String CATCH_DIR_ICON = "icon";
	public static final String USER_ICON = "USER_ICON";        //照相机返回原图
	public static final String USER_ICON_CATCH = "USER_ICON_CATCH"; //裁剪之后图片
	// 单词数据库时间格式
	public static final String DATE_DB = "yyyyMMdd";
	//任务记录
	public static final String DATE_RENWU = "yyyy.MM.dd";
	//金山词霸每日一句参数时间格式
	public static final String DATE_JS = "yyyy-MM-dd";
	//URL
	//金山词霸每日一句
	public static String JS_KEY = "0EAE08A016D6688F64AB3EBB2337BFB0";
	public static String URL_JS_CIDIAN = "http://dict-co.iciba.com/api/dictionary.php?key="+JS_KEY+"&type=json";
	public static String URL_JS_CIDIAN_XML = "http://dict-co.iciba.com/api/dictionary.php?key="+JS_KEY;
	public static String URL_JS_ONESENTENCE = "http://open.iciba.com/dsapi/";
	
	//百度
	public static String API_Key = "342OGbhyrXd82zaxOVDPSVA8";
	public static String Secret_Key = "ozDALCoe5lD30KkjcNlUOZ6uWu1jk43h"; 
	//http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=YourApiKey&q=do&from=en&to=zh
	public static String URL_BAIDU_CIDIAN = "http://openapi.baidu.com/public/2.0/translate/dict/simple"+
							"?client_id="+API_Key;
	//http://openapi.baidu.com/public/2.0/bmt/translate?client_id=YourApiKey&q=today&from=auto&to=auto
	//中文	zh	英语	en  日语	jp	韩语	kor  西班牙语	spa	法语	fra  泰语	th	阿拉伯语	ara 俄罗斯语	ru	葡萄牙语	pt
	//粤语	yue	文言文	wyw  白话文	zh	自动检测	auto 	德语	de	意大利语	it  荷兰语	nl	希腊语	el
	public static String URL_BAIDU_FANYI = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id="+API_Key;
	
	public static String APP_ID = "20151120000005943";
	public static String KEY_P = "X19Nyls_hyKiL6vkezva"; //私钥
	public static String URL_BAIDU_FANYI_NEW = "http://api.fanyi.baidu.com/api/trans/vip/translate?appid="+APP_ID;
	
	
	/**
	 * bmob
	 */
	public static String bmobAID = "373e4d6ebc9c735848119932c5ce9e64"; //Application ID
	public static String bmobRest = "3621afbd58520237eea2d398b10edbe2"; //REST API Key
	public static String bmobMaster = "3a3fa5baee40b41b1bcb0574dc535722"; //Master Key
	public static String bmobAccess = "ea6eeafc11c009ce366b08d2422488f8"; //Access Key
	public static String bmobSecret = "e508d97c560eea08"; //Secret Key
	public static String URL_GET_NEW_VERSION = "http://cloud.bmob.cn/"+bmobSecret+"/getNewVersion";
	
	
	public static String openName = "openXu";
	public static String openID = "d6912b85ee";
	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = CATCH_DIR + "/bmobchat/image/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
	public static final int REQUESTCODE_TAKE_IMAGE_EDIT = 0x000003;//图片编辑
	public static final String EXTRA_STRING = "extra_string";

	/**
	 * 万普
	 */
	public static boolean isDebug = true;
	public static String downLoadUrl = "http://openxuhjword.apps.cn/";
	public static String SHOWAD_CHANCLE = "goapk";
	/**
	 * umeng    友盟升级
	 * default    万普平台(默认)     163      网易应用
	 * wandoujia    豌豆荚          
	 * 91         91手机助手    	 hiapk      安卓市场        baidu    百度应用
	 * QQ      腾讯应用                     360      360市场   
	 * goapk      安智市场
	 * yyh     应用汇
	 * mumayi     木蚂蚁市场
	 * 
	 * google     GooglePlay市场                3G      3G安卓市场
	 *                        
	 *                             
	 * gfan       机锋市场                           sohu     搜狐应用
	 * appChina   掌上应用汇                       UC       UC应用商店
	 *                        dangle       当乐游戏中心
	 * eoe        优亿市场                          samsung    三星乐园
	 * nduo       N多市场                           mmw         移动MM商店
	 * feiliu     飞流下载                         xiaomi        小米市场
	 * crossmo     十字猫商店                  lenovo       联想乐商店
	 * huawei      华为智汇云                   nearme      NearMe商店
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	/**
	 * 经验
	 */
	public static int LV_JY1 = 199;        //幼儿园  0-199        199
	public static int LV_JY2 = 699;        //小学     200-699      499
	public static int LV_JY3 = 1499;       //初中     700-1499     799
	public static int LV_JY4 = 2799;       //高中     1500-2799    1299
	public static int LV_JY5 = 4699;       //大学     2800-4699    1899
	public static int LV_JY6 = 7199;       //研究     4700-7199    2499
	public static int LV_JY7 = 10000;       //博士     7200-10000
	public static int LV_1 = 2;        //幼儿园  0-199        199
	public static int LV_2 = 7;        //小学     200-699      499
	public static int LV_3 = 15;       //初中     700-1499     799
	public static int LV_4 = 28;       //高中     1500-2799    1299
	public static int LV_5 = 47;       //大学     2800-4699    1899
	public static int LV_6 = 72;       //研究     4700-7199    2499
	//+
	public static int REWARD_JY_REGIST = 100;   //注册加经验
	public static int REWARD_JY_ICON = 50;      //上传图像
	public static int REWARD_JY_OPEN = 5;       //启动应用
	public static int REWARD_JY_SHARE = 10;     //分享
	public static int REWARD_JY_XUE = 10;       //学习任务(多少个单词+1)
	public static int REWARD_JY_TEST = 30;      //测试任务(多少个单词+1)
	public static int REWARD_JY_FUXI = 30;      //复习任务(多少个单词+1)
	public static int REWARD_JY_FANKUI = 10;    //反馈
	
	//广播
	public static String RECEIVER_BOOK_CHANGED = "com.openxu.wordbook.changed";
	

}
