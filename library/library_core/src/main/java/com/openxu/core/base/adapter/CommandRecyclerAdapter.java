package com.openxu.core.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.openxu.core.utils.XClickUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * autour : openXu
 * date : 2017/9/7 19:05
 * className : CommandRecyclerAdapter
 * version : 1.0
 * description : 通用的CommandRecyclerAdapter
 */
public abstract class CommandRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    public List<T> mDatas;
    protected LayoutInflater mInflater;
    protected boolean longClickAble = false;   //是否支持长点击

    public interface IDataObserver {
        /**
         * 适配器数据发生变化时回调
         *
         * @param noData   是否为空
         * @param netError 是否为网络错误（传入数据集合为null）
         */
        void dataChanged(boolean noData, boolean netError);
    }

    private IDataObserver dataObserver;

    public void setDataObserver(IDataObserver dataObserver) {
        this.dataObserver = dataObserver;
    }

    public void setLongClickAble(boolean longClickAble) {
        this.longClickAble = longClickAble;
    }

    public CommandRecyclerAdapter(Context context, int layoutId, List<T> datas) {
        mDatas = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            dataChanged(false, false);   //有数据
        }

        //数据集中的项是否具有唯一标识，设置为true避免刷新时老数据闪烁，需要重写getItemId()
        setHasStableIds(true);
    }

    private void dataChanged(boolean noData, boolean netError) {
        if (dataObserver != null) {
            dataObserver.dataChanged(noData, netError);
        }
    }

    public void setData(List<T> datas) {
        mDatas.clear();
        if (datas != null) {
            if (datas.size() > 0) {
                mDatas.addAll(datas);
                dataChanged(false, false);   //有数据
            } else {
                dataChanged(true, false);    //服务器返回数据为空
            }
        } else {
            if (mDatas.size() <= 0)
                dataChanged(true, true);     //请求数据发生错误
        }
        notifyDataSetChanged();
    }

    /**
     * 添加数据，局部刷新，一般用于分页加载
     */
    public void addData(List<T> datas) {
//        int startPoint = mDatas.size();
        if (datas != null && datas.size() > 0) {
            dataChanged(false, false);   //有数据
            mDatas.addAll(datas);
            notifyDataSetChanged();
            //局部刷新
//            notifyItemRangeChanged(startPoint, datas.size());
        }
    }

    public List<T> getData() {
        return mDatas;
    }

    public void addData(T data) {
        int startPoint = mDatas.size();
        if (data != null) {
            dataChanged(false, false);   //有数据
            mDatas.add(data);
//        不能使用下面代码，会在换行的显示的时候出现高度测量不准的问题
//        notifyItemRangeChanged(startPoint, 1);
            notifyDataSetChanged();
        }
    }

    public void removeData(T data) {
        if (mDatas.contains(data)) {
            mDatas.remove(data);
            dataChanged(mDatas.size() <= 0, false);   //有数据
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return ViewHolder.get(mContext, parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.getBinding().setVariable(BR.data, getItem(position));
        convert(holder, getItem(position), position);
        holder.setOnClickListener(-1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (XClickUtil.isNotFastClick())
                    onItemClick(getItem(position), position);
            }
        });
        if (longClickAble) {
            holder.setOnLongClickListener(-1, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClick(getItem(position), position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return null == mDatas || position >= mDatas.size() ? null : mDatas.get(position);
    }

    /**
     * 重写此方法，将数据绑定到控件上
     *
     * @param holder
     * @param t
     */
    public abstract void convert(ViewHolder holder, T t, int position);

    /***
     * item点击
     * @param data
     */
    public abstract void onItemClick(T data, int position);

    /***
     * item点击
     * @param data
     */
    public void onItemLongClick(T data, int position) {
    }

}