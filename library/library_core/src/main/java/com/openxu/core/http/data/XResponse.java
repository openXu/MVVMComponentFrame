package com.openxu.core.http.data;

/**
 * Author: openXu
 * Time: 2020/8/3 10:46
 * class: XResponse
 * Description: 统一的接口返回数据，由于框架开源，不能针对业务需求做数据解析处理，此处简单通过String返回。
 *              如果项目业务接口有统一的数据返回格式，可参照FpcResponse在此封装，然后重写ParseDataFunction统一解析数据
 */
public class XResponse<T> {
    public XResponse(String result) {
        this.result = result;
    }
    public XResponse() {
    }
    String result;
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }


    /**服务器返回数据格式约定 示例*/
    /*
   {
        "resultNode": "1",
        "statusCode": "100",
        "message": "请求数据成功完成"
        "data": {
            "Name": "equipments",
            "Key": "equipmentID",
        }
    }
    */
    private String resultNode;
    private int statusCode;
    private String message;
    private T data;       //解析之后最终对象

    public void setData(T data) {
        this.data = data;
    }

    public String getResultNode() {
        return resultNode;
    }

    public void setResultNode(String resultNode) {
        this.resultNode = resultNode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
