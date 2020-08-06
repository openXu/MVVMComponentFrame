package com.openxu.core.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.openxu.core.config.AppConfig;
import com.openxu.core.utils.XLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: openXu
 * Time: 2020/8/6 10:28
 * class: XAppInitMng
 * Description:
 */
public class XAppInitManager {

    private List<XBaseAppLogic> logicClassList = new ArrayList<>();

    //类库初始化类名
    private static String[] initLogicNames = {
            "com.openxu.core.application.CoreInitLogic",   //核心库
            "com.openxu.frame.BaseLibInitLogic",   //基础库
    };

    public static void init(Application application){
        logicCreate(application, getCurrentProcessName(application));
    }
    /**
     * 获取当前进程名 com.openxu.english
     * @return
     */
    private static String getCurrentProcessName(Application application){
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return "";
    }
    private static void logicCreate(Application application, String processName) {
        List<String> list = new ArrayList<>();
        //库初始化逻辑
        list.addAll(Arrays.asList(initLogicNames));
        //其业务模块初始化逻辑
        if(!TextUtils.isEmpty(AppConfig.applicationInitClass)){
            list.addAll(Arrays.asList(AppConfig.applicationInitClass.split(",")));
        }
        for (int i = 0; i<list.size(); i++) {
            String name = list.get(i);
            try {
                //使用反射初始化调用
                Class clazz = Class.forName(name);
                XBaseAppLogic appLogic = (XBaseAppLogic) clazz.newInstance();
                appLogic.setApplication(application);
                XLog.w((i+1)+"-执行组件初始化：" + name);
                appLogic.onApplicationCreate(processName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                XLog.e("应用程序初始化类未找到：" + e.getMessage());
            }
        }
    }


}
