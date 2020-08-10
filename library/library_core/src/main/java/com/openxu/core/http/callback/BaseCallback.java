package com.openxu.core.http.callback;


import com.openxu.core.utils.toasty.XToast;

/**
 * Author: openXu
 * Time: 2019/4/11 9:53
 * class: BaseCallback
 * Description:
 */
public class BaseCallback<T> {

    public void onFail(String message) {
        XToast.error(message);
    }

}
