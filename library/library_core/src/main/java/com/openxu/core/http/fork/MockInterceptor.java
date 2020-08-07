package com.openxu.core.http.fork;


import com.openxu.core.utils.XLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MockInterceptor implements Interceptor {
    Parrot parrot;
    public MockInterceptor(Parrot parrot) {
        this.parrot = parrot;
    }
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //模拟服务器返回数据
        Response response = parrot.mockResult(request);
        if (response != null) {
            XLog.w("模拟服务器返回数据："+response);
            return response;
        }
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
        }
        return response;
    }
}


