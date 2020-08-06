package com.openxu.core.net.error;

/**
 * Author: openXu
 * Time: 2019/3/4 11:54
 * class: NetError
 * Description: 所有的网络请求异常（http异常、数据解析、后台业务错误）都将通过NetErrorHandle转换为该实例
 */
public class NetError extends RuntimeException {

    private int code;
    private String massage;
    private String userMsg;

    /**
     * @param code    错误码
     * @param massage 具体错误信息，用于开发中错误定位，可通过log打印
     * @param userMsg 发生错误时给用户提示的信息，通常Toast
     */
    public NetError(int code, String massage, String userMsg) {
        super();
        this.code = code;
        this.massage = massage;
        this.userMsg = userMsg;
    }

    @Override
    public String toString() {
        return "NetError{" +
                "code=" + code +
                ", massage='" + massage + '\'' +
                ", userMsg='" + userMsg + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }


}
