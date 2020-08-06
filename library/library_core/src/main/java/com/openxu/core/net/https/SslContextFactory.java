package com.openxu.core.net.https;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * @Author: lbing
 * @CreateDate: 2020/4/16 14:34
 * @Description:
 * @UpdateRemark:
 */
public class SslContextFactory {

    /**
     * 存储客户端自己的密钥
     */
    private final static String CLIENT_PRI_KEY = "client.bks";

    /**
     * 存储服务器的公钥
     */
    private final static String TRUSTSTORE_PUB_KEY = "publickey.bks";

    /**
     * 读取密码
     */
    private final static String CLIENT_BKS_PASSWORD = "123321";
    /**
     * 读取密码
     */
    private final static String PUCBLICKEY_BKS_PASSWORD = "123321";

    private final static String KEYSTORE_TYPE = "BKS";

    private final static String PROTOCOL_TYPE = "TLS";

    private final static String CERTIFICATE_STANDARD = "X509";


    public static SSLSocketFactory getSSLCertifcation(Context context) {

        SSLSocketFactory sslSocketFactory = null;

        try {
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);

            //读取证书,证书存放在assets中，或是存放在raw
            InputStream ksIn = context.getAssets().open(CLIENT_PRI_KEY);
            InputStream tsIn = context.getAssets().open(TRUSTSTORE_PUB_KEY);

            //加载证书
            keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
            trustStore.load(tsIn, PUCBLICKEY_BKS_PASSWORD.toCharArray());

            //关闭流
            ksIn.close();
            tsIn.close();

            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
            //取得TrustManagerFactory的X509密钥管理器实例
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_STANDARD);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_STANDARD);

            //初始化密钥管理器
            keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());
            trustManagerFactory.init(trustStore);

            //初始化SSLContext，进行双向验证
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }
}
