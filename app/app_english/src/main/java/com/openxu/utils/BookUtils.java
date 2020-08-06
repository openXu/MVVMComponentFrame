package com.openxu.utils;

import com.openxu.db.base.DAO;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.impl.GetWordDaoImpl;
import com.openxu.db.impl.WordDaoImpl;
import com.openxu.english.R;
import com.openxu.ui.ActivityBook.Book;
import com.openxu.ui.MyApplication;



public class BookUtils {


	//服务器上数据库地址
	public static String DB_TABLE_NAMES[] = {"wordZ4", "wordTF", "wordYSTL", "wordYSYD"};   //下载词库中表名，也是数据库名（需要加.db）
	public static int DB_TABLE_LEV[] = {Property.VALUE_LEVEL_Z4, Property.VALUE_LEVEL_TF, Property.VALUE_LEVEL_YS_TL, Property.VALUE_LEVEL_YS_YD}; 
	public static int DB_BOOK_ICON[] = {R.drawable.open_icon_z4, R.drawable.open_icon_tf, R.drawable.open_icon_ys_tl, R.drawable.open_icon_ys_yd}; 
	public static int WORD_NUM[] = {1926, 2129, 1483, 2220};
//	public static int DB_SIZE[] = {2600000, 2869008, 1949456, 3116816};
	public static int DB_SIZE[] = {2000896, 2213888, 1536000, 2289664};
	

	public static String BOOK_NAMES[] = {"专四核心词汇","托福(TOEFL)词汇精选","雅思(IELTS)听力高频词汇","雅思(IELTS)阅读超高频词汇"};

	public static String DB_DOWN_PATH[] = {"http://file.bmob.cn/M02/D1/09/oYYBAFZf0xKADNU_AB6IACXHJL86824.db",
											"http://file.bmob.cn/M02/D1/0D/oYYBAFZf0_mAMXDsACHIAIbcco44465.db", 
											"http://file.bmob.cn/M02/D1/0D/oYYBAFZf1B6AFLOQABdwAPOB_7c6205.db",
											"http://file.bmob.cn/M02/D1/0E/oYYBAFZf1DaAEfnHACLwAO4DDFA5926.db"};

	public static DAO<OpenWord> getDaoImpl(){
		if(MyApplication.property.level == Property.VALUE_LEVEL_G||
				MyApplication.property.level == Property.VALUE_LEVEL_4||
				MyApplication.property.level == Property.VALUE_LEVEL_6||
				MyApplication.property.level == Property.VALUE_LEVEL_Z8){
			return new WordDaoImpl();
		}else if(MyApplication.property.level == Property.VALUE_LEVEL_Z4||
				MyApplication.property.level == Property.VALUE_LEVEL_TF||
				MyApplication.property.level == Property.VALUE_LEVEL_YS_TL||
				MyApplication.property.level == Property.VALUE_LEVEL_YS_YD){
			return new GetWordDaoImpl();
		}
		return null;
	}
	public static String getBookName(){
		String name = "";
		switch (MyApplication.getApplication().property.level) {
		case Property.VALUE_LEVEL_G:
			name = Property.BOOK_NAME_G;
			break;
		case Property.VALUE_LEVEL_4:
			name = Property.BOOK_NAME_4;
			break;
		case Property.VALUE_LEVEL_6:
			name = Property.BOOK_NAME_6;
			break;
		case Property.VALUE_LEVEL_Z4:
			name = BOOK_NAMES[0];
			break;
		case Property.VALUE_LEVEL_Z8:
			name = Property.BOOK_NAME_8;
			break;
		case Property.VALUE_LEVEL_TF:
			name = BOOK_NAMES[1];
			break;
		case Property.VALUE_LEVEL_YS_TL:
			name = BOOK_NAMES[2];
			break;
		case Property.VALUE_LEVEL_YS_YD:
			name = BOOK_NAMES[3];
			break;
		}
		return name;
	}
	
	public static String getBookDetail(Book book){
		String detail = "涵盖《大学六级考试大纲》规定的1300个核心词汇，词条有例句，适合已掌握四级词汇量的学生考前中点突破。";
		switch (book.level) {
		case Property.VALUE_LEVEL_G:
			detail = "涵盖人教版高中英语课程体系内的"+book.num+"个核心词汇，词条有例句，适合初高中学生背诵。";
			break;
		case Property.VALUE_LEVEL_4:
			detail = "涵盖《大学四级考试大纲》规定的"+book.num+"个核心词汇，词条有例句，适合已掌握高中词汇量的学生考前中点突破。";
			break;
		case Property.VALUE_LEVEL_6:
			detail = "涵盖《大学六级考试大纲》规定的"+book.num+"个核心词汇，词条有例句，适合已掌握四级词汇量的学生考前中点突破。";
			break;
		case Property.VALUE_LEVEL_Z4:
			detail = "涵盖《英语专业四级考试大纲》规定的"+book.num+"个核心词汇，词条有例句，适合已掌握四级词汇量的学生考前中点突破。";
			break;
		case Property.VALUE_LEVEL_Z8:
			detail = "涵盖《英语专业八级考试大纲》规定的"+book.num+"个核心词汇，词条有例句，适合已掌握专四词汇量的学生考前中点突破。";
			break;
		case Property.VALUE_LEVEL_TF:
			detail = "《新东方·托福词汇精选》精选托福考试常考核心"+book.num+"词，以词根词缀记忆法辅助记忆，并提供大量高质量例句，考生在背诵后可方便的进行自我测试，以达到温故知新的效果。";
			break;
		case Property.VALUE_LEVEL_YS_TL:
			detail = "好记单词总结了雅思听力词库，背熟了再去考听力就不会漏掉关键词了。希望可以帮助到大家，祝各位考生都能取得好成绩。";
			break;
		case Property.VALUE_LEVEL_YS_YD:
			detail = "雅思词汇是通过雅思考试的基础性准备，很难想象没有雅思词汇的准备该如何通过雅思考试。词汇量最大的一块应该是雅思阅读，今天给大家整理了雅思阅读超高频词汇，希望可以帮助到大家。";
			break;
		}
		return detail;
	}
	
	public static String getLevelLable(){
		String levelStr = "";
		switch (MyApplication.property.level) {
		case Property.VALUE_LEVEL_G:
			levelStr = "高中";
			break;
		case Property.VALUE_LEVEL_4:
			levelStr = "CET-4";
			break;
		case Property.VALUE_LEVEL_6:
			levelStr = "CET-6";
			break;
		case Property.VALUE_LEVEL_Z4:
			levelStr = "TEM-4";
			break;
		case Property.VALUE_LEVEL_Z8:
			levelStr = "TEM-8";
			break;
		case Property.VALUE_LEVEL_TF:
			levelStr = "TOEFL";
			break;
		case Property.VALUE_LEVEL_YS_TL:
			levelStr = "IELTS-听力";
			break;
		case Property.VALUE_LEVEL_YS_YD:
			levelStr = "IELTS-阅读";
			break;
		default:
			break;
		}
		return levelStr;
	}
	
}
