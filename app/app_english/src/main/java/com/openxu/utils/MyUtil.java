package com.openxu.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.openxu.ui.MyApplication;

public class MyUtil {

	private static String TAG = "MyUtil";
    /**
     * 唯一的toast
     */
    private static Toast mToast = null;

	public synchronized static void showToast(Context mContext, int srcId,
			String more) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = str + more;
		
		 if (mToast != null) {
            //mToast.cancel();
        } else {
        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
	}

	public synchronized static void showToast(Context mContext, String more,
			int srcId, String more1) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = more + str;
		if (!TextUtils.isEmpty(more1))
			str = str + more1;
		 if (mToast != null) {
	            //mToast.cancel();
	        } else {
	        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
	        }
	        mToast.setText(str);
	        mToast.show();
	}

	public static String getString(Context mContext, int srcId) {
		return mContext.getResources().getString(srcId);
	}

	public static void TOAST(Context context, String msg) {
		if (Constant.isDebug)
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void LOG_V(String TAG, String msg) {
		if (Constant.isDebug)
			Log.v(TAG, msg);
	}

	public static void LOG_I(String TAG, String msg) {
		if (Constant.isDebug)
			Log.i(TAG, msg);
	}

	public static void LOG_D(String TAG, String msg) {
		if (Constant.isDebug)
			Log.d(TAG, msg);
	}

	public static void LOG_W(String TAG, String msg) {
		if (Constant.isDebug)
			Log.w(TAG, msg);
	}

	public static void LOG_E(String TAG, String msg) {
		if (Constant.isDebug)
			Log.e(TAG, msg);
	}

	public static void sysout(String clazz, String text) {
		if (Constant.isDebug)
			System.out.println(clazz + " " + text);
	}

	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	public static String getBeforDate(String dateStr, int witch, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(Constant.DATE_JS).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int old = 0;
		switch (witch) {
		case Calendar.YEAR:
			old = c.get(Calendar.YEAR);
			break;
		case Calendar.MONTH:
			old = c.get(Calendar.MONTH);
			break;
		case Calendar.DATE:
			old = c.get(Calendar.DATE);
			break;
		default:
			break;
		}
		c.set(witch, old - num);
		String dayBefore = new SimpleDateFormat(Constant.DATE_JS).format(c.getTime());
		return dayBefore;
	}

	public static String date2Str(Date date, String format) {
		SimpleDateFormat dataFormat = null;
		if (TextUtils.isEmpty(format))
			dataFormat = new SimpleDateFormat(Constant.DATE_DB);
		else
			dataFormat = new SimpleDateFormat(format);
		if (date == null)
			return "";
		return dataFormat.format(date);
	}

	public static Date str2Date(String str, String format) {
		SimpleDateFormat dataFormat = null;
		if (TextUtils.isEmpty(format))
			dataFormat = new SimpleDateFormat(Constant.DATE_DB);
		else
			dataFormat = new SimpleDateFormat(format);
		try {
			return dataFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getFloatStr(float a, float b) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		float pro = (a * 100.0f) / (b * 1.0f);
		if (pro == 0)
			return "0 %";
		String str = decimalFormat.format(pro);
		if (str.endsWith("0")) {
			str = str.substring(0, str.lastIndexOf("0"));
			if (str.endsWith("0")) {
				str = str.substring(0, str.lastIndexOf("0"));
			}
			if (str.endsWith(".")) {
				str = str.substring(0, str.lastIndexOf("."));
			}
		}
		return str + " %";
	}

	public static String byteToHexString(int ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static boolean isMail(String mail) {
		boolean flag = false;
		try {
			String check = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(mail);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	public static boolean existSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return freeBlocks * blockSize; // 单位MB
	}
	
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);

	      String device_id = tm.getDeviceId();

	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }

	      json.put("device_id", device_id);

	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
	
	
	public static boolean isPhone(String text){
		String regExp = "^[1]([3][0-9]{1}|[5][0-9]{1}|[7][0-9]{1}|[8][0-9]{1})[0-9]{8}$";  
		Pattern p = Pattern.compile(regExp);  
		Matcher m = p.matcher(text); 
		return m.find();
	}
}
