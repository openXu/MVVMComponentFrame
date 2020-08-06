package com.openxu.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

public class DeleteMywordDialog extends ItotemBaseDialog {

	private Listener listener;
	private TextView tv_delete, tv_cancel;

	public DeleteMywordDialog(Context context) {
		super(context, R.layout.dialog_delete_myword, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		
		tv_delete.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_cancel.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_delete.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancel.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		tv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
				listener.click(1);
			}
		});
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {

	}

	public interface Listener {
		public abstract void click(int witch);
	}

}
