package com.openxu.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class VoicePlayerImpl{

	private SpeechSynthesizer mTts;
	private Context context;
	public final String XUNFEI_APPID = "56318429";
	private String voiceMan = "xiaoyan";
	private Map<String, String> voiceMap;
	private String TAG  =  "VoicePlayerImpl";
	public VoicePlayerImpl(Context context) {
		this.context = context;
		//初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务。建议将初始化放在程序入口处（如Application、Activity的onCreate方法),初始化代码如下：
		// 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn  
		SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+XUNFEI_APPID);   
		//1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener  
		mTts= SpeechSynthesizer.createSynthesizer(context, null);  
		voiceMap = new HashMap<String, String>();
		voiceMap.put("auto", "xiaoyan");    //中英文（普通话）xiaoyan
		voiceMap.put("zh", "xiaoyan");    //中英文（普通话）xiaoyan
		voiceMap.put("en", "vimary");    //英文  vimary
		voiceMap.put("yue", "xiaomei");    //中英文（粤语）   xiaomei
		voiceMap.put("fra", "Mariane");    //法语   Mariane
		voiceMap.put("1", "Guli");       //维语   Guli
		voiceMap.put("ru", "Allabent");    //俄语   Allabent
		voiceMap.put("spa", "Gabriela");    //西班牙语   Gabriela
		voiceMap.put("2", "Abha");        //印地语    Abha
		voiceMap.put("3", "XiaoYun");    //越南语    XiaoYun
		
//        typeMap.put("日语", "jp");
//        typeMap.put("韩语", "kor");
//        typeMap.put("泰语", "th");
//        typeMap.put("阿拉伯语", "ara");
//        typeMap.put("葡萄牙语", "pt");
//        typeMap.put("文言文", "wyw");
//        typeMap.put("白话文", "zh");
//        typeMap.put("德语", "de");
//        typeMap.put("意大利语", "it");
//        typeMap.put("荷兰语", "nl");
//        typeMap.put("希腊语", "el");
		
	}
	public boolean setVoiceMan(String to){
		String man = voiceMap.get(to);
		if(TextUtils.isEmpty(man)){
			return false;
		}
		mTts.setParameter(SpeechConstant.VOICE_NAME, man);//设置发音人  
		MyUtil.LOG_I(TAG, to+"设置发音人为："+man);
		return true;
	}
	public void play(String text) {
		//2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类  
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速  
//		mTts.setParameter(SpeechConstant.VOLUME, "5");//设置音量，范围0~100  
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端  
		//设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”  
		//保存在SD卡需要在AndroidManifest.xml添加写SD卡权限  
		//如果不需要保存合成音频，注释该行代码  
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");  
		//3.开始合成  
		mTts.startSpeaking(text, mSynListener);    
		
	}
	
	//合成监听器  
	private SynthesizerListener mSynListener = new SynthesizerListener(){  
	    //会话结束回调接口，没有错误时，error为null  
	    public void onCompleted(SpeechError error) {
	    	if(error!=null){
	    		MyUtil.showToast(context, -1, "播报错误"+error.getErrorCode()+" "+error.getMessage());
	    		MyUtil.LOG_I(TAG, "播报错误"+error.getErrorCode()+" "+error.getMessage());  
	    	}
	    }  
	    //缓冲进度回调  
	    //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。  
	    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}  
	    //开始播放  
	    public void onSpeakBegin() {
	    	MyUtil.LOG_I(TAG, "开始播报");  
	    }
	    //暂停播放  
	    public void onSpeakPaused() {}  
	    //播放进度回调  
	    //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.  
	    public void onSpeakProgress(int percent, int beginPos, int endPos) {}  
	    //恢复播放回调接口  
	    public void onSpeakResumed() {}  
	    //会话事件回调接口  
	    public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}  
	};
	



}
