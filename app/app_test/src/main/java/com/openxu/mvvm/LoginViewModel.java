package com.openxu.mvvm;


import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.openxu.bean.User;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;

/**
 * Author: openXu
 * Time: 2020/5/13 16:56
 * class: LoginViewModel
 * Description:
 *
 * 需要继承ViewModel类，ViewModel类源码注释有示例怎样使用。
 *
 * 1. ViewModel负责为Activity/Fragment准备和管理数据，它还可以处理一部分业务逻辑。
 * 2. ViewModel总是与Activity/Fragment关联创建，并且只要Activity/Fragment还活着就会保留，直到被销毁。
 * 换句话说，这意味着如果ViewModel的所有者因配置更改（例如旋转）而被销毁，则ViewModel不会被销毁。所有者的新实例将重新连接到现有的ViewModel。
 * 3. ViewModel通常通过LiveData或者DataBinding实现与Activity/Fragment的数据通信
 * 4. ViewModel的唯一职责是管理数据，他不应该持有Activity/Fragment的引用
 *
 */
public class LoginViewModel extends XBaseViewModel {

    /**
     * LiveData是一个抽象类，而且它没有公开可用的方法来更新存储的数据，应该使用其子类MutableLiveData
     * MutableLiveData类公开了setValue(T)和postValue(T)方法用于更新存储的数据
     * setValue(T)应该在主线程中调用，postValue(T)应该在子线程中调用
     */
    public final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> showProgress = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }


    public void login(View v){
        XLog.v("vm中登陆啊啊啊啊啊啊啊啊");
    }

    public void login(User user){
        //显示进度对话框
        showProgress.setValue(true);
        //延迟2s模拟请求登录接口
        new Handler().postDelayed(() -> {
            //隐藏进度对话框
            showProgress.setValue(false);
            if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getUserPwd())){
                //登录失败，设置结果数据为false
                loginResult.setValue(false);
                return;
            }
            //登录成功，设置结果数据为true
            loginResult.setValue(true);
        }, 2000);
    }

    /**
     * Activity调用onDestroy()之前会调用此方法
     * 表示ViewModel不再使用并将销毁，可以在此进行清除数据的操作，避免内存泄漏
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        XLog.w("======ViewModel.onCleared()销毁"+this);
    }
}
