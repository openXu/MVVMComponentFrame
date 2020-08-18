package com.openxu.login.regist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.openxu.login.RouterPathLogin;
import com.openxu.login.databinding.LoginFragmentRegistBinding;
import com.openxu.core.base.XBaseFragment;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.login.R;

/**
 * Author: openXu
 * Time: 2020/08/17 16:27
 * class: RegistFragment
 * Description:
 * Update:
 */
@Route(path = RouterPathLogin.PAGE_REGIST)
public class RegistFragment extends XBaseFragment<LoginFragmentRegistBinding, RegistFragmentVM> {

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.login_fragment_regist;
    }
    @Override
    public void initView() {
        // 控件初始化，设置事件
    }
    @Override
    public void registObserve() {
        // 为ViewModel中LiveData类型数据注册监听，当数据变化时更新UI (通常通过binding.setXXX)
    }
    @Override
    public void initData() {
        // 通常调用viewModel获取数据
    }
}
