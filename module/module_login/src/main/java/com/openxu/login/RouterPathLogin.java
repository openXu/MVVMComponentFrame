package com.openxu.login;

/**
 * Author: openXu
 * Time: 2019/2/25 11:14
 * class: RouterPathCommon
 * Description: ARouter路由路径注册
 * <p>
 * 注意：
 * 1、不同的Module应该分不同的组，/groupName/pageName，也就是说groupName不能一样，否则可能导致
 * https://github.com/alibaba/ARouter/issues?utf8=%E2%9C%93&q=Multiple%20dex%20files%20define%20Lcom/alibaba/android/arouter/routes/ARouter
 */
public class RouterPathLogin {

    private static final String GROUP = "/module_login/";

    //登录
    public static final String PAGE_LOGIN = GROUP + "login";
    //注册
    public static final String PAGE_REGIST = GROUP + "regist";
}
