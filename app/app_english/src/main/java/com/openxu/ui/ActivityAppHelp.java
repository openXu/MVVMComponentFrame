package com.openxu.ui;

import android.text.Html;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.utils.MyUtil;

/**
 * @author openXu
 */
public class ActivityAppHelp extends BaseActivity implements OnClickListener {

	private TextView tv_help;
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_app_help);
		tv_help = (TextView) findViewById(R.id.tv_help);
	}
	
	@Override
	protected void initData() {
		MyUtil.LOG_I(TAG, "");
		tv_help.setText(Html.fromHtml(""));
	}
	
	private String TITLE_1 = "使用说明";
	private String TITLE_2 = "常见问题";
	
	private String TITLE_1_1 = "1.我的词书";
	private String TITLE_1_2 = "2.单词本";
	private String TITLE_1_3 = "3.新词任务";
	private String TITLE_1_4 = "4.复习任务";
	
	private String TITLE_1_DETAIL = "我的词书模块包含《高中词汇精选》,"
			+ "《四级核心词汇》,《六级核心词汇》,《八级核心词汇》四本词书，其中所有单词都已经包含在本地数据库中，"
			+ "不需要从网上下载。\n"
			+"当选中某本词书后，每日新词任务和每日复习任务都是从此书中抽取的。\n";
	
}
