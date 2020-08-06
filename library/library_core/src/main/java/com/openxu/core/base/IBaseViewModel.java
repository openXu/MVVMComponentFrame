package com.openxu.core.base;

/**
 * Author: openXu
 * Time: 2020/7/29 11:13
 * class: IBaseViewModel
 * Description:LifecycleObserver将类标记为生命周期观察者，它没有任何方法，而是依赖于@OnLifecycleEvent注释标记某个方法在组件的哪个生命周期状态下调用
 */
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
public interface IBaseViewModel extends LifecycleObserver {

    //ON_ANY可以匹配所有生命周期方法
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onAny(LifecycleOwner owner, Lifecycle.Event event);

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();
}
