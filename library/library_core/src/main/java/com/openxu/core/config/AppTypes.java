package com.openxu.core.config;

/**
 * Author: openXu
 * Time: 2019/3/9 13:04
 * class: AppTypes
 * Description: app版本，
 */
public enum AppTypes {


    TYPE_ALONG,     //模块层独立调试  FpcLibsProject\conf.gradle
    TYPE_TEST,      //测试工程

    TYPE_INSPECT,    //员工巡检
    TYPE_SINOPEC_INSPECT,    //员工巡检(油库)
    TYPE_LDJG,    //领导监管
    TYPE_SINOPEC_LDJG,    //领导监管(油库)
    TYPE_WBFW,    //维保服务
    TYPE_WBGL,    //维保管理
    TYPE_SHOP,  //一般单位
    TYPE_WELL //我的井

}

