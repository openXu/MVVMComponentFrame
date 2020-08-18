package com.openxu.core;

/**
 * Author: openXu
 * Time: 2019/2/25 11:14
 * class: RouterPath
 * Description: ARouter路由路径注册
 * <p>
 * 注意：
 * 1、不同的Module应该分不同的组，/groupName/pageName，也就是说groupName不能一样，否则可能导致
 * https://github.com/alibaba/ARouter/issues?utf8=%E2%9C%93&q=Multiple%20dex%20files%20define%20Lcom/alibaba/android/arouter/routes/ARouter
 *
 * 2、每个模块都应该有自己的路由注册类，该模块下所有页面都在对应注册类中注册
 */
public class RouterPathCore {

    private static final String GROUP = "/libbase";
    public static final String PAGE_ZXING = GROUP + "/zxing";  //二维码

}
