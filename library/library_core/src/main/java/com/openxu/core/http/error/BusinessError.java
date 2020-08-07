package com.openxu.core.http.error;

/**
 * Author: openXu
 * Time: 2019/3/5 16:05
 * class: BusinessError
 * Description: 后台返回的业务错误，将直接提示给用户
 */
public class BusinessError extends RuntimeException {

    private String massage;

    public BusinessError(String massage) {
        this.massage = massage;
    }
    @Override
    public String toString() {
        return "BusinessError{" +massage+ '}';
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
