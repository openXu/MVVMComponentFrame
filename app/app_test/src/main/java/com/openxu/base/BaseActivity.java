package com.openxu.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.openxu.core.R;
import com.openxu.core.utils.XBarUtils;
import com.openxu.core.utils.XStatusBarUtil;
import com.openxu.core.view.TitleLayout;

/**
 * Author: openXu
 * Time: 2020/5/12 11:15
 * class: BaseActivity
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG;
    protected Context mContext;
    protected TitleLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = this;
        setContentView(getContentView(savedInstanceState));
        initTitleView();
        initView();
    }

    /**返回Activity布局文件*/
    protected abstract int getContentView(Bundle savedInstanceState);
    /**初始化控件*/
    protected abstract void initView();
    /**初始化TitleLayout*/
    public void initTitleView() {
        //状态栏字体变黑色（透明状态栏）
        //XStatusBarUtil.darkMode(this);
        //全透明状态栏(状态栏字体默认白色)
        XStatusBarUtil.immersive(this);
        //设置导航栏颜色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && XBarUtils.isSupportNavBar()){
            XBarUtils.setNavBarVisibility(this, true);
            XBarUtils.setNavBarColor(this, Color.parseColor("#dddddd"));
        }
        titleLayout = findViewById(R.id.titleLayout);
        if(null!=titleLayout){
            //状态栏占位
            XStatusBarUtil.setPaddingSmart(this, titleLayout);
            //设置菜单点击事件
            titleLayout.setOnMenuClickListener((menu, view) -> onMenuClick(menu, view));
        }
    }

    /**
     * 标题栏菜单点击事件，其中返回键已做默认返回处理，如果子Activity需要处理其他标题菜单事件，请重写此方法
     * @param menu
     * @param view
     */
    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) {
        switch (menu) {
            case MENU_BACK:   //返回键（已做默认处理） ((Activity) getContext()).onBackPressed();
                break;
            case MENU_LEFT_TEXT:
                //左侧标题文字点击
                break;
            case MENU_CENTER:
                //中间标题文字点击
                break;
            case MENU_RIGHT_TEXT:
                //右侧标题文字点击
                break;
            case MENU_RIGHT_ICON:
                //右侧图标菜单点击
                break;
        }
    }
}
