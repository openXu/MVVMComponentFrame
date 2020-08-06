package com.openxu.core.net;

import com.google.gson.Gson;

import org.json.JSONException;


/**
 * Author: openXu
 * Time: 2019/3/5 16:40
 * class: ParseNetData
 * Description: TODO: 2020/8/3  解析数据工具，需要根据项目业务接口数据格式重写解析方法
 */
public class ParseNetData {

    /**
     * 根据传入的数据类型，解析json
     * @param data 数据json字符串
     * @param dataClassType 数据的Class类型
     * @throws JSONException
     */
    public static void parseDataSource(String data, Class dataClassType) throws JSONException {
        parseData(data, dataClassType);
    }
    public static <T> T parseData(String data, Class dataClassType) {
        Gson gson = new Gson();
        return (T) gson.fromJson(data, dataClassType);
    }

}
