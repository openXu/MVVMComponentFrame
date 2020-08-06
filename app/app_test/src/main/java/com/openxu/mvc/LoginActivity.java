package com.openxu.mvc;


import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.openxu.base.BaseActivity;
import com.openxu.bean.User;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.mvvm.R;

public class LoginActivity extends BaseActivity {

    private Button btn_login;
    private EditText et_name, et_pwd;
    private ProgressBar progressBar;

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

        btn_login.setOnClickListener(v -> {
            User user = new User(et_name.getText().toString().trim(), et_pwd.getText().toString().trim());
            //延迟2s模拟请求登录接口
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getUserPwd())){
                    XToast.error("登录失败，请检查用户名密码").show();
                    return;
                }
                XToast.success("登录成功").show();
            }, 2000);
        });
    }
}
