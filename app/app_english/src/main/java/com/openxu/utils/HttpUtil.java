package com.openxu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 请求网络工具类 1、上传错误日志MyUncaughtExceptionHandler.java中使用uploadError（仅此一处）
 * 2、其余地方调用直接调用doRequest（已存在子线程）或者创建AsyncHttpRequestTask对象 3、
 */
public class HttpUtil {
	private static String TAG = "HttpUtil";
	
	public static String doStringRequest(String urlStr) {
		InputStream inputStream = null;  
	    HttpURLConnection conn = null;  
	    InputStreamReader in = null;
		StringBuffer strBuffer = new StringBuffer();
        try {  
            URL url = new URL(urlStr);  
            if (url != null) {  
            	conn = (HttpURLConnection) url.openConnection();  
                // 设置连接网络的超时时间  
            	conn.setConnectTimeout(5000);  
            	conn.setDoInput(true);  
                // 表示设置本次http请求使用GET方式请求  
            	conn.setRequestMethod("GET");  
                int responseCode = conn.getResponseCode();  
                if (responseCode == 200) {
    				inputStream = conn.getInputStream();
    				in = new InputStreamReader(inputStream);
    				BufferedReader bufferedReader = new BufferedReader(in);
    				String line = null;
    				while ((line = bufferedReader.readLine()) != null) {
    					strBuffer.append(line);
    				}
    			}  
            }  
        } catch (Exception e) {  
        } finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return strBuffer.toString();
	}
	

}
