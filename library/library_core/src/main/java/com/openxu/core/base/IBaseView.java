package com.openxu.core.base;

/**
 * Author: openXu
 * Time: 2019/3/15 9:42
 * class: IBaseView
 * Description: 定义View(Activity、Fragment)的模板方法
 */
public interface IBaseView {
    /**初始化控件*/
    void initTitleView();
    /**初始化控件*/
    void initView();
    /**页面事件监听的方法，一般用于注册ViewModle中数据监听 */
    void registObserve();
    /**初始化数据*/
    void initData();

}
