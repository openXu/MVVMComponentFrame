package com.openxu.view;

import android.content.Context;
import android.widget.TextView;

import com.openxu.english.R;

/**
 * Shenhualugang -- com.shenhualugang.view
 * User: lizheng<br>
 * Date: 13-7-16<br>
 * Time: 下午15:57<br>
 * Email: kenny.li@itotemdeveloper.com<br>
 * 加载框
 */
public class CopyDBDialog extends ItotemBaseDialog {

    private TextView tvShowText, upload_text;

    public CopyDBDialog(Context context) {
        super(context, R.layout.dialog_dbcopy, R.style.ItotemTheme_Dialog_Loading);
    }

    @Override
    protected void initView() {
        tvShowText = (TextView) findViewById(R.id.tvShowText);
        upload_text = (TextView) findViewById(R.id.upload_text);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

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
