package com.openxu.login.login;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.RouterPathCore;
import com.openxu.core.base.XFragmentActivity;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.login.RouterPathLogin;
import com.openxu.login.bean.User;
import com.openxu.login.databinding.LoginActivityLoginBinding;
import com.openxu.core.base.XBaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.login.R;
/**
 * Author: openXu
 * Time: 2020/08/17 16:24
 * class: LoginActivity
 * Description:
 * Update:
 */
//注册路由路径
@Route(path = RouterPathLogin.PAGE_LOGIN)
//继承XBaseActivity，第一个泛型为布局生成的binding类，第二个为该页面的ViewModel类
public class LoginActivity extends XBaseActivity<LoginActivityLoginBinding,  LoginActivityVM > {
    //返回布局id
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.login_activity_login;
    }
    @Override
    public void initView() {
        // 控件初始化，设置事件
        binding.tvLogin.setOnClickListener(v->{
            //调用viewmodel的登录方法
            viewModel.login(binding.etName.getText().toString().trim(), binding.etPsw.getText().toString().trim());
        });
        binding.tvRegist.setOnClickListener(v->{
            //路由到注册页，由于注册页是继承XBaseFragment，使用XFragmentActivity提供的路由方法
            XFragmentActivity.start(LoginActivity.this,
                    ARouter.getInstance().build(RouterPathLogin.PAGE_REGIST)
                            .withString("username", binding.etName.getText().toString().trim()));
        });
    }
    @Override
    public void registObserve() {
        // 为ViewModel中LiveData类型数据注册监听，当数据变化时更新UI (通常通过binding.setXXX)
        viewModel.userData.observe(this, user -> {
            XToast.success("恭喜"+user.getName()+"登成功").show();
            /**
             * 路由到主页面
             * 注意：该模块将登录、注册抽取为单独模块，是假设有多个项目共用登陆注册逻辑，但是每个项目的主页面不同，该怎么路由到各个项目的主页面去呢？
             * 可以将主页的路由清单放到library_core中，这样就可以在这里引用了，每个项目主页面虽然注册的路由路径一样，但是只能同时运行其中一个
             */
//            ARouter.getInstance().build(RouterPathCore.PAGE_MAIN).navigation();
        });
    }
    @Override
    public void initData() {
        // 通常调用viewModel获取数据
//        viewModel.init();
    }
}
