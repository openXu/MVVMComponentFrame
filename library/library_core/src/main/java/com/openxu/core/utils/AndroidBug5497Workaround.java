package com.openxu.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

/**
 * autour : openXu
 * date : 2018/3/30 10:03
 * className : AndroidBug5497Workaround
 * version : 1.0
 * description : 解决键盘挡住输入框问题（全屏模式或者带WebView的界面）
 */
public class AndroidBug5497Workaround {
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    private boolean fullScreen = false;  //是否全屏

    public static void assistActivity(Activity activity, boolean fullScreen) {
        new AndroidBug5497Workaround(activity, fullScreen);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity, boolean fullScreen) {
        this.fullScreen = fullScreen;
        FrameLayout content = activity.findViewById(android.R.id.content);
        //获取activity布局
        mChildOfContent = content.getChildAt(0);
        //监听View树变化
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        int rootHeight = mChildOfContent.getRootView().getHeight();
        //rootHeight = 1964  可用空间：2071   当前空间：2097
        XLog.w("rootHeight = " + rootHeight + "  可用空间：" + usableHeightNow + "   当前空间：" + usableHeightPrevious);
        //如果可用空间和当前空间不一致，重新布局
        if (usableHeightNow != usableHeightPrevious) {
            //如果两次高度不一致
            //将计算的可视高度设置成视图的高度
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();//请求重新布局
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        //获取Activity布局的可用空间
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        int height = fullScreen ? r.bottom : (r.bottom - r.top); // 全屏模式下： return r.bottom
        return height;
    }

    public int getNavigationBarHeight(Activity activity) {
        int height = 0;
        if (checkDeviceHasNavigationBar(activity)) {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        }
        XLog.v("导航栏高度navigation bar>>> height:" + height);
        return height;
    }

    //获取是否存在NavigationBar
    private boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        XLog.v("是否存在导航栏" + hasNavigationBar);
        return hasNavigationBar;

    }
}
