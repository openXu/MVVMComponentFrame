package com.openxu.core.http.callback;

/**
 * Author: openXu
 * Time: 2019/3/11 9:27
 * class: ResponseCallback
 * Description:
 * <p>
 * //                new ResponseCallback(User.class){
 * //                    @Override
 * //                    public void onSuccess(String msg, FpcDataSource data) {
 * //                        FLog.i("fzyGet返回数据："+data.getTables().size());
 * //                        FLog.i("fzyGet返回数据："+data.getTables().get(0).getDataList().get(0));
 * //                        user.setValue((User)data.getTables().get(0).getDataList().get(0));
 * //                    }
 * //                }
 */
public abstract class ResponseCallback<T> extends BaseCallback {
    private Class dataClass;

    public Class getDataClass() {
        return dataClass;
    }

    public ResponseCallback(Class clazz) {
        dataClass = clazz;
    }
    public ResponseCallback() {
    }

    public abstract void onSuccess(T result) throws Exception;
}
