package com.openxu.core.http.rx;


import com.openxu.core.config.AppConfig;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


/**
 * Author: openXu
 * Time: 2019/4/30 12:50
 * class: RetryWhenReset
 * Description:
 *
 * retryWhen提供了重订阅的功能，重订阅触发需要两点要素：
 * ①、上游通知retryWhen本次订阅流已经完成，询问其是否需要重新订阅，该询问是以onError事件触发的
 * ②、retryWhen根据onError的类型，决定是否需要重新订阅，通过返回一个ObservableSource<?>来通知，
 *    如果这个ObservableSource返回onComplete/onError，那么不会触发重订阅；
 *    如果方法送onNext，那么会触发重订阅
 *
 * 实现retryWhen的关键在于如何定义它的Function参数：
 * ①、Function的输入是一个Observable<Throwable>，输出是一个泛型ObservableSource<?>。
 *     如果我们接受Observable<Throwable>发送的消息，那么就可以得到上游发送的错误类型，并根据该类型进行响应的处理。
 * ②、如果输出的Observable发送了onComplete/onError侧表示不需要重新订阅，结束整个流程；否则触发重订阅操作。
 *    也就是说，它仅仅作为一个是否需要触发重订阅的通知，onNext发送的是什么数据并不重要
 * 🌂、对于每次订阅的数据流Function函数只会回调一次，并且是在onError()的时候触发，他不会收到任何onNext事件
 * ④、在Function函数中，必须对输入的Observable<Throwable>进行处理，这里我们使用flatMap操作符
 *     接受上游的数据
 */
public class RetryWhenReset implements Function<Observable<Throwable>, ObservableSource<?>> {

    private int maxRetry; //重试次数
    private int delay;
    private int retryCount = 0;

    public RetryWhenReset(int maxRetry, int delay) {
        this.maxRetry = maxRetry;
        this.delay = delay;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                XLog.e("错误重试："+Thread.currentThread()+"     "+throwable);
                if(throwable instanceof SocketException && throwable.getMessage().contains("Connection reset")){
                    //由于java.net.SocketException: Connection reset的问题目前还没找到原因，但是知道重试4次即可，所以对该异常暂时进行4次重试
                    if (++retryCount <= maxRetry) {
                        XLog.w(retryCount+"  连接重置，重试："+throwable);
//                    return Observable.just(0);
                        if(AppConfig.DEBUG)
                            XToast.warning(retryCount+"连接重置，重试中...");
//                        saveCrashInfo2File(retryCount, throwable);
                        //retryWhen() 输出的Observable发送onNext，那么会触发重订阅。也就是说，它 仅仅是作为一个是否要触发重订阅的通知，onNext发送的是什么数据并不重要
                        return Observable.timer(delay, TimeUnit.MILLISECONDS);
                    } else {
                        XLog.e(retryCount+"  连接重置，重试超过 "+maxRetry+" 次，取消订阅："+throwable);
                        if(AppConfig.DEBUG)
                            XToast.warning(retryCount+"连接重置，重试失败...");
//                        saveCrashInfo2File(retryCount, throwable);
                        //retryWhen() 输出的Observable发送了onComplete或者onError则表示不需要重订阅，结束整个流程
                        return Observable.error(throwable);
                    }
                }else{
                    //其他异常不做重试
                    XLog.e("请求发生异常，不重试："+throwable);
                    return Observable.error(throwable);
                }
            }
        });
    }



}