package com.openxu.core.http.error;

import com.google.gson.JsonParseException;
import com.openxu.core.config.AppConfig;
import com.openxu.core.utils.XLog;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.HttpException;

/**
 * Author: openXu
 * Time: 2019/3/4 11:25
 * class: NetErrorHandle
 * Description: 处理http请求过程中出现的错误，以及后台返回的相关业务错误，将其转换成用户能看懂的提示
 */
public class NetErrorHandle {

    /**HTTP状态码*/
    //4xx(请求错误)
    public static final int CODE_BAD_REQUEST = 400;  //(错误请求) 语义有误或者请求参数有误，当前请求无法被服务器理解
    public static final int CODE_UNAUTHORIZED = 401;   //(未授权) 当前请求需要用户验证
    public static final int CODE_FORBIDDEN = 403;      //(已禁止) 服务器已经理解请求，但是拒绝执行它
    public static final int CODE_NOT_FOUND = 404;      //(未找到) 请求失败，请求所希望得到的资源未被在服务器上发现
    public static final int CODE_METHOD_NOT_ALLOWED = 405;      //(方法禁用) 禁用请求中所指定的方法
    public static final int CODE_NOT_ACCEPTEABLE = 406;      //(不接受) 请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体
    public static final int CODE_PROXY_UNAUTHORIZED = 407 ;      //与401响应类似，只不过客户端必须在代理服务器上进行身份验证
    public static final int CODE_REQUEST_TIMEOUT = 408;  //(请求超时) 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送
    public static final int CODE_CONFLICT = 409;  //(冲突) 由于和被请求的资源的当前状态之间存在冲突，请求无法完成
    public static final int CODE_GONE = 410;  //(已删除) 被请求的资源在服务器上已经不再可用
    public static final int CODE_LENGTH_REQUIRED = 411;  //(需要有效长度) 服务器不会接受包含无效内容长度标头字段的请求。
    public static final int CODE_PRECONDITION_FAILED = 412;  //服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个
    public static final int CODE_ENTITY_TOOLARGE = 413;  //(请求实体过大) 服务器无法处理请求，因为请求实体过大，已超出服务器的处理能力。
    public static final int CODE_URL_TOOLONG = 414;  //(请求的 URI 过长) 请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务
    public static final int CODE_UNSUPPORTED_MEDIATYPE = 415;  //(不支持的媒体类型) 请求的格式不受请求页面的支持。
    public static final int CODE_RANGE_NOTSATISFIABLE = 416;  //(请求范围不符合要求) 如果请求是针对网页的无效范围进行的，那么，服务器会返回此状态代码。
    public static final int CODE_EXPECTATION_FAILED = 417;  //(未满足期望值) 服务器未满足”期望”请求标头字段的要求。
    //5xx(服务器错误)这类状态码代表了服务器在处理请求的过程中有错误或者异常状态发生，也有可能是服务器意识到以当前的软硬件资源无法完成对请求的处理。除非这是一个HEAD 请求，否则服务器应当包含一个解释当前错误状态以及这个状况是临时的还是永久的解释信息实体。浏览器应当向用户展示任何在当前响应中被包含的实体
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;  //(服务器内部错误) 服务器遇到错误，无法完成请求。
    public static final int CODE_NOT_IMPLEMENTED = 501;  //(尚未实施) 服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求
    public static final int CODE_BAD_GATEWAY = 502;  //(错误网关) 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应
    public static final int CODE_SERVICE_UNAVAILABLE = 503;  //(服务不可用) 目前无法使用服务器(由于超载或进行停机维护)。通常，这只是一种暂时的状态。
    public static final int CODE_GATEWAY_TIMEOUT = 504;  //(网关超时) 服务器作为网关或代理，未及时从上游服务器接收请求。
    public static final int CODE_VERSION_NOT_SUPPORTED = 505;  //(HTTP 版本不受支持) 服务器不支持请求中所使用的 HTTP 协议版本

    /**自定义异常*/
    public static final int CODE_UNKNOWN_HOST = 1000;  //无法解析该域名
    public static final int CODE_CONNECT_EXC = 1001;  //连接服务器异常
    public static final int CODE_SOCKET_EXC = 1002;  //连接服务器中断
    public static final int CODE_SOCKET_TIMEOUT = 1003;  //连接超时
    public static final int CODE_SOCKET_RESET = 1004;  //连接重置
    public static final int CODE_JSON_EXC = 2000;  //json解析错误
    public static final int CODE_BUSINESS_ERROR = 3000;  //后台业务错误
    public static final int CODE_DOWNLOAD_ERROR = 5000;  //下载文件错误
    public static final int CODE_UNKNOWN_ERROR = 10000;  //未知错误

