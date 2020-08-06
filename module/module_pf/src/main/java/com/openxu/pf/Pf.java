package com.openxu.pf;


import android.content.Context;

import com.openxu.core.utils.XSharedData;

public class Pf {
	//全局
	public int item_selector;
	public int btn_selector;
	public int text_color;           //白色字体
	//regist
	public int regist_edt_bg;        //注册、找回密码输入栏背景
	public int regist_line_color;    //注册、找回密码线条
	public int regist_btn_selector;  //注册、找回密码按钮  
	//login
	public int login_ztn;            //登录界面状态栏颜色
	public int login_bg;             //登录界面背景
	public int login_btn_text;          //登录按钮字颜色
	//main
	public int main_item_bg;            //主界面item选择
	public int main_bookitem_bg;            //主界面item选择
	public int main_title_text_color;   //主界面标题未选中
	public int search_bag;           //查词历史记录背景
	//menu
	public int main_menu_bg;         // 侧边菜单背景
//	public int main_menu_item_bg;    // 侧边菜单背景
	//title
	public int title_bg;             // 标题背景
	public int title_item_selecter;  // 标题点击
	//圆圈进度
	public int round_color;
	//例句关键字颜色
	public String sents_en_color;
	// 单词排序
	public int word_sort;
	//广告背景
	public int ad_bg; 
	public int ad_text_color;
	//设置界面背景
	public int setting_bg; 
	//联系人popup
	public int pop_friend; 
	private static Pf instance ;
	public static Pf getInstance(Context context){
		if(instance==null)
			instance = new Pf(context);

		return instance;
	}
	private Pf(Context context) {
		switch (XSharedData.getInstance(context).getData(PfConstant.KEY_PF, Integer.class, PfConstant.VALUE_PF_1)) {
			case PfConstant.VALUE_PF_1:
				setPf1();
				break;
			case PfConstant.VALUE_PF_2:
				setPf2();
				break;
			case PfConstant.VALUE_PF_3:
				setPf3();
				break;
			default:
				setPf1();
				break;
		}
	}

	private void setPf1() {
		//全局
		item_selector = R.drawable.pf1_item_selector;         
		btn_selector = R.drawable.pf1_btn_selector;
		text_color = R.color.color_white;  //白色字体
		//regist
		regist_edt_bg = R.drawable.pf1_regist_edit_bg;
		regist_line_color = R.color.pf1_regist_line_color;        //注册、找回密码线条
		regist_btn_selector = R.drawable.pf1_regist_btn_selector;  //注册、找回密码按钮  
		//login
		login_ztn = R.color.pf1_login_bg_color1;
		login_bg = R.drawable.pf1_login_bg;                   //登录界面背景
		login_btn_text = R.color.pf1_login_btn_text;
		//main
		main_item_bg = R.drawable.pf1_main_item_bg;           //主界面item选择
		main_bookitem_bg = R.color.pf1_main_bookitem_bg;
		main_title_text_color = R.color.pf1_main_title_text_color;   //主界面标题未选中
		main_menu_bg = R.drawable.pf1_login_bg;               // 侧边菜单背景
		
		search_bag = R.drawable.pf1_search_history;
		//title
		// 标题背景
		title_bg = R.color.pf1_title_bg;                     
		// 标题点击
		title_item_selecter = R.drawable.pf1_title_item_selector; 
		//圆圈进度
		round_color = R.color.pf1_round_color;                       
		//例句关键字颜色
		sents_en_color = "#3DAAE2";     
		// 单词排序
		word_sort = R.color.pf1_word_sort;
		//广告背景
		ad_bg = R.color.pf1_ad_bg; 
		ad_text_color = R.color.pf1_ad_text_color;
		
		setting_bg = R.mipmap.setting_pf1;
		pop_friend = R.drawable.pf1_pop_friend;
	}

