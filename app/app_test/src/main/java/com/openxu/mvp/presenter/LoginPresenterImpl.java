package com.openxu.mvp.presenter;

import com.openxu.mvp.model.ILoginModel;
import com.openxu.mvp.model.LoginModelImpl;
import com.openxu.mvp.model.User;
import com.openxu.mvp.view.ILoginView;

/**
 * Author: openXu
 * Time: 2020/5/13 13:42
 * class: LoginPresenterImpl
 * Description:
 */
public class LoginPresenterImpl implements ILoginPresenter, ILoginListener {
    private ILoginView loginView;
    private ILoginModel loginModel;
    public LoginPresenterImpl(ILoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModelImpl();
    }
    @Override
    public void validateCredentials(User user) {
        loginView.showProgress();
        loginModel.login(user, this);
    }
    @Override
    public void onError() {
        loginView.hideProgress();
        loginView.loginError();
    }
    @Override
    public void onSuccess() {
        loginView.hideProgress();
        loginView.loginSuccess();
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }
}
