package com.openxu.core.http.api;

/**
 * Author: openXu
 * Time: 2019/4/3 18:13
 * class: ServerApi
 * Description: 服务器接口
 */
public class ServerApi {


    public static final String App_Operlog_AddOne = "App_Operlog_AddOne";   //提交操作日志

    //module_common
    public static final String LOGIN = "User_QueryPhonePrincipal";   //登录--获取用户数据
    public static final String SDIS_F_SYS_GetOne = "SDIS_F_SYS_GetOne";       //客户端请求信息通道开关
    public static final String POST_APPTOKEN_UPDATE = "User_AppToken_Update";       //更新推送token
    public static final String POST_APPTOKEN_UPDATE_NEW = "DEX_AppToken_AddOne";       //注册终端设备
    public static final String GET_USER_PERMISSION = "SDMS_User_QueryPrincipalForChart";//获取权限  除了巡检版外使用这个皆苦
    public static final String GET_USER_LOGIN = "Shop_User_login";//获取密码
    public static final String GET_CHANGE_PWD = "Shop_User_Change_Pwd";//修改密码
    public static final String POST_FEED_BACK = "CMDS_Feedback_AddOne";//意见反馈
    public static final String POST_USER_ICON = "User_HeadPortrait_Update";//替换用户头像接口
    public static final String GET_APP_NEWVERSION = "DEX_GetApp_One";//检测新版本


    //module_minitask微任务
    public static final String GET_MINITASK_TASKLIST = "CMDS_MiniTask_GetTaskList";   //微任务--任务列表
    public static final String POST_MINITASK_ADD = "CMDS_MiniTask_AddOne";//任务下发
    public static final String GET_REGION_LIST = "CMDS_Region_GetSonList";   //区域列表
    public static final String POST_MINITASK_REGIST = "CMDS_MiniTask_SubmitOne";//微任务_任务提交
    public static final String POST_MINITASK_ESTIMATE = "CMDS_MiniTask_EstimateOne";  //任务验收
    public static final String GET_MINITASK_DETAIL = "CMDS_MiniTask_GetTaskOne";   //微任务_获取任务明细
    public static final String CMDS_MiniTask_GetUser = "CMDS_MiniTask_GetUser";  //查询岗位人员
    public static final String CMDS_MiniTask_GetDepartPostList = "CMDS_MiniTask_GetDepartPostList";//

}
