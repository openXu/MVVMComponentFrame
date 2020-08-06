package com.openxu.core.dialog;

import android.content.Context;

import com.openxu.core.R;


/**
 * Author: openXu
 * Time: 2019/4/12 11:36
 * class: TimeConsumDialog
 * Description: 耗时对话框（网络请求，图片处理....）
 * Update: 
 */
public class TimeConsumDialog extends BaseDialog {

    public TimeConsumDialog(Context context) {
        super(context);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getDialogLayout() {
        return R.layout.core_dialog_time_consum;
    }

    @Override
    protected void initView() {
    }

    @Override
    public void show() {
        super.show();
       /* Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.gravity = Gravity.CENTER;
        wl.dimAmount = 0f;
        wl.alpha = 1f; //设置透明度
        window.setAttributes(wl);*/
    }
}
