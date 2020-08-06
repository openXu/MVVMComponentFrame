package com.openxu.core.net.interceptor;

import androidx.annotation.NonNull;


import com.openxu.core.utils.XLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: openXu
 * Time: 2019/2/26 14:47
 * class: RetryInterceptor
 * Description: http重试拦截
 */
public class RetryInterceptor implements Interceptor {
    public int maxRetry;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum ++;
            XLog.e("http失败，进行第"+retryNum+"次重试");
            response = chain.proceed(request);
        }
        return response;
    }
}
