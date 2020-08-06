package com.openxu.english.splash;


import android.content.BroadcastReceiver;
import android.os.Build;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.base.DataBinderMapperImpl;
import com.openxu.core.base.XBaseActivity;
import com.openxu.core.utils.XBarUtils;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XStatusBarUtil;
import com.openxu.english.R;
import com.openxu.english.RouterPathEnglish;
import com.openxu.english.databinding.ActivitySplashBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Author: openXu
 * Time: 2019/3/1 12:28
 * class: SplashActivity
 * Description:
 */
@Route(path = RouterPathEnglish.PAGE_SPLASh)
public class SplashActivity extends XBaseActivity<ActivitySplashBinding, SplashActivityVM> {

    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }
    @Override
    public void initView() {
        XStatusBarUtil.darkMode(this);   //状态栏字体变黑色（透明状态栏）
        //导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && XBarUtils.isSupportNavBar()) {
            XBarUtils.setNavBarVisibility(this, false);
//            FBarUtils.setNavBarColor(this, Color.parseColor("#ffffff"));
        }
    }
    @Override
    public void registObserve() {
    }
    @Override
    public void initData() {

        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(aLong -> {
                    XLog.i("定时跳转："+aLong);
                    Postcard postcard = ARouter.getInstance().build(RouterPathEnglish.PAGE_MAIN);
                    //点击通知栏消息
                    postcard.navigation();
                    finish();
                });


    }
}
