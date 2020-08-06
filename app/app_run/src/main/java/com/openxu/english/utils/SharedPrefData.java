package com.openxu.english.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.openxu.core.bean.User;
import com.openxu.core.utils.XLog;
import com.openxu.english.MyApplication;

public class SharedPrefData {

    private Context context;
    private static final String SP_NAME = "openXuSp";
    private static SharedPrefData instance;

    public static SharedPrefData getInstance(Context context) {
        if(instance==null)
            instance = new SharedPrefData(context);
        return instance;
    }

    private SharedPrefData(Context context) {
        this.context = context;
    }

    /**
     * key
     */
    private static final String KEY_AUTO_LOGIN = "auto_login";//是否自动登录
    private static final String KEY_CURRENT_USER = "current_userinfo";//当前登录用户信息
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



    public int level;
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

    private Pf pf;
    public Pf getPf() {
        if (pf == null) {
            pf = new Pf(getData(KEY_PF, Integer.class, Pf.VALUE_PF_1));
        }
        return pf;
    }
    public void setPf(int pfID){
        saveData(KEY_PF, pfID);
    }

    public boolean getAutoLogin() {
        return getData(KEY_AUTO_LOGIN, Boolean.class, true);
    }

    public void setAutoLogin(boolean autoLogin) {
        saveData(KEY_AUTO_LOGIN, autoLogin);
    }


    public <T> T getData(String key, Class clazz, Object def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Object obj;
        if (clazz == String.class)
            obj = sharedPreferences.getString(key, (String)def);
        else if (clazz == Integer.class)
            obj = sharedPreferences.getInt(key, (Integer)def);
        else if (clazz == Float.class)
            obj = sharedPreferences.getFloat(key, (Float)def);
        else if (clazz == Long.class)
            obj = sharedPreferences.getLong(key, (Long)def);
        else if (clazz == Boolean.class)
            obj = sharedPreferences.getBoolean(key, (Boolean)def);
        else
            obj = sharedPreferences.getString(key, (String)def);
        return (T) obj;
    }

    public void saveData(String key, Object data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String)
            editor.putString(key, (String) data);
        else if (data instanceof Integer)
            editor.putInt(key, (int) data);
        else if (data instanceof Float)
            editor.putFloat(key, (float) data);
        else if (data instanceof Long)
            editor.putLong(key, (long) data);
        else if (data instanceof Boolean)
            editor.putBoolean(key, (boolean) data);
        else
            editor.putString(key, (String) data);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }


}

