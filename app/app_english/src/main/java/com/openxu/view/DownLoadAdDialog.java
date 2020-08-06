package com.openxu.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

public class DownLoadAdDialog extends ItotemBaseDialog {

	private DownLoadAdListener listener;
	private TextView tv_title, tv_text, tv_cancle, tv_ok;

	public DownLoadAdDialog(Context context) {
		super(context, R.layout.dialog_download_ad, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		tv_title.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_ok.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_cancle.setBackgroundResource(MyApplication.pf.item_selector);
		tv_ok.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
				listener.downLoad();
			}
		});
		
	}

	public void setName(String name){
		tv_title.setText(name);
		tv_text.setText("您确定要下载这款应用吗，它将带给您超棒的体验？");
	}
	
	public void setListener(DownLoadAdListener listener) {
		this.listener = listener;
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {

	}

	public interface DownLoadAdListener {
		public abstract void downLoad();
	}

}
