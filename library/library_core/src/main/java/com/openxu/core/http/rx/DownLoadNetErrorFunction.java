package com.openxu.core.http.rx;


import com.openxu.core.http.data.FpcDownloadData;
import com.openxu.core.http.error.NetErrorHandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Author: openXu
 * Time: 2019/3/4 16:19
 * class: DownLoadNetErrorFunction
 * Description: 文件下载错误处理Function
 */
public class DownLoadNetErrorFunction implements Function<Throwable, ObservableSource<FpcDownloadData>> {
    @Override
    public ObservableSource<FpcDownloadData> apply(Throwable throwable) throws Exception {
//        NetError error = NetErrorHandle.handleError(throwable);
        Throwable error = NetErrorHandle.handleError(throwable);
        return Observable.error(error);
    }
}