	private void setPf2() {
		//全局
		item_selector = R.drawable.pf2_item_selector;         
		btn_selector = R.drawable.pf2_btn_selector;
		text_color = R.color.color_white;  //白色字体
		//regist
		regist_edt_bg = R.drawable.pf2_regist_edit_bg;
		regist_line_color = R.color.pf2_regist_line_color;        //注册、找回密码线条
		regist_btn_selector = R.drawable.pf2_regist_btn_selector;  //注册、找回密码按钮  
		//login
		login_ztn = R.color.pf2_login_bg_color1;
		login_bg = R.drawable.pf2_login_bg;                   //登录界面背景
		login_btn_text = R.color.pf2_login_btn_text;
		//main
		main_item_bg = R.drawable.pf2_main_item_bg;           //主界面item选择
		main_bookitem_bg = R.color.pf2_main_bookitem_bg;
		main_title_text_color = R.color.pf2_main_title_text_color;   //主界面标题未选中
		main_menu_bg = R.drawable.pf2_login_bg;               // 侧边菜单背景
		
		search_bag = R.drawable.pf2_search_history;
		//title
		// 标题背景
		title_bg = R.color.pf2_title_bg;                     
		// 标题点击
		title_item_selecter = R.drawable.pf2_title_item_selector; 
		//圆圈进度
		round_color = R.color.pf2_round_color;                       
		//例句关键字颜色
		sents_en_color = "#4AC2E3";     
		// 单词排序
		word_sort = R.color.pf2_word_sort;
		//广告背景
		ad_bg = R.color.pf2_ad_bg; 
		ad_text_color = R.color.pf2_ad_text_color;
		setting_bg = R.mipmap.setting_pf2;
		pop_friend = R.drawable.pf2_pop_friend;
	}
	private void setPf3() {
		//全局
		item_selector = R.drawable.pf3_item_selector;         
		btn_selector = R.drawable.pf3_btn_selector;
		text_color = R.color.color_white;  //白色字体
		//regist
		regist_edt_bg = R.drawable.pf3_regist_edit_bg;
		regist_line_color = R.color.pf3_regist_line_color;        //注册、找回密码线条
		regist_btn_selector = R.drawable.pf3_regist_btn_selector;  //注册、找回密码按钮  
		//login
		login_ztn = R.color.pf3_login_bg_color1;
		login_bg = R.drawable.pf3_login_bg;                   //登录界面背景
		login_btn_text = R.color.pf3_login_btn_text;
		//main
		main_item_bg = R.drawable.pf3_main_item_bg;           //主界面item选择
		main_bookitem_bg = R.color.pf3_main_bookitem_bg;
		main_title_text_color = R.color.pf3_main_title_text_color;   //主界面标题未选中
		main_menu_bg = R.drawable.pf3_login_bg;               // 侧边菜单背景
		search_bag = R.drawable.pf3_search_history;
		//title
		// 标题背景
		title_bg = R.color.pf3_title_bg;                     
		// 标题点击
		title_item_selecter = R.drawable.pf3_title_item_selector; 
		//圆圈进度
		round_color = R.color.pf3_round_color;                       
		//例句关键字颜色
		sents_en_color = "#4AC2E3";     
		// 单词排序
		word_sort = R.color.pf3_word_sort;
		//广告背景
		ad_bg = R.color.pf3_ad_bg; 
		ad_text_color = R.color.pf3_ad_text_color;
		setting_bg = R.mipmap.setting_pf3;
		pop_friend = R.drawable.pf3_pop_friend;
	}

	@Override
	public String toString() {
		return "Pf [item_selector=" + item_selector + ", btn_selector="
				+ btn_selector + ", text_color=" + text_color
				+ ", regist_edt_bg=" + regist_edt_bg + ", regist_line_color="
				+ regist_line_color + ", regist_btn_selector="
				+ regist_btn_selector + ", login_ztn=" + login_ztn
				+ ", login_bg=" + login_bg + ", login_btn_text="
				+ login_btn_text + ", main_item_bg=" + main_item_bg
				+ ", main_title_text_color=" + main_title_text_color
				+ ", main_menu_bg=" + main_menu_bg + ", title_bg=" + title_bg
				+ ", title_item_selecter=" + title_item_selecter
				+ ", round_color=" + round_color + ", sents_en_color="
				+ sents_en_color + ", word_sort=" + word_sort + ", ad_bg="
				+ ad_bg + ", ad_text_color=" + ad_text_color + ", setting_bg="
				+ setting_bg + "]";
	}
	
	
}
