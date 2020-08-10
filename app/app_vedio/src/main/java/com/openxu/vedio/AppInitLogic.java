package com.openxu.vedio;

import android.annotation.SuppressLint;

import com.openxu.core.application.XBaseAppLogic;
import com.openxu.core.config.AppConfig;


/**
 * Author: openXu
 * Time: 2019/2/23 14:49
 * class: AppInitLogic
 * Description: app中需要在Application启动时初始化的内容，在onCreate()中完成初始化
 */
public class AppInitLogic extends XBaseAppLogic {
    @SuppressLint("RestrictedApi")
    @Override
    public void onApplicationCreate(String processName) {
        if (processName.equals(AppConfig.appId)) {
        }
    }

}
