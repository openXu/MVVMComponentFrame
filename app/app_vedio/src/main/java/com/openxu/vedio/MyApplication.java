package com.openxu.vedio;

import com.openxu.core.application.XCoreApplication;

import org.greenrobot.eventbus.EventBus;

import app_vedio.MyEventBusIndex;


/**
 * Author: openXu
 * Time: 2020/8/3 11:35
 * class: MyApplication
 * Description:
 */
public class MyApplication extends XCoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
