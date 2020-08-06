package com.openxu.core.utils;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Author: openXu
 * Time: 2019/4/1 15:18
 * class: FClickUtil
 * Description: 按钮点击工具
 */
public class XClickUtil {


    private static final int fastTimeSpace = 500;
    /**
     * 防止快速连续点击
     *
     * @return
     */
    private static long lastClickTime = 0;

    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        long time = Math.abs(currentTime - lastClickTime);
        if (time > fastTimeSpace) {
            lastClickTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 防重复点击
     *
     * @param view
     * @param action
     * @return
     */
    public static void onClick(LifecycleOwner owner, View view, Action action) {
        Observable.create(new ClickObservableOnSubscribe(view))
                //监听取消订阅
                .doOnDispose(() -> view.setOnClickListener(null))
                //1000毫秒内之发射第一次
                .throttleFirst(fastTimeSpace, TimeUnit.MILLISECONDS)
                // .debounce(fastTimeSpace, TimeUnit.MILLISECONDS)   //debounce 操作符产生一个新的 Observable, 这个 Observable 只发射原 Observable 中时间间隔小于指定阈值的最大子序列的最后一个元素
                //OnDestory时自动解绑
                .as(AutoDispose.autoDisposable(
                        AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY)))
                .subscribe(integer -> {
                    action.onClick();
                });
    }

    /**
     * 连续快速点击数次
     *
     * @param view
     * @param num
     * @param action
     */
    public static void onFastClick(LifecycleOwner owner, View view, int num, Action action) {
        Observable.create(new ClickObservableOnSubscribe(view))
//        Observable.interval(1000, TimeUnit.MILLISECONDS)
                //监听取消订阅
                .doOnDispose(() -> view.setOnClickListener(null))
                //检查每次点击时间与上次相差是否>200毫秒，如果大于200毫秒则算作第一次点击，否则点击次数+1
                .scan((prev, time) -> {
                    XLog.d("   扫描点击事件：上次点击次数=" + prev + "   本次时间差=" + time);
                    return 1 + (time > 200 ? 0 : prev);
                })
                //过滤掉非连续点击num次的事件
                .filter(count -> count == num)
                //OnDestory时自动解绑
                .as(AutoDispose.autoDisposable(
                        AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY)))
                .subscribe(integer -> action.onClick());
    }

    private static class ClickObservableOnSubscribe implements ObservableOnSubscribe<Integer> {
        private View view;
        private long lastClick;

        public ClickObservableOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        public void subscribe(ObservableEmitter emitter) throws Exception {
            //当被订阅时设置点击事件，每次点击都发射一个事件
            view.setOnClickListener(v -> {
                //发射与上次点击时间差。由于第一次不会执行scan，所以返回1充当一次有效点击
                emitter.onNext((int) (lastClick == 0 ? 1 : System.currentTimeMillis() - lastClick));
                lastClick = System.currentTimeMillis();
            });
        }
    }

    public interface Action {
        void onClick();
    }


}
