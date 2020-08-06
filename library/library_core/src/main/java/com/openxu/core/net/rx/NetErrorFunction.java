package com.openxu.core.net.rx;


import com.openxu.core.net.data.XResponse;
import com.openxu.core.net.error.NetError;
import com.openxu.core.net.error.NetErrorHandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: openXu
 * Time: 2019/3/4 16:19
 * class: NetErrorFunction
 * Description: 错误处理Function
 */
public class NetErrorFunction implements Function<Throwable, ObservableSource<XResponse>> {
    @Override
    public ObservableSource<XResponse> apply(Throwable throwable) throws Exception {
        NetError error = NetErrorHandle.handleError(throwable);
        return Observable.error(error);
    }
}
