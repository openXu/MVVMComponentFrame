package com.openxu.core.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog extends Dialog {

    protected Window window = null;
    protected View view;
    protected Activity activity;

    public BaseDialog(Context context) {
        this(context, 0);
    }

    public BaseDialog(Context context, int style) {
        super(context, style);
        //去掉title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
        activity = (Activity) context;

        int layout = getDialogLayout();
        if (layout == 0) {
            return;
        }
        view = LayoutInflater.from(getContext()).inflate(layout, null);
        setContentView(view);
        initView();
    }

    protected abstract int getDialogLayout();

    protected abstract void initView();

    protected void hideSoftInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void show() {
        windowDeploy(0, 0);
        super.show();
    }

    /**
     * dialog按设置参数的偏移量来显示，窗口外部的背景透明
     *
     * @param x x轴偏移量
     * @param y y轴
     */
    public void showDialog(int x, int y) {
        windowDeploy(x, y);
        show();
    }


    // 设置窗口显示
    public void windowDeploy(int x, int y) {
        window = getWindow(); // 得到对话框
        // window.setGravity(Gravity.RIGHT | Gravity.TOP);
        // // window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
        // this.findViewById(R.id.llDialogParent).setAnimation(
        // AnimationUtils.loadAnimation(context, R.anim.setting_slide_up_in));
        // window.setBackgroundDrawableResource(R.color.vifrification); //
        // 设置对话框背景为透明
        // } else {
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = window.getAttributes();
        //设置dialog大小
        WindowManager manager = activity.getWindowManager();
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
//        params.width = (int) (d.getWidth() * 1.0f); // 宽度设置为屏幕的0.75，根据实际情况调整
        // 根据x，y坐标设置窗口需要显示的位置
        params.x = x; // x小于0左移，大于0右移
        params.y = y; // y小于0上移，大于0下移
        // lp.width = 300; // 宽度
        // lp.height = 300; // 高度
        // lp.alpha = 0.7f; // 透明度
        params.dimAmount = 0.4f;
        // wl.alpha = 0.6f; //设置透明度
        // wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(params);
    }

}