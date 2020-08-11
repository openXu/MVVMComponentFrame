package com.openxu.utils;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.openxu.db.bean.OpenSent;
import com.openxu.chaxun.bean.zhongwen.Mean;
import com.openxu.chaxun.bean.zhongwen.Part;
import com.openxu.chaxun.bean.zhongwen.Symbol;
import com.openxu.ui.MyApplication;

public class OpenWordUtil {

	private static String TAG = "OpenWordUtil";
	/**
	 * 查询：中-->英，格式化英文
	 */
	public static String formatWords1sss(String parts) {
		String result = "";
		if(!TextUtils.isEmpty(parts)){
			String[] partsarray = parts.split("],");
			if(partsarray!=null){
				for(String part : partsarray){
					try{
						String ps1 =  part.substring(0, part.lastIndexOf(".")+1);
						String ps2 = part.replace(ps1, "");
						part = "<font color=\"#000000\">"+ps1+"</font>";
						part+=ps2;
						result += (part+"]"+"<br>");
					}catch(Exception e){
						e.printStackTrace();
						result += (part+"]"+"<br>");
						continue;
					}
				}
				result = result.replaceAll("]]", "]");
				if(result.endsWith("<br>"))
					result = result.substring(0, result.lastIndexOf("<br>"));
			}
		}
		return result;
	}
	
	/**
	 * 查询：中-->英，格式化释义
	 */
	public static String formatZhongwenSymbols(List<Symbol> symbols) {
		String result = "";
		if(symbols!=null&&symbols.size()>0){
			for(Symbol symbol:symbols){
				result += "<font color=\"#000000\">"+symbol.word_symbol+"</font><br>";//hǎo
				if(symbol.parts!=null){
					for(Part part:symbol.parts){
						String partStr = "<font color=\"#000000\">"+part.part_name+"    </font>";//形容
						if(part.means!=null){
							String meanStr="[  ";
							for(Mean mean:part.means){
								meanStr += (mean.word_mean+" , ");
							}
							if(!TextUtils.isEmpty(meanStr)){
								if(meanStr.endsWith(" , "))
									meanStr = meanStr.substring(0, meanStr.lastIndexOf(" , "));
								meanStr = "<font color=\"#74787c\">"+meanStr+"  ]</font>";
							}
							partStr += meanStr;
						}
						result += (partStr+"<br>");
					}
				}
			}
			if(!TextUtils.isEmpty(result)){
				if(result.endsWith("<br>"))
					result = result.substring(0, result.lastIndexOf("<br>"));
			}
		}
		return result;
	}
	
	/**
	 * 格式化中文
	 */
	public static String formatParts(String parts) {
		String result = "";
		if(!TextUtils.isEmpty(parts)){
			String[] partsarray = parts.split("],");
			if(partsarray!=null){
				for(String part : partsarray){
					try{
						String ps1 =  part.substring(0, part.lastIndexOf(".")+1);
						String ps2 = part.replace(ps1, "");
						part = "<font color=\"#000000\">"+ps1+"</font>";
						part+=ps2;
						result += (part+"]"+"<br>");
					}catch(Exception e){
						e.printStackTrace();
						result += (part+"]"+"<br>");
						continue;
					}
				}
				result = result.replaceAll("]]", "]");
				if(result.endsWith("<br>"))
					result = result.substring(0, result.lastIndexOf("<br>"));
			}
		}
		return result;
	}
	
	/**
	 * 格式化发音
	 */
	public static String formatPh(String ph_en, String ph_am) {
		String voice = "";
		if(!TextUtils.isEmpty(ph_en)){
			voice += ("<font color=\"#6EB0E5\">英</font> "+ph_en);
			if(!TextUtils.isEmpty(ph_am)){
				voice += (" <font color=\"#000000\">\\</font> <font color=\"#6EB0E5\">美</font> "+ph_am);
			}
		}else{
			if(!TextUtils.isEmpty(ph_am)){
				voice += ("<font color=\"#6EB0E5\">美</font> "+ph_am);
			}
		}
		return voice;
	}

