package com.openxu.core.net.https;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author: lbing
 * @CreateDate: 2020/4/16 14:40
 * @Description:
 * @UpdateRemark:
 */
public class X509TrustManager implements javax.net.ssl.X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

//    返回信任的证书
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
