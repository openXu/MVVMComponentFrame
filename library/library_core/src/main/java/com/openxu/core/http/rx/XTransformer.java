package com.openxu.core.http.rx;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: openXu
 * Time: 2019/3/4 16:19
 * class: BaseFunction
 * Description:
 * ObservableTransformer（转换器）	对上游Observable应用一个函数，并返回带有可选不同元素类型的ObservableSource
 */
public class XTransformer  {

    public static ObservableTransformer baseTransformer(ParseDataFunction function) {
        return new ObservableTransformer() {
            //将上游被观察者进行转换
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(function)
                        .retryWhen(new RetryWhenReset(4, 100))
                        .onErrorResumeNext(new NetErrorFunction());
            }
        };
    }

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            //将上游被观察者进行转换
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer parseDataTransformer(ParseDataFunction function) {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.map(function);
            }
        };
    }
    public static ObservableTransformer retryTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.retryWhen(new RetryWhenReset(4, 100));
            }
        };
    }
    public static ObservableTransformer exceptionTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.onErrorResumeNext(new NetErrorFunction());
            }
        };
    }


}
