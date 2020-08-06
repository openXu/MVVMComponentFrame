package com.openxu.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * 网络判断
 * 
 * @author Administrator
 * 
 */
public class NetWorkUtil {
	private static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");// 4.0模拟器屏蔽掉该权限

	/**
	 * 检查网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		// ConnectivityManager--系统服务
		// ①判断WIFI链接吗
		boolean isWIFI = isWIFIConnectivity(context);
		// ②判断MOBILE链接吗
		boolean isMOBILE = isMOBILEConnectivity(context);
		// 如果没有网络
		if (!isMOBILE && !isWIFI) {
			return false;
		}

		// ③如果MOBILE处于链接状态：WAP NET
		// 如果WAP——代理信息
		// 获取到当前正在处理链接的方式——读取链接的配置参数
		// 如果读取到proxy+port---非空wap
		// TODO 会报权限错误，暂时注释。
		// No permission to write APN settings: Neither user 10104 nor current
		// process has android.permission.WRITE_APN_SETTINGS.
		/*
		 * if (isMOBILE) { readAPN(context); }
		 */

		return true;
	}
/*
	*//**
	 * 读取到proxy+port
	 * 
	 * @param context
	 *//*
	private static void readAPN(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(PREFERRED_APN_URI, null, null, null,
				null);// 获取到当前正在处于链接（单选）
		if (cursor != null && cursor.moveToFirst()) {
			// proxy+port
			URLConstant.PROXY_IP = cursor.getString(cursor
					.getColumnIndex("proxy"));
			URLConstant.PROXY_PORT = cursor.getInt(cursor
					.getColumnIndex("port"));
		}
	}*/

	/**
	 * 判断MOBILE链接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isMOBILEConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// MOBILE链接的描述信息
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断WIFI链接状态
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isWIFIConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// WIFI链接的描述信息
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断网络连接状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isAvailable()) {
			return false;
		}
		return true;
	}
	

}
