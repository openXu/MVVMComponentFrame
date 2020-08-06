package com.openxu.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;
import com.openxu.utils.MyUtil;

public class CreateBookDialog extends ItotemBaseDialog {

	private Listener listener;
	private TextView tv_title, tv_cancle, tv_ok;
	private EditText et_name;

	public CreateBookDialog(Context context) {
		super(context, R.layout.dialog_create_mybook, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		et_name = (EditText) findViewById(R.id.et_name);
		tv_title.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_ok.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
		tv_ok.setBackgroundResource(MyApplication.pf.item_selector);
		tv_cancle.setBackgroundResource(MyApplication.pf.item_selector);
		et_name.setBackgroundResource(MyApplication.pf.regist_edt_bg);
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = et_name.getText().toString().trim();
				if(TextUtils.isEmpty(name)){
					MyUtil.showToast(context, -1, "请输入单词本名称");
					return;
				}
				cancel();
				listener.create(name);
			}
		});
	}

	public void setText(String text){
		et_name.setText(text);	
	}
	public void setTitle(String text){
		tv_title.setText(text);	
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
		public abstract void create(String name);
	}

}
