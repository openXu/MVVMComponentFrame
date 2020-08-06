package com.openxu.core.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hwangjr.rxbus.RxBus;
import com.openxu.core.R;
import com.openxu.core.dialog.TimeConsumDialog;
import com.openxu.core.utils.XBarUtils;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XStatusBarUtil;
import com.openxu.core.view.TitleLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Author: openXu
 * Time: 2020/5/12 11:15
 * class: BaseActivity
 * Description:
 */
public abstract class XBaseActivity<V extends ViewDataBinding, VM extends XBaseViewModel>
        extends AppCompatActivity implements IBaseActivity {

    protected String TAG;
    protected Context mContext;
    protected TitleLayout titleLayout;
    protected TimeConsumDialog dialog;
    protected V binding;
    protected VM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = this;

        RxBus.get().register(this);
        ARouter.getInstance().inject(this);

        initMVVM(savedInstanceState);
        initTitleView();
        initView();
        registObserve();
        initData();
    }

    private void initMVVM(Bundle savedInstanceState){
        /**1. 注入DataBinding*/
        binding = DataBindingUtil.setContentView(this, getContentView(savedInstanceState));
        /**2. 初始化ViewModel*/
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = XBaseViewModel.class;
        }
        /*
         * 使用new ViewModelProvider(ViewModelStoreOwner owner).get(XXXViewModel.class)获取对应的ViewModel对象
         * ViewModelProvider构造方法接受ViewModelStoreOwner实例，androidx.appcompat.app.AppCompatActivity和androidx.fragment.app.Fragment都实现了ViewModelStoreOwner
         * 第二个参数是ViewModel构造器，如果不传递构造器将使用ViewModel不带参数的构造方法，由于我们的ViewModel继承自AndroidViewModel，构造方法需要传递Application作为参数，所以必须使用构造器来创建ViewModel实例
         *
         * 其实AndroidViewModel就是在ViewModel上封装了应用上下文Application，方便我们在ViewModel中使用上下文，这种方式是违背MVVM原则的《ViewModel 绝不能引用视图、Lifecycle 或可能存储对 Activity 上下文的引用的任何类》
         * 但是既然google官方提供了AndroidViewModel，说明对上下问的引用做了优化，不会导致内存泄漏的问题，这里我们就简单用了，其实ViewModel中使用上下文的情况是非常少的，如果有顾虑可以直接继承ViewModel即可
         */
        viewModel = (VM) new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                try {
                    Constructor<T> cons = modelClass.getDeclaredConstructor(Application.class);
                    return cons.newInstance(getApplication());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).get(modelClass);
        //将ViewModel作为生命周期观测者，让其拥有View的生命周期感应.BaseViewModel实现了LifecycleObserver
        getLifecycle().addObserver(viewModel);
        /**3. UI中注册观察者，观察ViewModel中控制UI事件的LiveData的变化*/
        //加载对话框显示
        viewModel.getUIEvent().event_dialog_loading.observe(this, aBoolean -> {
            showProgressDialog();
        });
        //对话框消失
        viewModel.getUIEvent().event_dialog_dismiss.observe(this, v -> dismissProgressDialog());
        //打开界面
        viewModel.getUIEvent().event_startactivity.observe(this, map -> {
            ARouter.getInstance()
                    .build(map.keySet().iterator().next())
                    .withBundle("bundle", map.values().iterator().next())
                    .navigation();
        });
        //关闭界面
        viewModel.getUIEvent().event_finish.observe(this, activityResult -> {
            if (activityResult != null)
                setResult(activityResult.resultCode, activityResult.intent);
            finish();
        });

    }

    /**初始化TitleLayout*/
    @Override
    public void initTitleView() {
        //状态栏字体变黑色（透明状态栏）
        //XStatusBarUtil.darkMode(this);
        //全透明状态栏(状态栏字体默认白色)
        XStatusBarUtil.immersive(this);
        //设置导航栏颜色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && XBarUtils.isSupportNavBar()){
            XBarUtils.setNavBarVisibility(this, true);
            XBarUtils.setNavBarColor(this, Color.parseColor("#dddddd"));
        }
        titleLayout = findViewById(R.id.titleLayout);
        if(null!=titleLayout){
            //状态栏占位
            XStatusBarUtil.setPaddingSmart(this, titleLayout);
            //设置菜单点击事件
            titleLayout.setOnMenuClickListener((menu, view) -> onMenuClick(menu, view));
        }
    }

    /**
     * 标题栏菜单点击事件，其中返回键已做默认返回处理，如果子Activity需要处理其他标题菜单事件，请重写此方法
     * @param menu
     * @param view
     */
    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) {
        switch (menu) {
            case MENU_BACK:   //返回键（已做默认处理） ((Activity) getContext()).onBackPressed();
                break;
            case MENU_LEFT_TEXT:
                //左侧标题文字点击
                break;
            case MENU_CENTER:
                //中间标题文字点击
                break;
            case MENU_RIGHT_TEXT:
                //右侧标题文字点击
                break;
            case MENU_RIGHT_ICON:
                //右侧图标菜单点击
                break;
        }
    }

    public void showProgressDialog() {
        if (dialog == null)
            dialog = new TimeConsumDialog(this);
        if (!dialog.isShowing())
            dialog.show();
    }
    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInputFromWindow() {
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        dismissProgressDialog();
        //解除ViewModel生命周期感应
        getLifecycle().removeObserver(viewModel);
        if (binding != null)
            binding.unbind();
    }


}
