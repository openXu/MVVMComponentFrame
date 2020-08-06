package com.openxu.mvp.view;

/**
 * Author: openXu
 * Time: 2020/5/13 13:34
 * class: ILoginView
 * Description:
 */
public interface ILoginView {

    //显示进度对话框
    void showProgress();
    //隐藏进度对话框
    void hideProgress();
    //login成功
    void loginSuccess();
    //失败给出提示
    void loginError();

}