    /**推送异常*/
    public static final int CODE_SENDDATA_ERROR = 20001;        //发送数据异常
    public static final int CODE_RECEIVEDATA_ERROR = 20002;     //接受数据异常
    public static final int CODE_RECEIVEDATA_TIMEOURT = 20003;  //读取数据超时

    private static Map<Integer, String> errorMsgMap = new HashMap();

    static {
        //4xx(请求错误)
        errorMsgMap.put(400, "请求错误");
        errorMsgMap.put(401, "未授权，请求需要用户验证");
        errorMsgMap.put(403, "已禁止，拒绝执行");
        errorMsgMap.put(404, "未找到资源");
        errorMsgMap.put(405, "方法禁用");
        errorMsgMap.put(406, "不接受");
        errorMsgMap.put(407 , "未授权");
        errorMsgMap.put(408, "请求超时");
        errorMsgMap.put(409, "冲突");
        errorMsgMap.put(410, "已删除");
        errorMsgMap.put(411, "需要有效长度");
        errorMsgMap.put(412, "请求头没能满足");
        errorMsgMap.put(413, "请求实体过大");
        errorMsgMap.put(414, "请求的 URI 过长");
        errorMsgMap.put(415, "不支持的媒体类型");
        errorMsgMap.put(416, "请求范围不符合要求");
        errorMsgMap.put(417, "未满足期望值");
        //5xx(服务器错误)这类状态码代表了服务器在处理请求的过程中有错误或者异常状态发生，也有可能是服务器意识到以当前的软硬件资源无法完成对请求的处理。除非这是一个HEAD 请求，否则服务器应当包含一个解释当前错误状态以及这个状况是临时的还是永久的解释信息实体。浏览器应当向用户展示任何在当前响应中被包含的实体
        errorMsgMap.put(500, "服务器内部错误");
        errorMsgMap.put(501, "尚未实施");
        errorMsgMap.put(502, "错误网关");
        errorMsgMap.put(503, "服务不可用");
        errorMsgMap.put(504, "网关超时");
        errorMsgMap.put(505, "HTTP 版本不受支持");
    }
    public static NetError handleError(Throwable throwable){
        throwable.printStackTrace();
        XLog.e("网络框架异常捕获："+throwable);
        NetError error;
        if (throwable instanceof NetError){
            error = (NetError)throwable;
        }else if (throwable instanceof UnknownHostException){  //无法解析该域名异常
            error = new NetError(CODE_UNKNOWN_HOST,"无法解析该域名-"+throwable.getMessage(), "请检查网络");
        }else if (throwable instanceof ConnectException){  //表示尝试将套接字连接到远程地址和端口时出错。通常，连接被远程拒绝（例如，没有进程在侦听远程地址/端口）。
            error = new NetError(CODE_CONNECT_EXC, "连接服务器异常-"+throwable.getMessage(), "请检查网络");
        }else if (throwable instanceof SocketTimeoutException){
            error = new NetError(CODE_SOCKET_TIMEOUT, "传输超时-"+throwable.getMessage(), "联网超时");
        }else if(throwable instanceof SocketException ||
                throwable instanceof IOException){  //Software caused connection abort
            if(throwable instanceof SocketException && throwable.getMessage().contains("Connection reset")){
                //java.net.SocketException: Connection reset
                if(AppConfig.DEBUG){
                    error = new NetError(CODE_SOCKET_RESET, "连接重置-"+throwable.getMessage(), "严重bug连接重置，请重试");
                }else{
                    error = new NetError(CODE_SOCKET_RESET, "连接重置-"+throwable.getMessage(), "请检查网络后重试");
                }
            }else{
                error = new NetError(CODE_SOCKET_EXC, "连接服务器中断-"+throwable.getMessage(), "请检查网络");
            }
        }else if (throwable instanceof HttpException){   //网络错误：意外的非2xx HTTP响应的异常
            int code = ((HttpException)throwable).code();
            String body = "";
            try{
                //向下转型，然后取出body,body中有具体错误信息
                body = ((HttpException) throwable).response().errorBody().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            error = new NetError(code, errorMsgMap.get(code)+"-"+body, errorMsgMap.get(code));
        }
        //数据解析错误 & 后台业务错误
        else if (throwable instanceof JsonParseException ||
                throwable instanceof JSONException){
            error = new NetError(CODE_JSON_EXC, "json解析错误-"+throwable.getMessage(), "数据解析错误");
        }else if (throwable instanceof BusinessError){
            error = new NetError(CODE_BUSINESS_ERROR, "后台业务错误-"+((BusinessError)throwable).getMassage(), ((BusinessError)throwable).getMassage());
        }
        else{
            error = new NetError(CODE_UNKNOWN_ERROR, "未知错误-"+throwable.getMessage(), "未知错误");
        }
        return error;
    }

}
