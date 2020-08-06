package com.openxu.core.net.https;

import com.openxu.core.utils.XLog;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @Author: lbing
 * @CreateDate: 2020/4/16 14:36
 * @Description:
 * @UpdateRemark:
 */
public class UnSafeHostnameVerifier implements HostnameVerifier {

//    给okhttp自定义验证规则
    @Override
    public boolean verify(String hostname, SSLSession session) {
        XLog.e("hostname:"+hostname);
        return true;//通过所有验证（正式环境勿这样使用
    }
}