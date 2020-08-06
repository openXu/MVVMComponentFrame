package com.openxu.core.net.rx;

import com.google.gson.Gson;
import com.openxu.core.utils.XLog;
import com.openxu.core.net.NetworkManager;
import com.openxu.core.net.data.XResponse;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author: openXu
 * Time: 2019/3/5 16:40
 * class: ParseDataFunction
 * Description: 用于解析服务器返回数据的Funcation，由于不同公司项目业务接口数据返回格式差异，此处只能简单的返回String，数据解析放到请求之后。
 *
 *          ps：如果项目接口返回数据格式是固定的，可以在XResponse中封装，在该Function的apply方法中做统一数据格式解析，返回XResponse
 *
 *          其实真正解析数据是一个工具类完成的，该Function的作用是将解析数据的动作放到RxJava流中处理，在业务使用时直接能得到想要的结果数据。
 *
 */
public class ParseDataFunction implements Function<ResponseBody, XResponse> {
    private Class dataClass;
    private NetworkManager.RequstBuilder builder;

    public ParseDataFunction(Class dataClass, NetworkManager.RequstBuilder builder) {
        this.dataClass = dataClass;
    }
    @Override
    public XResponse apply(ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        XLog.i("返回数据：" + result);
        Gson gson = new Gson();
        XResponse response = gson.fromJson(result, XResponse.class);
        return response;
        /**模拟统一数据解析，直接返回结果数据*/
        /*
        if (response.getStatusCode() == 200) {
            if (builder.method == NetworkManager.Method.GET) {
                //get请求，解析数据
                ParseNetData.parseDataSource(response.getResult(), dataClass);
            } else {
                //提交成功统一提示
                //XToast.success("提交成功");
            }
            return response;
        } else {
            throw new BusinessError(response.getMessage());
        }*/
    }
}
