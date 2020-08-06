package com.openxu.english;

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
public class RouterPathEnglish {

    private static final String GROUP = "/app_english/";

    //splash
    public static final String PAGE_SPLASh = GROUP +"splash";
    //主界面
    public static final String PAGE_MAIN = GROUP + "/main";
    public static final String PAGE_FRAGMENT_CIDIAN = GROUP + "/cidian";
    public static final String PAGE_FRAGMENT_FANYI = GROUP + "/fanyi";
    public static final String PAGE_FRAGMENT_CHACI = GROUP + "/chaci";

}
