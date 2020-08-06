package com.openxu.mvvm;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Author: openXu
 * Time: 2020/5/14 17:10
 * class: LoginFragment
 * Description:
 */
public class LoginFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        //★在Activity中监听viewModel的LiveData数据变化，实现ViewModel和Activity的通信
        viewModel.showProgress.observe(this, aBoolean -> {
        });
    }
}
