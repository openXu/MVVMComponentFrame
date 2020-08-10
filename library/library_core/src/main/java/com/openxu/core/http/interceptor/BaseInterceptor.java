package com.openxu.core.http.interceptor;

import android.os.Environment;
import android.util.Log;


import com.openxu.core.config.AppConfig;
import com.openxu.core.utils.XFiles;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.core.http.NetworkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: openXu
 * Time: 2019/2/26 14:47
 * class: BaseInterceptor
 * Description: 基础拦截器，设置URL
 */
public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;

    //apk下载url特殊标记
    public static final String fileDownloadUrl = "Common/FileStream.aspx?src=/uploadfiles/";
    public static final String fileUploadUrl = "WebApi/Upload/Post?SerialKey=";

    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        //根据请求类型改变url
        //http://114.115.144.251:8001/User_QueryPhonePrincipal?UserCode=nj&ApplicationID=4ccfccaf-9da0-11e7-840e-fa163ea287f1
        String url = request.url().toString();
//        XLog.i("baseUrl:"+ AppConfig.baseUrl);
//        XLog.i("请求数据Url:"+url);
        /*if (request.method().equals("GET")) {
            if(url.contains(fileDownloadUrl)){
                XLog.w("get请求为文件下载");
                //文件下载
                //http://dev.fpc119.com:8001/Common/FileStream.aspx?src=/uploadfiles/app/97759746492403888/soft/wbservice_201812131700_1.0.3v4_debug.apk&fileName=wbservice_201812131700_1.0.3v4_debug.apk&isDelete=0
            }else if(!url.contains("WebApi")){
                //普通get请求
                //http://114.115.144.251:8001/WebApi/DataExchange/GetData/User_QueryPhonePrincipal?UserCode=nj&ApplicationID=4ccfccaf-9da0-11e7-840e-fa163ea287f1
                url = url.replace(NetworkManager.getBaseUrl(), NetworkManager.getBaseUrl()+"WebApi/DataExchange/GetData/");
            }//剩余的就是配置全路径的url
        } else if(request.method().equals("POST")){

            if(url.contains(fileUploadUrl)){
                XLog.w("post请求为附件上传");
                //上传
            }else if(!url.contains("WebApi")){
                //普通get请求
                //http://114.115.144.251:8001/WebApi/DataExchange/SendData/User_QueryPhonePrincipal?UserCode=nj&ApplicationID=4ccfccaf-9da0-11e7-840e-fa163ea287f1
                url = url.replace(NetworkManager.getBaseUrl(), NetworkManager.getBaseUrl()+"WebApi/DataExchange/SendData/");
            }//剩余的就是配置全路径的url

        }*/
        request = builder.url(url).build();
        //添加公共参数
        HttpUrl newUrl = request.url().newBuilder()
//                .addEncodedQueryParameter("dataKey", "00-00-00-00")
                .build();
        builder.url(newUrl);

//        saveUrl2File(newUrl);
        return chain.proceed(builder.build());
    }

    private void saveUrl2File(HttpUrl newUrl){
        StringBuffer sb = new StringBuffer();
        sb.append( "\n" +new SimpleDateFormat("MM-dd HH:mm:ss:SSSS  ", Locale.CHINA).format(new Date()));
        sb.append(newUrl.toString() + "\n");
        XLog.w("写入请求："+newUrl.toString());
        try {
            String fileName = "AHttpUrl-Record.log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = XFiles.getErrorPath() + File.separator;
                File file = new File(path + fileName);
                FileOutputStream fos;
                if(!file.exists()){
                    file.createNewFile();//如果文件不存在，就创建该文件
                    fos = new FileOutputStream(file);//首次写入获取
                }else{
                    //如果文件已存在，那么就在文件末尾追加写入
                    fos = new FileOutputStream(file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                }
                fos.write(sb.toString().getBytes());
                fos.close();
            }
        } catch (Exception e) {
            XToast.error("写入请求接口文件失败："+e.getMessage());
        }
    }
}
