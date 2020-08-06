package com.openxu.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Toast;

import com.openxu.ui.MyApplication;
public class UIUtils {
	public static void showToast(int stringId){
		Toast.makeText(getContext(), stringId, 0).show();
	}
	public static void showToast(String str){
		Toast.makeText(getContext(), str, 0).show();
	}
	
	public static Context getContext(){
		return MyApplication.getApplication();
	}
	public static String[] getStringArray(int id){
		return getResource().getStringArray(id);
	}
	public static Resources getResource() {
		return getContext().getResources();
	}
	public static int getColor(int id){
		return getResource().getColor(id);
	}
	public static View  inflate(int layoutId){
		return View.inflate(getContext(), layoutId, null);
	}
}
