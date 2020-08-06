package com.openxu.core.utils;

import android.text.TextUtils;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Author: lbing
 * Time:   2019/4/9 14:59
 * class:  FLayoutConvertUtils
 * Description:  布局转化工具，用于databinglayout的的数据转换
 * Update:
 */
public class XLayoutConvertUtils {

    public static boolean changeStatus(String status) {
        if ("2".equals(status)) {
            return true;
        }
        return false;
    }

    public static int changeVisible(String isBind) {
        return (Integer.parseInt(TextUtils.isEmpty(isBind) ? "0" : isBind)) == 1 ? View.VISIBLE : View.GONE;
    }

    /**
     * 转换为百分比
     * @param pre
     * @param total
     * @return
     */
    public static String toPercentage(String pre, String total) {
        XLog.w("转换为百分比:"+pre+"    "+total);
        String percentage = "0.0";
        if(!"0".equals(total)){
            try {
                DecimalFormat decimalFormat = new DecimalFormat(percentage);
                float a = Integer.parseInt(pre) * 100.0f / Integer.parseInt(total) * 1.0f;
                XLog.w("转换为百分比:" + a);    //NaN  total=0
                percentage = decimalFormat.format(a);
                XLog.w("转换为百分比:" + percentage);
            }catch (Exception e){
                XLog.e(e);
            }
        }
        return percentage +"%";
    }
    public static String passOrUnpass(String auditExplain) {
        return "0".equals(auditExplain)?"通过":"未通过";
    }


}
