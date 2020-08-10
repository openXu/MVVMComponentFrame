package com.openxu.core.http.rx;

import com.google.gson.Gson;
import com.openxu.core.utils.XLog;
import com.openxu.core.http.NetworkManager;
import com.openxu.core.http.data.XResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author: openXu
 * Time: 2019/3/5 16:40
 * class: ParseDataFunction
 * Description: 用于解析服务器返回数据的Funcation，由于不同公司项目业务接口数据返回格式差异，此处只能简单的返回传递的泛型类型，所以每个接口需要单独封装结果数据，存在冗余封装（返回状态码、错误码、错误信息等）。
 *
 *          ps：如果项目接口返回数据格式是固定的，可以在XResponse中封装，在该Function的apply方法中做统一数据格式解析，返回XResponse
 *
 */
public class ParseDataFunction<T> implements Function<ResponseBody, T> {

    //泛型必须在编程时指定而不是运行时才能获取到泛型的类型，所以此处需要通过构造方法传入数据类型
    private Class dataClass;

    public ParseDataFunction(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

   /* public Class getTClass(){
        Class tClass = (Class)((ParameterizedType)getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
        return tClass;
    }
*/
    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        XLog.i("返回数据：" + result);
        if(null==dataClass)
            return (T) new XResponse(result);
        else{
            Gson gson = new Gson();
            T response = (T) gson.fromJson(result, dataClass);
            return response;
        }
    }
}
