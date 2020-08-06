package com.openxu.mvp.model;

import com.openxu.mvp.presenter.ILoginListener;

/**
 * Author: openXu
 * Time: 2020/5/13 13:32
 * class: ILoginModel
 * Description:
 */
public interface ILoginModel {

    void login(User user, ILoginListener listener);
}
