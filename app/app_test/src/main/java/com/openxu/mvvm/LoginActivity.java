package com.openxu.mvvm;

import android.os.Bundle;
import android.view.View;
import com.openxu.bean.User;
import com.openxu.core.base.XBaseActivity;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.mvvm.databinding.ActivityLoginBinding;

public class LoginActivity extends XBaseActivity<ActivityLoginBinding, LoginViewModel> {

//    private Button btn_login;
//    private EditText et_name, et_pwd;
//    private ProgressBar progressBar;

    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
//        btn_login = findViewById(R.id.btn_login);
//        et_name = findViewById(R.id.et_name);
//        et_pwd = findViewById(R.id.et_pwd);
//        progressBar = findViewById(R.id.progressBar);
        //xml布局中带id的控件对象
        String userName = binding.etName.getText().toString().trim();
        String userPwd = binding.etPwd.getText().toString().trim();
        binding.setVm(viewModel);
        User user = new User(null, null);
        binding.setUser(user);
        binding.btnLogin.setOnClickListener(v -> {
            XLog.v("自动绑定了数据："+user.getUserName());
            XLog.v("自动绑定了数据："+binding.getUser().getUserName());
            //★调用viewModel的login()方法实现登录接口请求
//            User user = new User(binding.etName.getText().toString().trim(), binding.etPwd.getText().toString().trim());
            //设置<data>变量
            binding.setUser(new User(user.getUserName(), user.getUserPwd()));
            viewModel.login(binding.getUser());

//            startActivity(new Intent(this, Login2Activity.class));
        });
    }

    @Override
    public void registObserve() {
        //★在Activity中监听viewModel的LiveData数据变化，实现ViewModel和Activity的通信
        viewModel.showProgress.observe(this, aBoolean -> {
            XLog.v("showProgress数据变化了："+aBoolean);
            binding.progressBar.setVisibility(aBoolean?View.VISIBLE:View.GONE);
        });
        viewModel.loginResult.observe(this, aBoolean -> {
            XLog.v("loginResult数据变化了："+aBoolean);
            if(aBoolean)
                XToast.success("登录成功").show();
            else
                XToast.error("登录失败，请检查用户名密码").show();
        });
    }

    @Override
    public void initData() {

    }

}
