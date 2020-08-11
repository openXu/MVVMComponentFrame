package com.openxu.db.view;

import android.content.Context;
import android.widget.TextView;

import com.openxu.core.dialog.BaseDialog;
import com.openxu.db.R;

/**
 * Shenhualugang -- com.shenhualugang.view
 * User: lizheng<br>
 * Date: 13-7-16<br>
 * Time: 下午15:57<br>
 * Email: kenny.li@itotemdeveloper.com<br>
 * 加载框
 */
public class CopyDBDialog extends BaseDialog {

    private TextView tvShowText, upload_text;

    public CopyDBDialog(Context context) {
        super(context);
    }

    public CopyDBDialog(Context context, int style) {
        super(context, style);
    }


    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_dbcopy;
    }

    @Override
    protected void initView() {
        tvShowText = (TextView) findViewById(R.id.tvShowText);
        upload_text = (TextView) findViewById(R.id.upload_text);
    }

    public void setShowText(int resId){
        tvShowText.setText(resId);
    }
    public void setShowText(String showText){
        tvShowText.setText(showText);
    }
    public void setUploadingText(String uploadtext){
        upload_text.setText(uploadtext);
    }

}
