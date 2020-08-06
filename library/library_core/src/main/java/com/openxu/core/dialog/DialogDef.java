package com.openxu.core.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxu.core.R;


/**
 * autour: openXu
 * date: 2017/3/18 18:12
 * className: DialogLogin
 * version:
 * description: 通用dialog
 */
public class DialogDef extends BaseDialog {

    private LinearLayout ll_btn_two, ll_btn_one;
    private TextView tv_title, tv_message, tv_cancel, tv_ok, tv_btn_one;
    private View view_line;

    private String title, message, cancelStr, sureStr, oneBtnStr;

    public DialogDef(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getDialogLayout() {
        return R.layout.core_dialog_def;
    }

    @Override
    protected void initView() {
        ll_btn_two = (LinearLayout)findViewById(R.id.ll_btn_two);
        ll_btn_one = (LinearLayout)findViewById(R.id.ll_btn_one);
        tv_title = (TextView)findViewById(R.id.tv_title);
        view_line = findViewById(R.id.view_line);
        tv_message = (TextView)findViewById(R.id.tv_message);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_ok = (TextView)findViewById(R.id.tv_ok);
        tv_btn_one = (TextView)findViewById(R.id.tv_btn_one);

        tv_cancel.setOnClickListener(v->{
            if(listener!=null)
                listener.onCancel();
            dismiss();
        });
        tv_ok.setOnClickListener(v->{
            dismiss();
            if(listener!=null) {
                listener.onOk();
            }
        });
        tv_btn_one.setOnClickListener(v->{
            dismiss();
            if(listener!=null) {
                listener.onOneBtn();
            }
        });
    }

    public DialogDef setTitle(String title){
        this.title = title;
        tv_title.setText(title);
        return this;
    }
    public DialogDef setMessage(String message){
        this.message = message;
        tv_message.setText(message);
        return this;
    }
    public DialogDef setCancelStr(String cancelStr){
        this.cancelStr = cancelStr;
        tv_cancel.setText(cancelStr);
        return this;
    }
    public DialogDef setSureStr(String sureStr){
        this.sureStr = sureStr;
        tv_ok.setText(sureStr);
        return this;
    }

    public DialogDef setCancelTextColor(int color){
        tv_cancel.setTextColor(color);
        return  this;
    }
    public DialogDef setOkTextColor(int color){
        tv_ok.setTextColor(color);
        return  this;
    }
    public DialogDef setOneBtnStr(String oneBtnStr){
        this.oneBtnStr = oneBtnStr;
        tv_btn_one.setText(oneBtnStr);
        return this;
    }
    public DialogDef setCancelBg(int bg){
        tv_cancel.setBackgroundResource(bg);
        return this;
    }
    public DialogDef setSureBg(int bg){
        tv_ok.setBackgroundResource(bg);
        return this;
    }
    public DialogDef setOneBtnBg(int bg){
        tv_btn_one.setBackgroundResource(bg);
        return this;
    }
    public DialogDef setOneBtnBgColor(int bg){
        tv_btn_one.setBackgroundColor(bg);
        return this;
    }
    public DialogDef setOneBtnTextColor(int textColor){
        tv_btn_one.setTextColor(textColor);
        return this;
    }

//    设置点击后不消失弹窗
    public DialogDef setNotDismiss(){
        //设置点击屏幕不消失
        this.setCanceledOnTouchOutside(false);
        //设置点击返回键不消失
        this.setCancelable(false);
        return this;
    }


    @Override
    public void show() {
        tv_title.setVisibility(TextUtils.isEmpty(title)? View.GONE:View.VISIBLE);
        view_line.setVisibility(TextUtils.isEmpty(title)? View.GONE:View.VISIBLE);
        tv_message.setVisibility(TextUtils.isEmpty(message)? View.GONE:View.VISIBLE);

        ll_btn_one.setVisibility(TextUtils.isEmpty(oneBtnStr)?View.GONE:View.VISIBLE);
        if(TextUtils.isEmpty(cancelStr) && TextUtils.isEmpty(sureStr)){
            ll_btn_two.setVisibility(View.GONE);
        }else{
            ll_btn_two.setVisibility(View.VISIBLE);
        }
        super.show();
    }

    private OnBtnClickListener listener;
    public static class OnBtnClickListener{
        public void onCancel(){}
        public void onOk(){}
        public void onOneBtn(){}
    }
    public DialogDef setOnBtnCLickListener(OnBtnClickListener listener){
        this.listener = listener;
        return this;
    }

}