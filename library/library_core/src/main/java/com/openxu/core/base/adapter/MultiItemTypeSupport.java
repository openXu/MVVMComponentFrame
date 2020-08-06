package com.openxu.core.base.adapter;

/**
 * Created by Admin on 2018/7/24.
 */

public interface MultiItemTypeSupport<T> {
    int getLayoutId(int itemType);
    int getItemViewType(int position, T t);
}
