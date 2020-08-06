package com.openxu.core.utils;

import android.os.Environment;
import com.openxu.core.BuildConfig;
import com.openxu.core.config.AppConfig;

import java.io.File;


/**
 * autour : openXu
 * date : 2017/11/6 11:24
 * className : FpcFiles
 * version : 1.0
 * description : 文件相关工具类
 */
public class XFiles {

    private static String TAG = "XFiles";

    /*app存储根目录*/
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + AppConfig.appFileRoot;
    /*版本升级下载路径*/
    private static final String PATH_UPDATE = ROOT_PATH + File.separator+ "appDownload";     //sd/xxx/appDownload
    /*崩溃日志*/
    private static final String PATH_ERROR = ROOT_PATH + File.separator+ "errorlog";
    /*图片缓存*/
    private static final String PATH_CACHE = ROOT_PATH + File.separator+ "cache";
    /*重要文件存储*/
    private static final String PATH_STORAGE = ROOT_PATH + File.separator+ "storage";
    /*临时目录*/
    private static final String PATH_TEMP = ROOT_PATH + File.separator+ "temp";
    /**
     * 初始化存储文件夹，此方法需要在Application.onCreate()中调用
     * @return
     */
    public static boolean initFileDir(){
        try{
            File file = new File(ROOT_PATH);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            file = new File(PATH_UPDATE);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            file = new File(PATH_ERROR);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            file = new File(PATH_CACHE);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            file = new File(PATH_STORAGE);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            file = new File(PATH_TEMP);
            if(!file.exists() || !file.isDirectory()){
                file.mkdir();
            }
            XLog.i("初始化存储文件夹...");
        }catch (Exception e){
            return false;
        }
        return true;
    }
    /**获取版本升级下载目录*/
    public static String getRootPath(){
        File file = new File(ROOT_PATH);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return ROOT_PATH;
    }
    /**获取版本升级下载目录*/
    public static String getUpdatePath(){
        File file = new File(PATH_UPDATE);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return PATH_UPDATE;
    }
    /**获取版本升级下载目录*/
    public static String getErrorPath(){
        File file = new File(PATH_ERROR);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return PATH_ERROR;
    }
    /**获取版本升级下载目录*/
    public static String getCachePath(){
        File file = new File(PATH_CACHE);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return PATH_CACHE;
    }
    /**获取版本升级下载目录*/
    public static String getStoragePath(){
        File file = new File(PATH_STORAGE);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return PATH_STORAGE;
    }
    /**获取临时目录*/
    public static String getTempPath(){
        File file = new File(PATH_TEMP);
        if(!file.exists() || !file.isDirectory()){
            initFileDir();
        }
        return PATH_TEMP;
    }

    /*************************工具方法************************/
    /**
     * 在目录下创建临时文件
     * @param path 父文件夹
     * @param fileName 文件名
     * @return 创建的文件
     */
    public static File createFile(String path, String fileName, String suffix) {
        try {
            File file = new File(path);
            if (!file.exists()|| !file.isDirectory())
                file.mkdir();
           /* File tempFile = File.createTempFile(fileName, //文件名
                    suffix, //后缀
                    file);   //目录*/
            file = new File(path+File.separator+fileName+suffix);
            XLog.i("创建文件="+(file.exists()&&file.isFile()));
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
