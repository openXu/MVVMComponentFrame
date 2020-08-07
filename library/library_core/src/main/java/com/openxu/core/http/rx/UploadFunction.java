package com.openxu.core.http.rx;

import android.text.TextUtils;


import com.openxu.core.http.data.XResponse;
import com.openxu.core.http.error.BusinessError;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author: openXu
 * Time: 2019/3/5 16:40
 * class: UploadFunction
 * Description: 文件上传操作符
 */
public class UploadFunction implements Function<ResponseBody, XResponse> {

    @Override
    public XResponse apply(ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        /*
        {"201904120944330147.jpg":"/uploadfiles/2019/20190412-0944/201904120944330147.jpg",
        "201904120944348047.jpg":"/uploadfiles/2019/20190412-0944/201904120944348047.jpg",
        "201904120944361836.jpg":"/uploadfiles/2019/20190412-0944/201904120944361836.jpg"}
         */
        if(TextUtils.isEmpty(result) || !result.contains("uploadfiles")){
            throw new BusinessError(result);
        }
        //模拟post返回数据
        XResponse response = new XResponse();
        response.setStatusCode(100);
        response.setMessage("附件上传成功："+result);
        return response;
    }
}
