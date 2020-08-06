package com.openxu.core.binding;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.core.view.TitleLayout;

import java.util.List;

/**
 * Author: openXu
 * Time: 2020/6/3 16:57
 * class: TextViewBindingAdapter
 * Description:
 *
 * BindingAdapter负责将xml布局绑定表达式@{}的值设置给View的对应属性。比如`android:text="@{user.name}"`，其实就是适配器调用TextView.setText()将user.name的值设置进去
 *
 * DataBinding框架有一系列自带的适配器，自带的预定义适配器可在androidx.databinding:databinding-adapters-3.2.1库的androidx.databinding.adapters包下查看
 *  ★注意：只有定义了适配器的属性才能在布局中使用@{}绑定表达式；
 *         同理如果想适配器方法被调用，布局中绑定值必须使用@{}表达式
 * DataBinding框架允许我们自己定义适配器，自定义的适配器会被自动检测并覆盖自带预定义适配器，不用做特殊设置
 */
public class XTextViewBindingAdapter {
    /**
     * 1. 使用BindingAdapter注解标记方法为适配器方法，方法名和类名随便写
     * 参数类型非常重要。第一个参数用于确定与特性关联的视图类型，第二个参数用于确定在给定特性的绑定表达式中接受的类型。
     * @param view 固定的，为被绑定的视图对象
     * @param url 绑定表达式中接受的类型，比如android:src="@{user.imageUrl}"中user.imageUrl为String类型
     *
     * 注意：方法必须是static的，返回值类型不做限制，参数必须由 视图 + @{值}值类型 组成
     */
    @BindingAdapter("android:src")
    public static String setImage(ImageView view, String url) {
        XLog.v("适配器拦截setImage："+"     "+url);
        //拦截android:src属性的数据绑定，使用Glide绑定图片
        Glide.with(view.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(0).centerCrop())
                .into(view);
        return "";
    }

    /**
     * 2. 标记多个属性特性的适配器方法
     *    value 接受字符串数组
     *    requireAll表示是否必须同时满足
     *              true : 布局中同时设置value中的属性才会调用适配器
     *              false : 设置了任意属性时调用适配器，未设置的属性实参为默认值（String 为null, int为0...）
     * @param view 固定的，为被绑定的视图对象
     * @param color 绑定表达式中接受的类型
     * @param size 绑定表达式中接受的类型
     */
    @BindingAdapter(value={"android:textColor", "android:textSize"}, requireAll = false)
    public static void setTextCS(TextView view, int color, int size) {
        XLog.v("适配器拦截："+"     "+color + "       "+size);
        view.setTextColor(color);
        view.setTextSize(size);
    }



}
