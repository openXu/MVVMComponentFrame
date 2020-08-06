package com.openxu.core.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hwangjr.rxbus.RxBus;
import com.openxu.core.R;
import com.openxu.core.dialog.TimeConsumDialog;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XStatusBarUtil;
import com.openxu.core.view.TitleLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Author: openXu
 * Time: 2019/2/28 16:24
 * class: XBaseFragment
 * Description:
 */
public abstract class XBaseFragment<V extends ViewDataBinding, VM extends XBaseViewModel> extends Fragment implements IBaseFragment {

    protected V binding;
    protected VM viewModel;
    protected TitleLayout titleLayout;
    protected static String catchDataHint = "巡检数据已保存";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        ARouter.getInstance().inject(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initMVVM(inflater, container, savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleView();
        initView();
        registObserve();
        initData();
    }

    private void initMVVM(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**1. 注入DataBinding*/
        binding = DataBindingUtil.inflate(inflater,
                getContentView(inflater, container, savedInstanceState), container, false);
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
         */
//        viewModel = (VM) new ViewModelProvider(this).get(modelClass);
        viewModel = (VM) new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                try {
                    Constructor<T> cons = modelClass.getDeclaredConstructor(Application.class);
                    return cons.newInstance(getContext().getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).get(modelClass);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        /**3. UI中注册观察者，观察ViewModel中控制UI事件的LiveData的变化*/
        //加载对话框显示
        viewModel.getUIEvent().event_dialog_loading.observe(this, aBoolean -> {
            showProgressDialog();
        });
        //对话框消失
        viewModel.getUIEvent().event_dialog_dismiss.observe(this, v -> {
            dismissProgressDialog();
        });
        //打开界面
        viewModel.getUIEvent().event_startactivity.observe(this, map -> {
            ARouter.getInstance()
                    .build(map.keySet().iterator().next())
                    .withBundle("bundle", map.values().iterator().next())
                    .navigation();
        });
        //关闭界面
        viewModel.getUIEvent().event_finish.observe(this, activityResult -> {
            if (activityResult != null) {
                finish(activityResult.resultCode, activityResult.intent);
            } else {
                finish();
            }
        });
    }


    @Override
    public void initTitleView() {
        titleLayout = binding.getRoot().findViewById(R.id.titleLayout);
        if (null != titleLayout) {
            XStatusBarUtil.setPaddingSmart(getContext(), titleLayout);
            titleLayout.setOnMenuClickListener((menu, view) -> {
                onMenuClick(menu, view);
            });
        }
    }

    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) {
    }



    public void finish() {
        getActivity().finish();
    }

    public void finish(int resultCode, Intent intent) {
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }

    public void showProgressDialog() {
        XLog.e("3、显示dialog");
        if (getActivity() instanceof XBaseActivity) {
            ((XBaseActivity) getActivity()).showProgressDialog();
        }
    }

    public void dismissProgressDialog() {
        XLog.e("3、隐藏  dialog");
        if (getActivity() instanceof XBaseActivity) {
            ((XBaseActivity) getActivity()).dismissProgressDialog();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        //解除ViewModel生命周期感应
        getLifecycle().removeObserver(viewModel);
        if (binding != null) {
            binding.unbind();
        }
    }
}
