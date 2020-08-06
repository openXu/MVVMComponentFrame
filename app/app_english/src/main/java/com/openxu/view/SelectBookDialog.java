package com.openxu.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.ActivityBook.Book;
import com.openxu.ui.MyApplication;
import com.openxu.utils.BookUtils;
import com.openxu.utils.Constant;
import com.openxu.utils.Property;

public class SelectBookDialog extends ItotemBaseDialog {

	private SelectBookListener listener;
	private ImageView iv_icon;
	private TextView tv_name, tv_num, tv_detail, tv_go;

	public SelectBookDialog(Context context) {
		super(context, R.layout.dialog_select_book, R.style.ItotemTheme_Dialog);
	}

	@Override
	protected void initView() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_go = (TextView) findViewById(R.id.tv_go);
		tv_go.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener!=null){
					listener.selectbook();
					cancel();
				}
			}
		});
		tv_go.setBackgroundResource(MyApplication.getApplication().pf.btn_selector);
	}

	public void fillData(Book book){
		String detail = BookUtils.getBookDetail(book);
		iv_icon.setImageResource(book.icon);
		tv_name.setText(book.name);
		tv_num.setText("共"+book.num+"词");
		tv_detail.setText(detail);
	}
	
	public void setListener(SelectBookListener listener) {
		this.listener = listener;
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {

	}

	public interface SelectBookListener {
		public abstract void selectbook();
	}

}
