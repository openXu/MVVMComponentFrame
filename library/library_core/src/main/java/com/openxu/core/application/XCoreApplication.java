package com.openxu.core.application;

import android.app.Application;
import android.content.Context;

/**
 * Author: openXu
 * Time: 2019/2/23 14:49
 * class: XCoreApplication
 * Description: Application请继承该基类，如果不能继承该类，请在自己的Application的onCreate方法中调用：
 * //初始化类库
 * XAppInitManager.init(this);
 */
public class XCoreApplication extends Application {


    private static Context mcontext;
    protected static XCoreApplication application;

    @Override
    public void onCreate() {
        application = this;
        mcontext = getApplicationContext();
        super.onCreate();
        //初始化类库
        XAppInitManager.init(this);
    }

    public static XCoreApplication getApplication() {
        return application;
    }
    public static Context getContext() {
        return mcontext;
    }





}
