package com.openxu.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import androidx.annotation.IntDef;
import com.openxu.mvvm.BuildConfig;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NetworkManager {


    private static Context context;
    public static String address;
    public static int port;
    private static NetworkManager INSTANCE;

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    private NetworkManager() {
    }

    public static String getBaseUrl() {
        return "http://" + address + ":" + port + "/";
    }

    @IntDef({Method.GET, Method.POST, Method.DOWNLOAD, Method.UPLOAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Methods {
    }

    public static class Method {
        public static final int GET = 1;
        public static final int POST = 2;
        public static final int DOWNLOAD = 3;
        public static final int UPLOAD = 4;
    }

    public RequstBuilder newBuilder() {
        return new RequstBuilder();
    }

    public class RequstBuilder {
        public int method;   //请求方式
        public String url;    //url
        public BaseCallback callback;
        public boolean showDialog = true;          //是否显示对话框
        public boolean postSuccessToast = true;    //post提交成功后是否Toast默认提示（ParseDataFunction提示：提交成功）
        public Map<String, String> params = new HashMap<>();   //参数
        //post带实体对象
        public Object object;

        public RequstBuilder method(@Methods int method) {
            this.method = method;
            return this;
        }
        public RequstBuilder url(String url) {
            this.url = url;
            return this;
        }
        public RequstBuilder showDialog(boolean showDialog) {
            this.showDialog = showDialog;
            return this;
        }
        public RequstBuilder postSuccessToast(boolean postSuccessToast) {
            this.postSuccessToast = postSuccessToast;
            return this;
        }
        public RequstBuilder putParam(String key, String value) {
            params.put(key, value);
            return this;
        }
        public RequstBuilder putParams(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }
        public RequstBuilder object(Object object) {
            this.object = object;
            return this;
        }
        public void build(BaseCallback callback) {
        }
    }

    /**
     * 普通get请求
     *
     * @param builder
     */

   private void doGet(RequstBuilder builder) {

    }

    private void doPost(RequstBuilder builder, Object object) {
    }

}
