package com.openxu.single;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.base.XBaseActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.base.XFragmentActivity;
import com.openxu.fanyi.RouterPathFanyi;
import com.openxu.single.databinding.ActivitySplashBinding;

/**
 * Author: openXu
 * Time: 2020/8/7 9:20
 * class: SplashActivity
 * Description:
 */
public class SplashActivity extends XBaseActivity<ActivitySplashBinding, XBaseViewModel> {
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }
    @Override
    public void initView() {
        /**★★★设置调试模块入口页面路由*/
        XFragmentActivity.start(this,
                ARouter.getInstance().build(RouterPathFanyi.PAGE_FRAGMENT_FANYI));
    }
    @Override
    public void registObserve() {
    }
    @Override
    public void initData() {
    }
}
