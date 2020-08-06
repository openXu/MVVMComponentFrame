package com.openxu.core.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;


/**
 * autour : openXu
 * date : 2016/7/24 14:12
 * className : FontUtil
 * version : 1.0
 * description : 文字相关处理帮助类(自定义控件专用)
 */
public class XFontUtil {
    /**
     * @param paint
     * @param str
     * @return 返回指定笔和指定字符串的长度
     * @add yujiangtao 16/8/5
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     * @add yujiangtao 16/8/5
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    //    去掉时分秒
    public static String getSimpleTime(String time) {
        String temp = time;
        try {
            if (!TextUtils.isEmpty(time)) {
                String[] s = time.split(" ");
                temp = s[0];
            }
        } catch (Exception e) {
        }
        return temp;
    }

    /**
     * @return 返回指定笔离文字顶部的基准距离
     * @add yujiangtao 16/8/5
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }


    /**
     * 获取标签和值组成的SpannableString对象
     *
     * @param lable 标签字符串，将包裹为浅灰色
     * @param value 值字符串，将包裹为深灰色
     * @return
     */
    public static SpannableString getLableValueSpan(String lable, String value) {
//        lable = TextUtils.isEmpty(lable)?"":lable;
//        value = TextUtils.isEmpty(value)?"":value;
//        String space = "   ";
//        SpannableString spanString = new SpannableString(lable+space+value);
//        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#939393"));
//        spanString.setSpan(span, 0, lable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        span = new ForegroundColorSpan(Color.parseColor("#5E5E5E"));
//        spanString.setSpan(span, lable.length()+space.length()-1, lable.length()+space.length()+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spanString;
        return getSpecifyLableValueSpan(lable, value, Color.parseColor("#939393"), Color.parseColor("#5E5E5E"));

    }

    public static SpannableString getSpecifyLableValueSpan(String lable, String value, int preColor, int afterColor) {
        lable = TextUtils.isEmpty(lable) ? "" : lable;
        value = TextUtils.isEmpty(value) ? "" : value;
        String space = "   ";
        SpannableString spanString = new SpannableString(lable + space + value);
        ForegroundColorSpan span = new ForegroundColorSpan(preColor);
        spanString.setSpan(span, 0, lable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span = new ForegroundColorSpan(afterColor);
        spanString.setSpan(span, lable.length() + space.length() - 1, lable.length() + space.length() + value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getSpecifyLableValueSpan(String lable, String value, int preTextSize, int afterTextSize, int preColor, int afterColor) {
        lable = TextUtils.isEmpty(lable) ? "" : lable;
        value = TextUtils.isEmpty(value) ? "" : value;
        String space = "   ";
        SpannableString spanString = new SpannableString(lable + space + value);
        ForegroundColorSpan span = new ForegroundColorSpan(preColor);
        spanString.setSpan(span, 0, lable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span = new ForegroundColorSpan(afterColor);
        spanString.setSpan(span, lable.length() + space.length() - 1, lable.length() + space.length() + value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体大小（绝对值,单位：像素）,第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素
        spanString.setSpan(new AbsoluteSizeSpan(preTextSize), 0, lable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new AbsoluteSizeSpan(afterTextSize), lable.length() + space.length() - 1, lable.length() + space.length() + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanString;
    }
}
