package com.openxu.db;

import android.annotation.SuppressLint;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.BuildConfig;
import com.openxu.core.application.XBaseAppLogic;
import com.openxu.core.config.AppConfig;
import com.openxu.core.crash.CaocConfig;
import com.openxu.core.crash.DefaultErrorActivity;
import com.openxu.core.utils.XFiles;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XUtils;
import com.openxu.db.manager.GreenDaoManager;

/**
 * Author: openXu
 * Time: 2019/2/23 14:49
 * class: BaseLibInitLogic
 * Description: Core库需要在Application启动时初始化的内容，在onCreate()中完成初始化
 */
public class DbInitLogic extends XBaseAppLogic {

    @SuppressLint("RestrictedApi")
    @Override
    public void onApplicationCreate(String processName) {

    }


}
