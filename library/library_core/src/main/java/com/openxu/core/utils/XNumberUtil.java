package com.openxu.core.utils;

import java.text.DecimalFormat;

/**
 * Author: openXu
 * Time: 2020/8/11 10:55
 * class: XNumberUtil
 * Description:
 */
public class XNumberUtil {

    public static String getFloatStr(float a, float b) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
        float pro = (a * 100.0f) / (b * 1.0f);
        if (pro == 0)
            return "0 %";
        String str = decimalFormat.format(pro);
        if (str.endsWith("0")) {
            str = str.substring(0, str.lastIndexOf("0"));
            if (str.endsWith("0")) {
                str = str.substring(0, str.lastIndexOf("0"));
            }
            if (str.endsWith(".")) {
                str = str.substring(0, str.lastIndexOf("."));
            }
        }
        return str + " %";
    }

}
