package com.openxu.core.application;

import android.annotation.SuppressLint;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.BuildConfig;
import com.openxu.core.config.AppConfig;
import com.openxu.core.crash.CaocConfig;
import com.openxu.core.crash.DefaultErrorActivity;
import com.openxu.core.utils.XFiles;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XUtils;

/**
 * Author: openXu
 * Time: 2019/2/23 14:49
 * class: BaseLibInitLogic
 * Description: Core库需要在Application启动时初始化的内容，在onCreate()中完成初始化
 */
public class CoreInitLogic extends XBaseAppLogic {

    @SuppressLint("RestrictedApi")
    @Override
    public void onApplicationCreate(String processName) {

        XLog.i("进程:" + processName + "  CoreInitLogic初始化");
        //创建应用缓存目录
        XFiles.initFileDir();
        //初始化工具
        XUtils.init(mApplication);
        MultiDex.install(mApplication);
        /**初始化阿里路由框架*/
        if (BuildConfig.DEBUG) {
            XLog.i("debug模式:"+BuildConfig.DEBUG);
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.printStackTrace();
        }
        ARouter.init(mApplication); // 尽可能早，推荐在Application中初始化
//        GreenDaoManager.init(mApplication);

        //初始化异常捕获
        CaocConfig.Builder.create()
                //程序在后台时，发生崩溃的处理方式
                .backgroundMode(AppConfig.DEBUG?
                        CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面
                        CaocConfig.BACKGROUND_MODE_SILENT)      //当应用程序处于后台时崩溃，默默地关闭程序！
                .enabled(true) //default: true
                .minTimeBetweenCrashesMs(3000) //default: 3000定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中
//                .restartActivity(YourCustomActivity.class) //default: null (your app's launch activity)
                .errorActivity(DefaultErrorActivity.class) //default: null (default error activity)
//                .eventListener(crashListener) //default: null
                .apply();
    }


}
