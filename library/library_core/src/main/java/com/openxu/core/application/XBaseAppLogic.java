package com.openxu.core.application;


import android.app.Application;
import android.content.res.Configuration;


/**
 * Author: openXu
 * Time: 2019/2/23 14:42
 * class: BaseAppLogic
 * Description: 模块初始化基类，如果module中需要在Application创建时进行初始化操作，
 *              只需要继承该类，覆盖onCreate()进行初始化，并将实现类添加到ModuleInitManager中
 *
 * 注意：如果只需要在特定进程初始化，请通过processName判断
     if (processName.equals(AppConfig.appId)) {
        //主进程

     }else if(processName.equals(xxx)){
        //其他进程
     }
 */
public abstract class XBaseAppLogic {

    protected String TAG;

    protected Application mApplication;

    public XBaseAppLogic(){
        TAG = getClass().getSimpleName();
    }

    public void setApplication(Application application){
        mApplication = application;
    }

    public abstract void onApplicationCreate(String processName);

}
