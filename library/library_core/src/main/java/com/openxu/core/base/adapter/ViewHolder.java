package com.openxu.core.base.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


/**
 * autour : openXu
 * date : 2017/9/7 19:04
 * className : ViewHolder
 * version : 1.0
 * description : 通用的ViewHolder
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private ViewDataBinding binding;
    private View rootView;
    private Context mContext;

    public ViewHolder(Context context, ViewDataBinding binding, ViewGroup parent) {
        super(binding.getRoot());
        this.binding = binding;
        this.mContext = context;
        this.mViews = new SparseArray<>();
    }

    public ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        this.rootView = itemView;
        mViews = new SparseArray<>();
    }

    public static ViewHolder get(Context context, ViewGroup parent, int layoutId) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutId, parent, false);
        if (null == binding) {
            ViewGroup itemView = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, parent, false);
            return new ViewHolder(context, itemView, parent);
        } else {
            return new ViewHolder(context, binding, parent);
        }
    }
    public static ViewHolder get2(Context context, View itemView) {
            return new ViewHolder(context, itemView, null);
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            if (binding == null) {
                view = rootView.findViewById(viewId);
            } else {
                view = binding.getRoot().findViewById(viewId);
            }
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getRootView() {
        if (binding == null) {
            return rootView;
        } else {
            return binding.getRoot();
        }
    }

    public ViewHolder setVisible(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public ViewHolder setImageColorFilter(int viewId, int color) {
        ImageView tv = getView(viewId);
        tv.setColorFilter(color);
        return this;
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(TextUtils.isEmpty(text) ? "" : text);
        return this;
    }
    public ViewHolder setEnable(int viewId, boolean enable) {
        View tv = getView(viewId);
        tv.setEnabled(enable);
        return this;
    }

    public ViewHolder setBackgroundResource(int viewId, int id) {
        View view = getView(viewId);
        view.setBackgroundResource(id);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public ViewHolder setCheckBoxChecked(int viewId, boolean check) {
        CheckBox cb = getView(viewId);
        cb.setChecked(check);
        return this;
    }

    public ViewHolder setLinearLayoutBgIcon(int viewId, int iconResourse) {
        LinearLayout ll = getView(viewId);
        ll.setBackgroundResource(iconResourse);
        return this;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        if (viewId == -1) {
            if (binding == null) {
                rootView.setOnClickListener(listener);
            } else {
                binding.getRoot().setOnClickListener(listener);
            }
        } else {
            View view = getView(viewId);
            view.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        if (viewId == -1) {
            if (binding == null) {
                rootView.setOnLongClickListener(listener);
            } else {
                binding.getRoot().setOnLongClickListener(listener);
            }
        } else {
            View view = getView(viewId);
            view.setOnLongClickListener(listener);
        }
        return this;
    }
}