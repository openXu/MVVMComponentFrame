package com.openxu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.openxu.ui.MainActivity;
import com.openxu.ui.MyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录 发送错误报告.
 */
public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private static final String TAG = "MyUncaughtExceptionHandler";
	private Activity activity;
	/** 系统默认的UncaughtException处理类 */
	private UncaughtExceptionHandler mDefaultHandler;
	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";

	private static final String CRASH_REPORTER_EXTENSION = ".log";
	
	private static MyUncaughtExceptionHandler INSTANCE;

	private MyUncaughtExceptionHandler() {
	}

	public static MyUncaughtExceptionHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MyUncaughtExceptionHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器,
	 * 设置该MyUncaughtExceptionHandler为程序的默认处理器
	 */
	public void init(Activity activity) {
		activity = activity;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		MyUtil.LOG_E(TAG, thread.getName()+ "产生了一个未处理的异常，但是这个异常被哥给捕获了____________________________");
		if (ex != null) {
			MyUtil.LOG_E(TAG, ex.getMessage());
			//07-27 16:26:04.900: E/MyUncaughtExceptionHandler(12283): divide by zero
			if(!ex.getMessage().contains("by zero")){
				collectCrashDeviceInfo(activity);
				// 保存错误报告文件
				saveCrashInfoToFile(ex);
				// 发送错误报告到服务器
				sendCrashReportsToServer(thread, activity);
			}else{
				MyUtil.LOG_E(TAG, "因为还原数据故意抛得/0异常，不上传");
			}
			
			if (thread.getName().equalsIgnoreCase("main")) {
				MyUtil.LOG_E(TAG, "主线程抛异常，重启");
				Intent intent = new Intent(activity, MainActivity.class);
				PendingIntent restarIntent = PendingIntent.getActivity(activity, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis()+1500, restarIntent);
				MyApplication.getApplication().finishActivitys();
				
//				如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
				MobclickAgent.onKillProcess(activity);
				// 早死早超生
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		} else if (mDefaultHandler != null)
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
	}


	public void collectCrashDeviceInfo(Context ctx) {
		MyUtil.LOG_E(TAG, "收集异常信息____________________________");
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME + ":",
						pi.versionName == null ? "not set" : pi.versionName
								+ "||");
				MyUtil.LOG_V(TAG, VERSION_NAME + ":"
						+ (pi.versionName == null ? "not set" : pi.versionName)
						+ "||");
				mDeviceCrashInfo.put(VERSION_CODE + ":", pi.versionCode + "||");
				MyUtil.LOG_V(TAG, VERSION_CODE + ":" + pi.versionCode + "||");
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName() + ":", field.get(null)
						+ "||");
				MyUtil.LOG_V(TAG, field.getName() + ":" + field.get(null)
						+ "||");
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		byte[] byt = result.getBytes();
		byte[] flag = null;
		try {
			flag = "ErrorInfo".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		printWriter.close();
		// mDeviceCrashInfo.put(STACK_TRACE, result);

		try {
			long timestamp = System.currentTimeMillis();
			String fileName = "FirmailError-" + timestamp
					+ CRASH_REPORTER_EXTENSION;
			FileOutputStream trace = activity.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			mDeviceCrashInfo.store(trace, "");
			if (flag != null)
				trace.write(flag, 0, flag.length - 1);
			trace.write(byt, 0, byt.length - 1);
			trace.flush();
			trace.close();
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
		return null;
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 */
	private void sendCrashReportsToServer(final Thread thread, final Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			final TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));
			new AsyncTask<Void, Integer, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					for (String fileName : sortedFiles) {
						try {
							File cr = new File(ctx.getFilesDir(), fileName);
							BufferedReader br = new BufferedReader(
									new FileReader(cr));
							String line = "";
							StringBuffer buffer = new StringBuffer();
							while ((line = br.readLine()) != null) {
								buffer.append(line);
							}
							String fileContent = buffer.toString();
							MyUtil.LOG_I(TAG, "上传错误日志："+fileContent);
							//友盟上传错误日志
							MobclickAgent.reportError(activity, fileContent);
							cr.delete();// 删除已发送的报告
							continue;
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
					publishProgress(1); // 全部上传完成
					return null;
				}

				protected void onProgressUpdate(Integer... values) {
					
				};
			}.execute();
			
			/*
			 * if(thread.getName().equalsIgnoreCase("main")) dialog.show(); else
			 * MyUtil.TOAST(activity, "正在上传错误信息***************************");
			 */
		}
	}

	/**
	 * 获取错误报告文件名
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

}
