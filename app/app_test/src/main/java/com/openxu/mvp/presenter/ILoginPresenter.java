package com.openxu.mvp.presenter;

import com.openxu.mvp.model.User;

/**
 * Author: openXu
 * Time: 2020/5/13 13:41
 * class: ILoginPresenter
 * Description:
 */
public interface ILoginPresenter {
    void validateCredentials(User user);

    void onDestroy();
}
