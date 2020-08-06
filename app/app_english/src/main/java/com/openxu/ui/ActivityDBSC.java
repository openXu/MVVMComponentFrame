package com.openxu.ui;

import java.io.File;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;

/**
 * 用于上传数据库。供开发者使用
 * 专业四级单词库下载地址：http://file.bmob.cn/M02/CA/C9/oYYBAFZdElOAACe5AB6IAAF9Bz48897.db
 * 
 * 
 * 
 * @author aa
 * 
 */
public class ActivityDBSC extends BaseActivity {

	private EditText et_name;
	private TextView tv_commit;
	// /storage/emulated/0/haojicidian/icon/USER_ICON_CATCH1448008129188.jpg
	private String filePath = "/storage/emulated/0/haojicidian/";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_dbsc);
		et_name = (EditText) findViewById(R.id.et_name);
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
	}

	protected void setPf() {
		super.setPf();
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_commit:
			commit1();
			break;
		default:
			break;
		}
	}

	private void commit() {
		String name = et_name.getText().toString().trim();
		if (TextUtils.isEmpty(name))
			return;
		String path = filePath + name;
		dialog.setShowText("正在上传..." + path);
		dialog.show();
		BTPFileResponse response = BmobProFile.getInstance(mContext).upload(
				filePath, new UploadListener() {
					@Override
					public void onSuccess(String fileName, String url,
							BmobFile file) {
						dialog.cancel();
						MyUtil.LOG_V(TAG, "文件上传成功：" + fileName + ",可访问的文件地址："
								+ file.getUrl());
					}

					@Override
					public void onProgress(int progress) {
						MyUtil.LOG_V(TAG, "onProgress :" + progress);
					}

					@Override
					public void onError(int statuscode, String errormsg) {
						dialog.cancel();
						MyUtil.LOG_V(TAG, "数据库上传失败：" + errormsg);
						MyUtil.showToast(mContext, -1, "数据库上传失败" + errormsg);
					}
				});

	}

	private void commit1() {
		String name = et_name.getText().toString().trim();
		if (TextUtils.isEmpty(name))
			return;
		String path = filePath + name;
		dialog.setShowText("正在上传..." + path);
		dialog.show();
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.uploadblock(mContext, new UploadFileListener() {

		    @Override
		    public void onSuccess() {
		    	dialog.cancel();
		    	 //bmobFile.getUrl()---返回的上传文件的地址（不带域名）
		        //bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
		    	//上传文件成功:http://file.bmob.cn/M02/D0/E9/oYYBAFZfzFuAZN0LAB6IACXHJL81393.db
		       MyUtil.LOG_V(TAG, "上传文件成功:" + bmobFile.getFileUrl(mContext));
		    }

		    @Override
		    public void onProgress(Integer value) {
		        // 返回的上传进度（百分比）
		    	MyUtil.LOG_V(TAG, "onProgress :" + value);
		    	dialog.setShowText("正在上传..." + value+"%");
		    }

		    @Override
		    public void onFailure(int code, String msg) {
		        dialog.cancel();
				MyUtil.LOG_V(TAG, code+"数据库上传失败：" + msg);
				MyUtil.showToast(mContext, -1, code+"数据库上传失败" + msg);
		    }
		});
	}
}
