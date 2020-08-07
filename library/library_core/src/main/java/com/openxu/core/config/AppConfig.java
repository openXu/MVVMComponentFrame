package com.openxu.core.config;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


import com.openxu.core.BuildConfig;
import com.openxu.core.utils.XLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Author: openXu
 * Time: 2019/3/14 17:32
 * class: AppConfig
 * Description: BuildConfig代替类，避免倒错包
 */
public class AppConfig {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    //app包名
    public static final String appId = BuildConfig.appId;
    // Fields from build type: debug
    public static final String appType = BuildConfig.appType;
//    public static final String baseUrl = BuildConfig.baseUrl;
    //app存储文件夹名
    public static final String appFileRoot = BuildConfig.appFileRoot;
    ////Application初始化类，使用,分割
    public static final String applicationInitClass = BuildConfig.applicationInitClass;
    //接口基础部分
    public static final String baseUrl = BuildConfig.baseUrl;


     public static String deviceID;
    /**
     * 获取设备唯一ID
     * @param context 上下文
     * @return 设备ID
     */
    public static String getDeviceID(Context context){
        if(TextUtils.isEmpty(deviceID)){
            String m_szDevIDShort = "35" +
                    Build.BOARD.length()%10 +
                    Build.BRAND.length()%10 +
                    Build.DEVICE.length()%10 +
                    Build.DISPLAY.length()%10 +
                    Build.HOST.length()%10 +
                    Build.ID.length()%10 +
                    Build.MANUFACTURER.length()%10 +
                    Build.MODEL.length()%10 +
                    Build.PRODUCT.length()%10 +
                    Build.TAGS.length()%10 +
                    Build.TYPE.length()%10 +
                    Build.USER.length()%10 ;
            String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
            BluetoothAdapter m_BluetoothAdapter;
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String m_szBTMAC = "";
            if(m_BluetoothAdapter != null) {
                m_szBTMAC = m_BluetoothAdapter.getAddress();
            }
//        String m_szLongID = m_szDevIDShort + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;
            String m_szLongID = m_szDevIDShort + m_szAndroidID + m_szBTMAC;
            //计算MD5
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if(messageDigest == null) {
                return UUID.randomUUID().toString();
            }
            messageDigest.update(m_szLongID.getBytes(),0,m_szLongID.length());
            byte p_md5Data[] = messageDigest.digest();
            //创建一个16进制数
            String m_szUniqueID = "";
            for (byte aP_md5Data : p_md5Data) {
                int b = (0xFF & aP_md5Data);
                if (b <= 0xF)
                    m_szUniqueID += "0";
                m_szUniqueID += Integer.toHexString(b);
            }
            m_szUniqueID = m_szUniqueID.toUpperCase();
            // TODO 20190117 同一设备上不同app，如果设备ID相同，后台只会维持最后的连接，现在还没有想到更好的办法，先把设备ID后面拼接包名，让每个应用都能连接上
//            deviceID = m_szUniqueID + productAppId;
            deviceID = m_szUniqueID;
        }
        XLog.w("获取设备唯一标识deviceID："+deviceID);
        return deviceID;
    }
}
