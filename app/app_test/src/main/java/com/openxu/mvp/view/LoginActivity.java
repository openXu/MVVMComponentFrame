package com.openxu.mvp.view;


import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.openxu.base.BaseActivity;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.mvp.model.User;
import com.openxu.mvp.presenter.ILoginPresenter;
import com.openxu.mvp.presenter.LoginPresenterImpl;
import com.openxu.mvvm.R;

public class LoginActivity extends BaseActivity implements ILoginView{

    private Button btn_login;
    private EditText et_name, et_pwd;
    private ProgressBar progressBar;
    private ILoginPresenter presenter;

    @Override
    protected int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        btn_login = findViewById(R.id.btn_login);
        et_name = findViewById(R.id.et_name);
        et_pwd = findViewById(R.id.et_pwd);
        progressBar = findViewById(R.id.progressBar);
        presenter = new LoginPresenterImpl(this);
        btn_login.setOnClickListener(v -> {
            User user = new User(et_name.getText().toString().trim(), et_pwd.getText().toString().trim());
            presenter.validateCredentials(user);
        });
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void loginSuccess() {
        XToast.success("登录成功").show();
    }
    @Override
    public void loginError() {
        XToast.error("登录失败，请检查用户名密码").show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
