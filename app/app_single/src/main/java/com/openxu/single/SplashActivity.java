package com.openxu.single;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.Application;
import android.app.ContextImpl;
import android.app.Instrumentation;
import android.app.LoadedApk;
import android.app.ResourcesManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ResourcesImpl;
import android.content.res.ResourcesKey;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.Trace;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.Window;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.android.internal.util.ArrayUtils;
import com.google.android.gles_jni.EGLContextImpl;
import com.openxu.chaxun.RouterPathChaxun;
import com.openxu.core.base.XBaseActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.base.XFragmentActivity;
import com.openxu.core.config.AppConfig;
import com.openxu.core.dialog.DialogDef;
import com.openxu.core.utils.XFileUtils;
import com.openxu.core.utils.XFiles;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XNumberUtil;
import com.openxu.core.utils.permission.PermissionCallBack;
import com.openxu.core.utils.permission.PermissionUtile;
import com.openxu.single.databinding.ActivitySplashBinding;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.servertransaction.ActivityLifecycleItem.ON_CREATE;

/**
 * Author: openXu
 * Time: 2020/8/7 9:20
 * class: SplashActivity
 * Description:
 */
public class SplashActivity extends XBaseActivity<ActivitySplashBinding, XBaseViewModel> {
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }
    @SuppressLint("AutoDispose")


    /***************************************权限相关*********************************************/
    protected String[] permissions = new String[]{};

    /**
     * 如果页面需要相关权限，请调用此方法申请，并覆盖onPermissionGranted()执行权限通过后的逻辑
     */
    protected void requestPermission(String... permissions) {
        this.permissions = permissions;
        PermissionUtile.requestPermission(this, new PermissionCallBack() {
            @Override
            public void onGranted() {
                onPermissionGranted();
            }

            @Override
            public void onDenied() {
                finish();
            }
        }, permissions);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode) {
            case PermissionUtile.PERMISSION_REQUEST_CODE: {
                if (AndPermission.hasPermissions(this, permissions)) {
                    // 有对应的权限
                    XLog.i("有对应的权限");
                    onPermissionGranted();
                } else {
                    // 没有对应的权限
                    XLog.e("没有对应的权限，继续申请");
                    requestPermission(permissions);
                }
                break;
            }
        }
    }



    /**
     * 权限通过后调用
     */
    @SuppressLint("AutoDispose")
    protected void onPermissionGranted() {
       /* XFiles.initFileDir();
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String destPath = "/data/data/" + mContext.getPackageName()+ "/databases";
            File dir = new File(destPath);
            if (!dir.exists())
                dir.mkdir();
            AssetManager am = mContext.getResources().getAssets();
            int conut = 0;
            int all = 10619880;
            destPath += ( File.separator + "openword.db");
            File file = new File(destPath);
            file.deleteOnExit();
            FileOutputStream out = new FileOutputStream(destPath);
            InputStream in = am.open("openword.db");
            XLog.e("从"+in.available()+"  ->>  "+destPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
                conut += 1024;
            }
            // 最后关闭就可以了
            out.flush();
            in.close();
            out.close();
            emitter.onComplete();
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(String s) {
                        XLog.e("拷贝数据库"+s);
                    }
                    @Override
                    public void onError(Throwable e) {
                        XLog.e("错误"+e);
                    }
                    @Override
                    public void onComplete() {
                        XFragmentActivity.start(SplashActivity.this, ARouter.getInstance().build(RouterPathChaxun.PAGE_FRAGMENT_CHAXUN));
                    }
                });*/
    }


    /***************************************权限相关*********************************************/


    @Override
    public void initView() {
        requestPermission(
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE
        );

    }
    @Override
    public void registObserve() {
    }
    @Override
    public void initData() {
    }

}
