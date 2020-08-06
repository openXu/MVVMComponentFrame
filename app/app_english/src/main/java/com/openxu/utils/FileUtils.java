package com.openxu.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
    private static final String TAG = "FileUtils";

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}else if (temp.isDirectory()){
				delAllFile(temp.getAbsolutePath());
			}
		}
		return true;
	}
	

	public static int getlist(File f) {// 递归求取目录文件个数
		int size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
	

	public static ArrayList<String> getFileList(File f) {// 递归求取目录文件个数
		int size = 0;
		File flist[] = f.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isFile()) {
				files.add(flist[i].getAbsolutePath());
			}
		}
		return files;
	}
	

	public static boolean checkSDCard(Context context) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		} else {
			return true;
		}
	}
	

	public static  byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	/**
     * 获取/data/data/files目录
     * @param context
     * @return
     */
    public static File getFileDirectory(Context context) {
        File appCacheDir=null;
        if(appCacheDir == null) {
            appCacheDir=context.getFilesDir();
        }
        if(appCacheDir == null) {
            String cacheDirPath="/data/data/" + context.getPackageName() + "/files/";
            appCacheDir=new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 获取缓存文件夹
     * @param context
     * @param preferExternal
     * @param dirName
     * @return
     */
    public static File getCacheDirectory(Context context, boolean preferExternal,String dirName) {
        File appCacheDir = null;
        //如果有权限，存sd卡
        if (preferExternal && checkSDCard(context) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context,dirName);
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);   //
            if(!appCacheDir.exists()){
            	if (!appCacheDir.mkdir()) {
                    Log.w(TAG,"Unable to create external cache directory");
                    return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
                }
            }
            appCacheDir = new File(Constant.CATCH_DIR+File.separator+dirName); 
            if(!appCacheDir.exists()){
            	if (!appCacheDir.mkdir()) {
                    Log.w(TAG,"Unable to create external cache directory");
                    return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
                }
            }
            Log.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
        }
        return appCacheDir;
    }
    /** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

    private static File getExternalCacheDir(Context context,String dirName) {
        File dataDir = new File(Constant.CATCH_DIR);   //
        if(!dataDir.exists()){
        	if (!dataDir.mkdir()) {
                Log.w(TAG,"Unable to create external cache directory");
                return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
            }
        }
        dataDir = new File(Constant.CATCH_DIR+File.separator+dirName); 
        if(!dataDir.exists()){
        	if (!dataDir.mkdir()) {
                Log.w(TAG,"Unable to create external cache directory");
                return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
            }
        }
        return dataDir;
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
    
}
