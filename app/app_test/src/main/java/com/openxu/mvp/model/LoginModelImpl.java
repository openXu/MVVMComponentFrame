package com.openxu.mvp.model;

import android.os.Handler;
import android.text.TextUtils;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.mvp.presenter.ILoginListener;

/**
 * Author: openXu
 * Time: 2020/5/13 13:32
 * class: LoginModelImpl
 * Description:
 */
public class LoginModelImpl implements ILoginModel {

    @Override
    public void login(User user, ILoginListener listener){
        final String username = user.getUserName();
        final String password = user.getUserPwd();
        //延迟2s模拟请求登录接口
        new Handler().postDelayed(() -> {
            if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getUserPwd())){
                listener.onError();
                return;
            }
            listener.onSuccess();
        }, 2000);
    }
}
