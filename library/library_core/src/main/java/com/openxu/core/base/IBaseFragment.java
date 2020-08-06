package com.openxu.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * Author: openXu
 * Time: 2019/3/15 9:42
 * class: IBaseView
 * Description:
 */
public interface IBaseFragment extends IBaseView {

    /**返回Fragment布局ID*/
    int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

}
