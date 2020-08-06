package com.openxu.core.binding;

import android.view.View;
import android.widget.TextView;
import androidx.databinding.*;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.core.view.TitleLayout;

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

/**
 * BindingMethods、BindingMethod注解可以标记在任何类上，甚至是一个空类
 * 通过这两个注解可以将属性和set方法关联，type属性为控件类型，attribute为特性的属性，method为需要匹配的方法
 * 需要注意的是method对应的方法必须是public void setXXXX(...)形式，返回类型为void，方法参数为@{}表达式值的类型
 */
/*@BindingMethods({
        @BindingMethod(type = TitleLayout.class, attribute = "textcenter", method = "setMyTextcenter"),
})*/
public class XTitleLayoutBindingAdapter {

   @BindingAdapter("textcenter")
    public static String setTextcenter(TitleLayout view, String textcenter) {
       XLog.v("数据绑定适配器setTextcenter： "+textcenter);
        view.setMyTextcenter(textcenter);
        return "";
    }

    /**
     * 当 view 视特性发生更改时要调用的内容
     */
    @InverseBindingAdapter(attribute="textcenter")
    public static String getTextcenter(TitleLayout view) {
        XLog.v("数据绑定适配器getTextcenter： "+view.getTextcenter());
        return view.getTextcenter();
    }

    /**
     * 每个双向绑定都会生成“合成事件特性”。该特性与基本特性具有相同的名称，但包含后缀 "AttrChanged"。合成事件特性允许库创建使用 @BindingAdapter 注释的方法，以将事件监听器与相应的 View 实例相关联。
     * @param view
     * @param attrChange
     */
/*    @BindingAdapter("app:textcenterAttrChanged")
    public static void setListeners(TitleLayout view, InverseBindingListener attrChange) {
        XLog.w("监听到属性textcenter变化了： "+view.getTextcenter());
    }*/
}
