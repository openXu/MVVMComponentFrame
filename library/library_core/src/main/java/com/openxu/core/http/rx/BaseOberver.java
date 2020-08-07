package com.openxu.core.http.rx;


import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Author: openXu
 * Time: 2019/3/4 17:18
 * class: BaseOberver
 * Description:
 */
public abstract class BaseOberver<T> implements Observer<T> {

    private XBaseViewModel viewModel;

    protected boolean showDialog;        //请求数据时是否显示dialog

    private Disposable disposable;

    public BaseOberver(XBaseViewModel viewModel, boolean showDialog){
        this.viewModel = viewModel;
        this.showDialog = showDialog;
    }
    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
     /*   if(null!=viewModel&&viewModel.mDisposable!=null) {
            viewModel.mDisposable.add(disposable);
            if (showDialog) {
                viewModel.showDialog();
            }
        }*/
    }
    @Override
    public void onError(Throwable e) {
        finish();
    }
    @Override
    public void onComplete() {
        finish();
    }

    private void finish(){
        XLog.w("------小  取消订阅");
//        disposable.dispose();
        /*if(null!=viewModel&&viewModel.mDisposable!=null) {
            viewModel.mDisposable.remove(disposable);
            if(viewModel.mDisposable.size()<=0 && showDialog){   //当前页面没有其他正在进行的请求时
                viewModel.dismissDialog();
            }
        }*/
    }

}
