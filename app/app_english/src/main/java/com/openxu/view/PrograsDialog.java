package com.openxu.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
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
public class PrograsDialog extends ItotemBaseDialog {

    private TextView  id_tv_loadingmsg;
    private ImageView loadingImageView;
    Animation stateAnim;
    public PrograsDialog(Context context) {
        super(context, R.layout.dialog_pro, R.style.ItotemTheme_Dialog_Loading);
    }

    @Override
    protected void initView() {
    	loadingImageView = (ImageView) findViewById(R.id.loadingImageView);
        id_tv_loadingmsg = (TextView) findViewById(R.id.id_tv_loadingmsg);
		// 进度条转圈圈
		stateAnim = AnimationUtils.loadAnimation(context,R.anim.open_listviewloding_anim);
		LinearInterpolator lin = new LinearInterpolator();
		stateAnim.setInterpolator(lin);
    }

    public void onWindowFocusChanged(boolean hasFocus){
    	loadingImageView.startAnimation(stateAnim);
    }
    
    @Override
    public void cancel() {
    	stateAnim.cancel();
    	super.cancel();
    }
    @Override
    public void dismiss() {
    	stateAnim.cancel();
    	super.dismiss();
    }
    
    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    public void setShowText(int resId){
    	id_tv_loadingmsg.setText(resId);
    }
    public void setShowText(String showText){
    	id_tv_loadingmsg.setText(showText);
    }
    
}
