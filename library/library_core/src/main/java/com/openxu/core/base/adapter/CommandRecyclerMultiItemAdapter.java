package com.openxu.core.base.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * autour : openXu
 * date : 2017/9/7 19:05
 * className : CommandRecyclerAdapter
 * version : 1.0
 * description : 通用的RecyclerAdapter 多种ItemViewType
 */
public abstract class CommandRecyclerMultiItemAdapter<T> extends CommandRecyclerAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public CommandRecyclerMultiItemAdapter(Context context, List<T> datas, MultiItemTypeSupport<T> multiItemTypeSupport){
        super(context, -1, datas);
        mMultiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemViewType(int position){
        return mMultiItemTypeSupport.getItemViewType(position, mDatas.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        ViewHolder holder = ViewHolder.get(mContext, parent, layoutId);
        return holder;
    }


}
