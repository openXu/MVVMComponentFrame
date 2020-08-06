package com.openxu.ui;

import android.view.View;
import android.widget.ImageView;

import com.openxu.english.R;

/**
 * @author openXu
 */
public class ActivitySetMsg extends BaseActivity {

	private ImageView iv_v1, iv_v2;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_msg);
		TAG = "ActivitySetMsg";

		iv_v1 = (ImageView) findViewById(R.id.iv_v1);
		iv_v1.setOnClickListener(this);
		if(MyApplication.property.voice){
			iv_v1.setImageResource(R.drawable.on);
		}else{
			iv_v1.setImageResource(R.drawable.off);
		}
		iv_v2 = (ImageView) findViewById(R.id.iv_v2);
		iv_v2.setOnClickListener(this);
		if(MyApplication.property.vibrate){
			iv_v2.setImageResource(R.drawable.on);
		}else{
			iv_v2.setImageResource(R.drawable.off);
		}
	}

	@Override
	protected void initData() {}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_v1:
			if(MyApplication.property.voice){
				iv_v1.setImageResource(R.drawable.off);
				MyApplication.property.setVoice(false);
			}else{
				iv_v1.setImageResource(R.drawable.on);
				MyApplication.property.setVoice(true);
			}
			break;
		case R.id.iv_v2:
			if(MyApplication.property.vibrate){
				iv_v2.setImageResource(R.drawable.off);
				MyApplication.property.setVibrate(false);
			}else{
				iv_v2.setImageResource(R.drawable.on);
				MyApplication.property.setVibrate(true);
			}
			break;
		}
	}

}
