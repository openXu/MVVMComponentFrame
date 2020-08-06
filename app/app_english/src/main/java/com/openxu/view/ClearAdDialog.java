package com.openxu.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

public class ClearAdDialog extends ItotemBaseDialog {

	private ClearAdListener listener;
	private TextView tv_title, tv_text, tv_cancle, tv_clear;

	public ClearAdDialog(Context context) {
		super(context, R.layout.dialog_clear_ad, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_clear = (TextView) findViewById(R.id.tv_clear);
		tv_title.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_clear.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_cancle.setBackgroundResource(MyApplication.pf.item_selector);
		tv_clear.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		tv_clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
				listener.clear();
			}
		});
		tv_text.setText("去除广告将会消费您"+MyApplication.property.clearPoint+
				"积分，接下来的"+MyApplication.property.clearDay+"天内使用将没有广告！");
	}
	
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	public void setText(String text){
		tv_text.setText(text);
	}
	public void setOk(String ok){
		tv_clear.setText(ok);
	}
	public void setCancle(String cancle){
		tv_cancle.setText(cancle);
	}
	public void setListener(ClearAdListener listener) {
		this.listener = listener;
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {

	}

	public interface ClearAdListener {
		public abstract void clear();
	}

}
