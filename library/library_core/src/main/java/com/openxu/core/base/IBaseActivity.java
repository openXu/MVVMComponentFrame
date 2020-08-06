package com.openxu.core.base;

import android.os.Bundle;

/**
 * Author: openXu
 * Time: 2019/3/15 9:42
 * class: IBaseView
 * Description:
 */
public interface IBaseActivity extends IBaseView {

    /**返回Activity布局文件*/
    int getContentView(Bundle savedInstanceState);
}
