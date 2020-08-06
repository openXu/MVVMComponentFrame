package com.openxu.core.utils;

import java.util.List;

/**
 * Company: SyberOS BeiJing
 * Project: Device Inspection
 * Created by 陈冬 on 16/5/16.
 */
public class XStringUtils {
    /**
     * list<String>解析成string，中间用分隔符“separator”隔开
     *
     * @param strings   list
     * @param separator 分隔符
     * @return
     */
    static public String toString(List<String> strings, String separator) {
        if (strings.size() <= 0) {
            return "";
        }
        String ret = "";
        for (String str : strings) {
            ret += str;
            ret += separator;
        }
        ret = ret.substring(0, ret.lastIndexOf(separator));
        return ret;
    }

}
