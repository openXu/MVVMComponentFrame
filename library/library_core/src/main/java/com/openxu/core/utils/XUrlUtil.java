package com.openxu.core.utils;

import android.text.TextUtils;


/**
 * autour : lbing
 * className : FontUtil
 * version : 1.0
 * description : url格式字符串相关工具
 */
public class XUrlUtil {
    /**
     * 将服务器返回的url格式中转义字符还原为真实字符
     */
    public static String changeCharacter(String url){
        if(TextUtils.isEmpty(url))
            return url;
        if(url.contains("&amp;")){
            url = url.replaceAll("&amp;", "&");
        }
        return url;
    }
}


