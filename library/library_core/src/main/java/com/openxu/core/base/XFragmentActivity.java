package com.openxu.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.R;
import com.openxu.core.databinding.CoreFragmetnActivityBinding;

/**
 * Author: openXu
 * Time: 2019/4/10 12:39
 * class: FragmentActivity
 * Description:  Fragment 容器页面
 * <p>
 * 使用：
 * FragmentActivity.start(this, ARouter.getInstance().build(RouterPath.Common.PAGE_SET_FEEDBACK)
 * .withBoolean("data1", true)
 * .withParcelable("data2", new UserPermission()))
 * <p>
 * Update:
 */
@SuppressWarnings({"UnusedParameters", "unused"})
public class XFragmentActivity extends XBaseActivity<CoreFragmetnActivityBinding, XBaseViewModel> {

    private XBaseFragment mFragment;


    public static void start(Activity activity, Postcard postcard) {
        activity.startActivity(getIntent(activity, postcard));
    }

    public static void start(Activity activity, Postcard postcard, int requestCode) {
        activity.startActivityForResult(getIntent(activity, postcard), requestCode);
    }

    public static void start(Fragment fragment, Postcard postcard) {
        fragment.startActivity(getIntent(fragment.getContext(), postcard));
    }

    public static void start(Fragment fragment, Postcard postcard, int requestCode) {
        fragment.startActivityForResult(getIntent(fragment.getContext(), postcard), requestCode);
    }

    private static Intent getIntent(Context context, Postcard postcard) {
        Intent intent = new Intent(context, XFragmentActivity.class);
        intent.putExtra("Path", postcard.getPath());
        intent.putExtra("Bundle", postcard.getExtras());
        return intent;
    }

    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.core_fragmetn_activity;
    }

    @Override
    public void initView() {
        String routerPath = getIntent().getStringExtra("Path");
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        try {
            mFragment = (XBaseFragment) ARouter.getInstance().build(routerPath)
                    .with(bundle).navigation();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentActivityContent, mFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }

    public XBaseFragment getFragment() {
        return mFragment;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        Log.e("TAG", "FragmentActivity返回数据===");
     /*   if (null != mFragment){
            mFragment.onActivityResult(reqCode, resCode, data);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (null != mFragment) {
            if (!mFragment.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
