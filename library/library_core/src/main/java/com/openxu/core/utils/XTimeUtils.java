package com.openxu.core.utils;


import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: openXu
 * Time: 2019/3/14 11:31
 * class: FTimeUtils
 * Description: 时间工具
 */
public final class XTimeUtils {
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm:ss";

    public static String date2Str(Date date, String format) {
        SimpleDateFormat dataFormat = null;
        if (TextUtils.isEmpty(format))
            dataFormat = new SimpleDateFormat(DATE_TIME);
        else
            dataFormat = new SimpleDateFormat(format);
        if (date == null)
            return "";
        return dataFormat.format(date);
    }

    public static Date str2Date(String str, String format) {
        SimpleDateFormat dataFormat = null;
        if (TextUtils.isEmpty(format))
            dataFormat = new SimpleDateFormat(DATE_TIME);
        else
            dataFormat = new SimpleDateFormat(format);
        try {
            return dataFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBeforDate(String dateStr, int witch, int num, String format) {
        Calendar c = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat(format).parse(dateStr);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int old = 0;
        switch (witch) {
            case Calendar.YEAR:
                old = c.get(Calendar.YEAR);
                break;
            case Calendar.MONTH:
                old = c.get(Calendar.MONTH);
                break;
            case Calendar.DATE:
                old = c.get(Calendar.DATE);
                break;
            default:
                break;
        }
        c.set(witch, old - num);
        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }

    /**
     * 根据毫秒值，获取时间00:00:00
     *
     * @param ms
     * @return
     */
    public static String getTimeByMs(int ms) {
        String time;
        int h = ms / 1000 / 60 / 60;
        int f = (ms - h * 60 * 60 * 1000) / 1000 / 60;
        int s = (ms - h * 60 * 60 * 1000 - f * 1000 * 60) / 1000;
        if (h > 0)
            time = h >= 10 ? (h + ":") : ("0" + h + ":");
        else
            time = "00:";
        if (f > 0)
            time += (f >= 10 ? (f + ":") : ("0" + f + ":"));
        else
            time += "00:";
        if (s > 0)
            time += (s >= 10 ? (s + "") : ("0" + s));
        else
            time += "00";
        return time;
    }


    //当前时间所在月起始时间  如:2017-12-01 00:00:00
    public static String getMonthStartTimeByCurrentTime(String time, String formatStr) {
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        if (!TextUtils.isEmpty(time)) {
            Date date = null;
            try {
                date = new SimpleDateFormat(formatStr).parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.setTime(date);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();

        XLog.e("当前月第1天===" + dateFormater.format(cal.getTime()));
        return dateFormater.format(cal.getTime());
    }


    //获取上下某个月的开始时间时间 X表示前几个月
    public static String getLastXMonthStartTimeByCurrentTime(String formatStr, int X) {
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        cal.add(Calendar.MONTH, X);
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        return dateFormater.format(cal.getTime());
    }
    //获取上下某个月的结束时间 X表示前几个月
    public static String getLastXMonthEndTimeByCurrentTime( String formatStr,int X) {
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        cal.add(Calendar.MONTH, X);
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormater.format(cal.getTime());
    }

    //当前时间所在月结束时间  如:2017-12-01 00:00:00
    public static String getMonthEndTimeByCurrentTime(String time, String formatStr) {
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        if (!TextUtils.isEmpty(time)) {
            Date date = null;
            try {
                date = new SimpleDateFormat(formatStr).parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.setTime(date);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        XLog.e("当前月最后1天===" + dateFormater.format(cal.getTime()));
        return dateFormater.format(cal.getTime());
    }

    //当前时间所在周起始时间  如:2017-12-04 00:00:00
    public static String getWeekStartTimeByCurrentTime(String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 1);
        XLog.e("当前得到本周一的日期*******" + simpleDateFormat.format(cal.getTime()));
        return simpleDateFormat.format(cal.getTime());
    }

    //当前时间所在周结束时间  如:2017-12-10 23:59:59
    public static String getWeekEndTimeByCurrentTime(String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 7);
        XLog.e("当前得到本周天的日期*******" + simpleDateFormat.format(cal.getTime()));
        return simpleDateFormat.format(cal.getTime());
    }
//    产生单位为秒的时间戳
    public static long generateTimeStamp(String timeString, String type) {
        SimpleDateFormat sdr = new SimpleDateFormat(type,
                Locale.CHINA);
        Date date;
        long times = 0;
        try {
            date = sdr.parse(timeString);
            times = date.getTime()/1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
//    产生时间戳
    public static long generateTimeStamp2(String timeString, String type) {
        SimpleDateFormat sdr = new SimpleDateFormat(type,
                Locale.CHINA);
        Date date;
        long times = 0;
        try {
            date = sdr.parse(timeString);
            times = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCuDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }


    @SuppressLint("SimpleDateFormat")
    public static String getCuMonth(String time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        if (!TextUtils.isEmpty(time)) {

            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            date = new Date();
        }

        return format.format(date);
    }



}