package com.openxu.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;
import com.openxu.view.DrawZoomImageView;
import com.openxu.view.PrograsDialog;
import com.openxu.view.DrawZoomImageView.ModeEnum;

/**
 * 图片编辑
 * @author openXu
 *
 */
public class ActivityTyImage extends Activity implements OnClickListener {

	private DrawZoomImageView iv_photo;
	private RelativeLayout rl_contrl; // 编辑
	private ImageView iv_result;      // 预览
	private LinearLayout ll_cx, ll_xp, ll_color1, ll_color2, ll_color3, ll_color4, ll_color5, ll_color6;
	private ImageView iv_xp;

	private TextView tv_cancel, tv_see, tv_send;
	protected PrograsDialog dialog;
	private Context mContext;
	private String TAG;
	private String imagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		TAG = "ActivityTyImage";
		MyApplication.getApplication().addActivity(this);
		initView();
		setPf();
		initData();
	}

	protected void initView() {
		setContentView(R.layout.activity_ty_image);
		iv_photo = (DrawZoomImageView) findViewById(R.id.iv_photo);
		rl_contrl = (RelativeLayout) findViewById(R.id.rl_contrl);
		rl_contrl.setVisibility(View.VISIBLE);
		iv_result = (ImageView) findViewById(R.id.iv_result);
		iv_result.setVisibility(View.GONE);

		ll_cx = (LinearLayout) findViewById(R.id.ll_cx);
		iv_xp = (ImageView) findViewById(R.id.iv_xp);
		ll_xp = (LinearLayout) findViewById(R.id.ll_xp);
		ll_color1 = (LinearLayout) findViewById(R.id.ll_color1);
		ll_color2 = (LinearLayout) findViewById(R.id.ll_color2);
		ll_color3 = (LinearLayout) findViewById(R.id.ll_color3);
		ll_color4 = (LinearLayout) findViewById(R.id.ll_color4);
		ll_color5 = (LinearLayout) findViewById(R.id.ll_color5);
		ll_color6 = (LinearLayout) findViewById(R.id.ll_color6);

		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_see = (TextView) findViewById(R.id.tv_see);
		tv_send = (TextView) findViewById(R.id.tv_send);

		ll_cx.setOnClickListener(this);
		ll_xp.setOnClickListener(this);
		ll_color1.setOnClickListener(this);
		ll_color2.setOnClickListener(this);
		ll_color3.setOnClickListener(this);
		ll_color4.setOnClickListener(this);
		ll_color5.setOnClickListener(this);
		ll_color6.setOnClickListener(this);

		tv_cancel.setOnClickListener(this);
		tv_see.setOnClickListener(this);
		tv_send.setOnClickListener(this);

		setTyColor(getResources().getColor(R.color.image_edit_c1), 1);
		
		dialog = new PrograsDialog(mContext);
	}

	protected void setPf() {
		tv_cancel.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
		tv_see.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
		tv_send.setTextColor(getResources().getColor(MyApplication.pf.title_bg));
	}

	protected void initData() {
		imagePath = getIntent().getStringExtra("imagePath");
		MyUtil.LOG_V(TAG, "源图地址:" + imagePath);
		if (TextUtils.isEmpty(imagePath)) {
			MyUtil.showToast(mContext, -1, "图片不存在，请重新选择");
			finish();
		} else {
			Bitmap bitmap = readBitmapAutoSize(imagePath, iv_photo.getWidth(), iv_photo.getHeight());
			iv_photo.setImageBitmap(bitmap);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			try {
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				String path = cursor.getString(column_index);
				Bitmap bitmap = readBitmapAutoSize(path, iv_photo.getWidth(),
						iv_photo.getHeight());
				iv_photo.setImageBitmap(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_cx: // 撤销
			iv_photo.revoke();
			break;
		case R.id.ll_xp: // 橡皮
			setTyColor(getResources().getColor(R.color.image_edit_c1), 0);
			iv_photo.setMode(ModeEnum.XP);
			iv_xp.setImageResource(R.drawable.image_edit_xp2);
			break;
		case R.id.ll_color1: // 颜色
			setTyColor(getResources().getColor(R.color.image_edit_c1), 1);
			break;
		case R.id.ll_color2:
			setTyColor(getResources().getColor(R.color.image_edit_c2), 2);
			break;
		case R.id.ll_color3:
			setTyColor(getResources().getColor(R.color.image_edit_c3), 3);
			break;
		case R.id.ll_color4:
			setTyColor(getResources().getColor(R.color.image_edit_c4), 4);
			break;
		case R.id.ll_color5:
			setTyColor(getResources().getColor(R.color.image_edit_c5), 5);
			break;
		case R.id.ll_color6:
			setTyColor(getResources().getColor(R.color.image_edit_c6), 6);
			break;
		case R.id.tv_cancel: // 取消
			finish();
			break;
		case R.id.tv_see: // 预览
			setState();
			break;
		case R.id.tv_send: // 发送
			sendImage();
			break;
		}
	}

	private void setState(){
		if (tv_see.getText().toString().trim().contains("预览")) {
			tv_see.setText("编辑");
			rl_contrl.setVisibility(View.GONE);
			iv_result.setVisibility(View.VISIBLE);
			Bitmap bitmap = iv_photo.getImageBitmap();
			iv_result.setImageBitmap(bitmap);
		} else if (tv_see.getText().toString().trim().contains("编辑")) {
			tv_see.setText("预览");
			iv_result.setVisibility(View.GONE);
			rl_contrl.setVisibility(View.VISIBLE);
		}
	}
	
	private void sendImage() {
		if(!iv_photo.hasEdit()){
			//没有编辑
			Intent data = new Intent();
			data.putExtra("imagePath", imagePath);
			setResult(RESULT_OK, data);
			finish();
		}else{
			dialog.setShowText("正在生成图片...");
			dialog.show();
			new AsyncTask<Void, Void, Integer>() {
				String newImagePath = "";
				@Override
				protected Integer doInBackground(Void... params) {
					Bitmap bitmap = iv_photo.getImageBitmap();
					String destPath = Constant.CATCH_DIR;
					File destFile = new File(destPath);
					if (!destFile.exists()) {
						boolean ok = destFile.mkdir();
					}
					destPath = Constant.CATCH_DIR_SEND_IMAGE;
					destFile = new File(destPath);
					if (!destFile.exists()) {
						boolean ok = destFile.mkdir();
					}
					String name = System.currentTimeMillis()+imagePath.substring(imagePath.lastIndexOf("."),imagePath.length());
					newImagePath = destPath + File.separator + name;
					File f = new File(newImagePath);
					if (f.exists()) {
						f.delete();
					}
					MyUtil.LOG_V(TAG, "保存图片:" + newImagePath);
					try {
						FileOutputStream out = new FileOutputStream(f);
						bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
						out.flush();
						out.close();
						bitmap = null;
						System.gc();
						MyUtil.LOG_V(TAG, "已经保存");
						return 1;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return 0;
				}
				
				protected void onPostExecute(Integer result) {
					dialog.cancel();
					if(result == 1){
						Intent data = new Intent();
						data.putExtra("imagePath", newImagePath);
						setResult(RESULT_OK, data);
						finish();
					}else{
						MyUtil.showToast(mContext, -1, "保存图片失败");
					}
				};
			}.execute();
			
		}
	}

	private void setTyColor(int color, int witch) {
		MyUtil.LOG_V(TAG, "切换画笔颜色" + witch);
		iv_photo.setMode(ModeEnum.TY);
		iv_photo.setTyColor(color);
		iv_xp.setImageResource(R.drawable.image_edit_xp1);
		ll_color1.setBackgroundColor(Color.TRANSPARENT);
		ll_color2.setBackgroundColor(Color.TRANSPARENT);
		ll_color3.setBackgroundColor(Color.TRANSPARENT);
		ll_color4.setBackgroundColor(Color.TRANSPARENT);
		ll_color5.setBackgroundColor(Color.TRANSPARENT);
		ll_color6.setBackgroundColor(Color.TRANSPARENT);
		switch (witch) {
		case 1:
			ll_color1.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		case 2:
			ll_color2.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		case 3:
			ll_color3.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		case 4:
			ll_color4.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		case 5:
			ll_color5.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		case 6:
			ll_color6.setBackgroundResource(R.drawable.image_edit_color_round);
			break;
		}
	}

	public Bitmap readBitmapAutoSize(String filePath, int outWidth,
			int outHeight) {
		// outWidth和outHeight是目标图片的最大宽度和高度，用作限制
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);
			BitmapFactory.Options options = setBitmapOption(filePath, outWidth,
					outHeight);
			return BitmapFactory.decodeStream(bs, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private BitmapFactory.Options setBitmapOption(String file, int width,
			int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		// 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
		BitmapFactory.decodeFile(file, opt);
		int outWidth = opt.outWidth; // 获得图片的实际高和宽
		int outHeight = opt.outHeight;
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
		opt.inSampleSize = 2;
		// 设置缩放比,1表示原比例，2表示原来的四分之一....
		// 计算缩放比
		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			opt.inSampleSize = sampleSize;
		}
		opt.inJustDecodeBounds = false;// 最后把标志复原
		return opt;
	}

	@Override
	public void onBackPressed() {
		if (tv_see.getText().toString().trim().contains("预览")) {
			super.onBackPressed();
		} else if (tv_see.getText().toString().trim().contains("编辑")) {
			tv_see.setText("预览");
			iv_result.setVisibility(View.GONE);
			rl_contrl.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		MyApplication.getApplication().removeActivity(this);
		if(dialog!=null)
			dialog.cancel();
		super.onDestroy();
	}
}
