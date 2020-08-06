package com.openxu.core.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.openxu.core.R;
import com.openxu.core.utils.XLog;

/**
 * Author: openXu
 * Time: 2020/7/29 11:03
 * class: XLifecycleOwnerActivity
 * Description: 自定义LifecycleOwner示例
 */
public class XLifecycleOwnerActivity extends Activity implements LifecycleOwner {
    //用于管理组件生命周期状态信息，他可以被多个LifecycleObserver对象观察
    private LifecycleRegistry lifecycleRegistry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_test_activity_xl);
        XLog.v("----------------onCreate");
        //创建注册者，构造方法中接受生命周期提供者
        lifecycleRegistry = new LifecycleRegistry(this);
        //设置当前生命周期状态为CREATED
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        XBaseViewModel viewModel = new XBaseViewModel(getApplication());
        getLifecycle().addObserver(viewModel);
    }
    @Override
    public void onStart() {
        super.onStart();
        XLog.v("----------------onStart");
        //设置当前生命周期状态为STARTED
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
    }
    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        //实现LifecycleOwner接口的方法，返回lifecycleRegistry对象，这样我们自定义的Activity就能被LifecycleObserver观察了
        return lifecycleRegistry;
    }
}
