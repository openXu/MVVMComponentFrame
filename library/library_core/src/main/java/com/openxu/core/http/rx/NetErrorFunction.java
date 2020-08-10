package com.openxu.core.http.rx;


import com.openxu.core.http.data.XResponse;
import com.openxu.core.http.error.NetError;
import com.openxu.core.http.error.NetErrorHandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


/**
 * Author: openXu
 * Time: 2019/3/4 16:19
 * class: NetErrorFunction
 * Description: 错误处理Function
 */
public class NetErrorFunction<T> implements Function<Throwable, ObservableSource<T>> {
    @Override
    public ObservableSource<T> apply(Throwable throwable) throws Exception {
        NetError error = NetErrorHandle.handleError(throwable);
        return Observable.error(error);
    }
}
