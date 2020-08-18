package com.openxu.login.login;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.http.NetworkManager;
import com.openxu.core.http.callback.ResponseCallback;
import com.openxu.core.utils.XLog;
import com.openxu.login.LoginService;
import com.openxu.login.bean.User;

import android.app.Application;

/**
 * Author: openXu
 * Time: 2020/08/17 16:24
 * class: LoginActivityVM
 * Description:
 * Update:
 */
public class LoginActivityVM extends XBaseViewModel {


    public MutableLiveData<User> userData = new MutableLiveData<>();

	public LoginActivityVM(@NonNull Application application) {
        super(application);
    }

    //登录
    public void login(String name, String pwd){
        NetworkManager.getInstance().build()
                //必须
                .viewModel(this)   //ViewModel对象
                .url(LoginService.loginUrl)          //url，绝对路径或者相对路径都行，如果设置相对路径，请在conf_app.gradle中配置baseUrl
                .putParam("name", name)
                .putParam("pwd", pwd)
//                .putParams(map)
                //可选
                .showDialog(true)   //是否显示进度条，默认true
                .doGet(new ResponseCallback<User>(User.class) {
                    @Override
                    public void onSuccess(User result){
                        XLog.d("请求数据结果："+result);
                        //将结果设置给livedata，通知ui刷新
                        userData.setValue(result);
                    }
                    @Override
                    public void onFail(String message) {
                        //错误统一Toast处理
                        super.onFail(message);
                    }
                });
    }

}