	/*** 
     * replaceAll,忽略大小写 
     * @param input 
     * @param regex 
     * @param replacement 
     * @return 
     */  
	public static String formatSent(String sents, String english) {  
		String result = "";
		try{
			List<OpenSent> sentsList = JSON.parseArray(sents, OpenSent.class);
			String[] big = new String[]{"一","二","三","四","五"};
			if(sentsList!=null&&sentsList.size()>0){
				for(int i=0;i<sentsList.size();i++){
					OpenSent sent =  sentsList.get(i);
					Pattern p = Pattern.compile(english, Pattern.CASE_INSENSITIVE);  
			        Matcher m = p.matcher(sent.orig);  
			        String orig = m.replaceAll("<font color=\""+MyApplication.getApplication().pf.sents_en_color+"\">"+english+"</font>");  
					result += ("<font color=\"#6EB0E5\">"+"例句"+big[i]+"</font>"+"<br>" +orig+"<br>" + 
							"<font color=\"#74787c\">"+"  "+sent.trans+"</font>"+"<br><br>");
				}
			} 
		}catch(Exception e){
		}
		return result;  
	}
	
	/*** 
     * @param input 
     * @param regex 
     * @param replacement 
     * @return 
     */  
	public static String formatSentZh(String sents, String zh) {  
		String result = "";
		try{
			List<OpenSent> sentsList = JSON.parseArray(sents, OpenSent.class);
			String[] big = new String[]{"一","二","三","四","五"};
			if(sentsList!=null&&sentsList.size()>0){
				for(int i=0;i<sentsList.size();i++){
					OpenSent sent =  sentsList.get(i);
					String sentTran = sent.trans.replaceAll("\n", "");
					String tranStr = "";
					if(!TextUtils.isEmpty(sentTran)){
						if(sentTran.startsWith(zh)){
							tranStr += ("<font color=\""+MyApplication.getApplication().pf.sents_en_color+"\">"+zh+"</font>");
						}
						String[] trans = sentTran.split(zh);
						for(int j=0;j<trans.length;j++){
							String tran = trans[j];
							if(!TextUtils.isEmpty(tran)){
								if(j==trans.length-1)
									tranStr += ("<font color=\"#74787c\">"+tran+"</font>");
								else
									tranStr += ("<font color=\"#74787c\">"+tran+"</font>"+"<font color=\""+MyApplication.getApplication().pf.sents_en_color+"\">"+zh+"</font>");
							}
						}
						if(sentTran.endsWith(zh)){
							tranStr += ("<font color=\""+MyApplication.getApplication().pf.sents_en_color+"\">"+zh+"</font>");
						}
					}
					result += ("<font color=\"#6EB0E5\">"+"例句"+big[i]+"</font>"+"<br>" +sent.orig+"<br>" + tranStr+"<br><br>");
				}
			} 
		}catch(Exception e){
		}
		return result;  
	}
	
	/************单词测试*************/
	/**
	 * 例句
	 */
	public static String getSent(String sents) {  
		String result = "";
		try{
			List<OpenSent> sentsList = JSON.parseArray(sents, OpenSent.class);
			String[] big = new String[]{"一","二","三","四","五"};
			if(sentsList!=null&&sentsList.size()>0){
				Random random = new Random();
				OpenSent sent = sentsList.get(random.nextInt(sentsList.size()));
				if(sent!=null){
					return sent.orig.trim();
				}
			} 
		}catch(Exception e){
		}
		return result;  
	}
	public static String getOnePart(String parts) {
		String result = "";
		if(!TextUtils.isEmpty(parts)){
			MyUtil.LOG_I(TAG, "获取意思："+parts);
			String[] partsarray = parts.split("],");
			if(partsarray!=null){
				Random random = new Random();
				result = partsarray[random.nextInt(partsarray.length)];
				MyUtil.LOG_I(TAG, "获取意思："+result);  //获取意思：n.["饥荒","饥饿","极度缺乏"
				String ps[] = result.split("\\.\\[");
				MyUtil.LOG_I(TAG, "获取意思："+ps.length);
				if(ps.length>=2){
					result = ps[0]+".";   //n.
					ps = ps[1].split(",");   //"饥荒"   "饥饿"   "极度缺乏"]
				}else if(ps.length==1){
					ps = ps[0].split(",");   //["饥荒","饥饿","极度缺乏"
				}
				int num = 1;
				for(int i=0;i<ps.length;i++){
					String p = ps[i].replaceAll("\"", "");   //饥荒   饥饿    极度缺乏]
					p = p.replaceAll("\\]", "");
					p = p.replaceAll("\\[", "");
					result += (p+";");
					if(num<2){
						num++;
						continue;
					}else{
						break;
					}
				}
				
				if(result.endsWith(";"))
					result = result.substring(0, result.lastIndexOf(";"));
				return result;
			}
		}
		return result;
	}
	
}
