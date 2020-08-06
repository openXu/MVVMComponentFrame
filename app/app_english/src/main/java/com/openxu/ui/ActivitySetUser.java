package com.openxu.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.openxu.db.impl.UserDaoImpl;
import com.openxu.english.R;
import com.openxu.utils.Constant;
import com.openxu.utils.FileUtils;
import com.openxu.utils.ImageUtil;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;
import com.openxu.utils.Property;
import com.openxu.view.ChangeUserInfoDialog;
import com.openxu.view.ChangeUserInfoDialog.Listener;
import com.openxu.view.dialog.DialogTips;

/**
 * @author openXu
 */
public class ActivitySetUser extends BaseActivity implements
		UpdatePointsNotifier {

	private RelativeLayout rl_icon;
	private ImageView iv_icon;
	private TextView tv_name, tv_mail, tv_phone;
	private TextView tv_pointnum; // 积分
	private TextView tv_logout;
	private LinearLayout ll_setsex;
	private TextView tv_sex;
	private ImageView iv_check_nan, iv_check_nv;
	private int sex;
	private UserDaoImpl dao;
	private ChangeUserInfoDialog changeDialog;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_set_user);
		TAG = "ActivitySetUser";
		rl_icon = (RelativeLayout) findViewById(R.id.rl_icon);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_pointnum = (TextView) findViewById(R.id.tv_pointnum);
		tv_mail = (TextView) findViewById(R.id.tv_mail);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_logout = (TextView) findViewById(R.id.tv_logout);
		tv_logout.setOnClickListener(this);
		tv_name.setOnClickListener(this);
		tv_mail.setOnClickListener(this);
		tv_phone.setOnClickListener(this);
		
		ll_setsex = (LinearLayout) findViewById(R.id.ll_setsex);	
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		iv_check_nan = (ImageView) findViewById(R.id.iv_check_nan);
		iv_check_nv = (ImageView) findViewById(R.id.iv_check_nv);
		iv_check_nan.setOnClickListener(this);
		iv_check_nv.setOnClickListener(this);
		
		dialog.setShowText("请稍候...");
	}

	@Override
	protected void setPf() {
		super.setPf();
		tv_logout.setBackgroundResource(MyApplication.pf.btn_selector);
	}
	
	@Override
	public void onResume() {
		// 从服务器端获取当前用户的虚拟货币.
		// 返回结果在回调函数getUpdatePoints(...)中处理
		AppConnect.getInstance(this).getPoints(this);
		super.onResume();
	}

	@Override
	protected void initData() {
		dao = new UserDaoImpl();
		if (MyApplication.user != null) {
			tv_name.setText(MyApplication.user.getUsername());
			tv_mail.setText(MyApplication.user.getEmail());
			tv_phone.setText(MyApplication.user.getMobilePhoneNumber());
			ImageLoader.getInstance().displayImage(MyApplication.user.getIcon(), iv_icon);
			if(MyApplication.user.getSexset() == 1){
				ll_setsex.setVisibility(View.GONE);
				tv_sex.setVisibility(View.VISIBLE);
				boolean nan = true;
				try{
					nan = Boolean.parseBoolean(MyApplication.user.getSex());
				}catch(Exception e){
				}
				tv_sex.setText(nan?"男":"女");
			}else{
				ll_setsex.setVisibility(View.VISIBLE);
				tv_sex.setVisibility(View.GONE);
			}
		} else
			finish();
		rl_icon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_icon:
			// Intent imageSelect = new Intent(Intent.ACTION_PICK,
			// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			// /* 取得相片后返回本画面 */
			// startActivityForResult(imageSelect, 1);
			showAlbumDialog();
			break;
		case R.id.tv_name:
			changeDialog(ChangeUserInfoDialog.ACTION_NAME);
			break;
		case R.id.iv_check_nan:
			sex=1;
			iv_check_nan.setImageResource(R.drawable.open_hf_icon_check_yes);
			iv_check_nv.setImageResource(R.drawable.open_hf_icon_check_no);
			showSexDialog();
			break;
		case R.id.iv_check_nv:
			sex=2;
			iv_check_nan.setImageResource(R.drawable.open_hf_icon_check_no);
			iv_check_nv.setImageResource(R.drawable.open_hf_icon_check_yes);
			showSexDialog();
			break;
		case R.id.tv_mail:
			changeDialog(ChangeUserInfoDialog.ACTION_EMAIL);
			break;
		case R.id.tv_phone:
			changeDialog(ChangeUserInfoDialog.ACTION_PHONE);
			break;
		case R.id.tv_logout: // 退出
			//清空本地经验
			MyApplication.property.rewardJy(0, mContext, true);
			new Thread(){
				public void run() {
					BmobUserManager.getInstance(getApplicationContext()).logout();
					BmobUser.logOut(mContext);
					MyApplication.getApplication().logout();
					UserDaoImpl dao = new UserDaoImpl();
					dao.deleteAll();
					MyApplication.user = null;
					MyApplication.property.setLevel(Property.VALUE_LEVEL_G);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					});
				};
			}.start();
			
			break;
		}
	}
	private void changeDialog(int action){
		if(changeDialog==null){
			changeDialog = new ChangeUserInfoDialog(mContext);
			changeDialog.setListener(listener);
		}
		changeDialog.setAction(action);
		changeDialog.show();
	}
	private Listener listener = new Listener() {
		@Override
		public void ok(String text) {
			switch (changeDialog.action) {
			case ChangeUserInfoDialog.ACTION_NAME:
				setName(text);
				break;
			case ChangeUserInfoDialog.ACTION_PASW:
				break;
			case ChangeUserInfoDialog.ACTION_EMAIL:
				setEmail(text);
				break;
			case ChangeUserInfoDialog.ACTION_PHONE:
				setPhone(text);
				break;
			default:
				break;
			}
		}
	};
	
	public void setName(final String name) {
		dialog.show();
		BmobUser newUser = new BmobUser();
		newUser.setUsername(name);
		newUser.update(this,MyApplication.user.getObjectId(),new UpdateListener() {
			@Override
			public void onSuccess() {
				dialog.cancel();
				MyUtil.showToast(mContext, -1, "修改成功");
				MyApplication.user.setUsername(name);
				tv_name.setText(MyApplication.user.getUsername());
				dao.updata(MyApplication.user);
			}
			@Override
			public void onFailure(int code, String msg) {
				dialog.cancel();
				if (msg.contains("already taken")) {
					//username '呵呵呵呵呵' already taken.
					MyUtil.showToast(mContext, -1, "该昵称已被占用");
				} else {
					MyUtil.showToast(mContext, -1, "保存失败：" + msg);
				}
				MyUtil.LOG_E(TAG, msg);
			}
		});
	}
	private void showSexDialog(){
		DialogTips dialog = new DialogTips(mContext,"提示","确定更改性别为 "+(sex==1?" 男 " : " 女 ")+"？\n以后将不能修改", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				setSex();
			}
		});
		dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				iv_check_nan.setImageResource(R.drawable.open_hf_icon_check_no);
				iv_check_nv.setImageResource(R.drawable.open_hf_icon_check_no);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	private void setSex(){
		MyApplication.user.setSex(sex==1?true : false);
		MyApplication.user.setSexset(1);
		dialog.show();
		MyApplication.user.update(mContext,
				MyApplication.user.getObjectId(),
				new UpdateListener() {
					@Override
					public void onSuccess() {
						dialog.cancel();
						int row = dao.updata(MyApplication.user);
						MyUtil.LOG_V(TAG, row + "修改性别成功");
						ll_setsex.setVisibility(View.GONE);
						tv_sex.setVisibility(View.VISIBLE);
						boolean nan = true;
						try{
							nan = Boolean.parseBoolean(MyApplication.user.getSex());
						}catch(Exception e){
						}
						tv_sex.setText(nan?"男":"女");
					}
					@Override
					public void onFailure(int code, String msg) {
						dialog.cancel();
						MyUtil.showToast(mContext, -1, "修改性别失败："+ msg);
						MyUtil.LOG_E(TAG, msg);
					}
				});
		
	}
	protected void setPhone(final String text) {
		dialog.show();
		BmobUser newUser = new BmobUser();
		newUser.setMobilePhoneNumber(text);
		newUser.update(this,MyApplication.user.getObjectId(),new UpdateListener() {
			@Override
			public void onSuccess() {
				dialog.cancel();
				MyUtil.showToast(mContext, -1, "绑定成功");
				MyApplication.user.setMobilePhoneNumber(text);
				tv_phone.setText(MyApplication.user.getMobilePhoneNumber());
				dao.updata(MyApplication.user);
			}
			@Override
			public void onFailure(int code, String msg) {
				dialog.cancel();
				if (msg.contains("already taken")) {
					MyUtil.showToast(mContext, -1, "该邮箱已被绑定");
				} else {
					MyUtil.showToast(mContext, -1, "保存失败：" + msg);
				}
				MyUtil.LOG_E(TAG, msg);
			}
		});
	
	}

	public void setEmail(final String email) {
		dialog.show();
		BmobUser newUser = new BmobUser();
		newUser.setEmail(email);
		newUser.update(this,MyApplication.user.getObjectId(),new UpdateListener() {
			@Override
			public void onSuccess() {
				dialog.cancel();
				MyUtil.showToast(mContext, -1, "绑定成功");
				MyApplication.user.setEmail(email);
				tv_mail.setText(MyApplication.user.getEmail());
				dao.updata(MyApplication.user);
			}
			@Override
			public void onFailure(int code, String msg) {
				dialog.cancel();
				if (msg.contains("already taken")) {
					MyUtil.showToast(mContext, -1, "该邮箱已被绑定");
				} else {
					MyUtil.showToast(mContext, -1, "保存失败：" + msg);
				}
				MyUtil.LOG_E(TAG, msg);
			}
		});
	}
	

	public void showAlbumDialog() {
		final AlertDialog albumDialog = new AlertDialog.Builder(mContext)
				.create();
		albumDialog.setCanceledOnTouchOutside(false);
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_usericon, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
		albumPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				getAvataFromAlbum();
			}
		});
		cameraPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				getAvataFromCamera();
			}
		});
	}

	String icontime = "";

	// 相册选取
	private void getAvataFromAlbum() {
		icontime = System.currentTimeMillis() + "";
		Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
		intent2.setType("image/*");
		startActivityForResult(intent2, 2);
	}

	// 拍照
	private void getAvataFromCamera() {
		File f = new File(FileUtils.getCacheDirectory(mContext, true,
				Constant.CATCH_DIR_ICON) + File.separator + Constant.USER_ICON);
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Uri uri = Uri.fromFile(f);

		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(camera, 1);
	}

	String iconUrl;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == this.RESULT_OK) {
			switch (requestCode) {
			case 1:
				String files = FileUtils.getCacheDirectory(mContext, true,
						Constant.CATCH_DIR_ICON)
						+ File.separator
						+ Constant.USER_ICON;
				MyUtil.LOG_V(TAG, "照相机返回图片：" + files);
				File file = new File(files);
				if (file.exists() && file.length() > 0) {
					Uri uri = Uri.fromFile(file);
					startPhotoZoom(uri);
				} else {
				}
				break;
			case 2:
				if (data == null) {
					return;
				}
				MyUtil.LOG_V(TAG, "相册返回图片：" + data.getData());
				startPhotoZoom(data.getData());
				break;
			case 3:
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap bitmap = extras.getParcelable("data");
						// 锟斤拷锟斤拷图片
						iconUrl = saveToSdCard(bitmap);
						MyUtil.LOG_V(TAG, "裁剪后图片：" + iconUrl);
						ImageLoader.getInstance().displayImage(
								"file:///" + iconUrl, iv_icon);
						upLoadPic(iconUrl);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 锟斤拷锟斤拷锟斤拷锟絚rop=true锟斤拷锟斤拷锟斤拷锟节匡拷锟斤拷锟斤拷Intent锟斤拷锟斤拷锟斤拷锟斤拷示锟斤拷VIEW锟缴裁硷拷
		// aspectX aspectY 锟角匡拷叩谋锟斤拷锟�
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 锟角裁硷拷图片锟斤拷锟�
		intent.putExtra("outputX", 120);
		intent.putExtra("outputY", 120);
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);// 去锟斤拷锟节憋拷
		intent.putExtra("scaleUpIfNeeded", true);// 去锟斤拷锟节憋拷
		// intent.putExtra("noFaceDetection", true);//锟斤拷锟斤拷识锟斤拷
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	public String saveToSdCard(Bitmap bitmap) {
		String files = FileUtils.getCacheDirectory(mContext, true,
				Constant.CATCH_DIR_ICON)
				+ File.separator
				+ Constant.USER_ICON_CATCH + icontime + ".jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MyUtil.LOG_I(TAG, file.getAbsolutePath());
		return file.getAbsolutePath();
	}

	/**
	 * 通过返回值来改变头像。
	 * 
	 * @param data
	 */
	private void setICon(Intent data) {
		if (!NetWorkUtil.isNetworkAvailable(this)) {
			MyUtil.showToast(mContext, R.string.no_net, "");
			return;
		}
		// content://media/external/images/media/356
		Uri uri = data.getData();
		ContentResolver resolver = mContext.getContentResolver();
		// 获取图片本地路径
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		final String img_path = actualimagecursor
				.getString(actual_image_column_index);
		MyUtil.LOG_V(TAG, "获取文件路径" + img_path);
		if (TextUtils.isEmpty(img_path)) {
			MyUtil.showToast(mContext, -1, "图像为空");
			return;
		}
		// 设置新图片
		ImageLoader.getInstance().displayImage(img_path, iv_icon);
		// iv_icon.setImageBitmap(ImageUtil.resizeImage(img_path, 100, 100));
		// <100kb图片过大
		if (ImageUtil.detectSize(img_path)) {
			upLoadPic(img_path);
		} else {
			dialog.setShowText("正在压缩图片");
			dialog.show();
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					boolean result = ImageUtil
							.reSizeAndSave(mContext, img_path);
					if (result)
						return "icon"
								+ img_path.substring(img_path.lastIndexOf("."));
					else
						return null;
				}

				protected void onPostExecute(String result) {
					dialog.hide();
					if (TextUtils.isEmpty(result)) {
						MyUtil.showToast(mContext, -1, "压缩失败");
					} else {
						// 设置图片
						String newPath = mContext.getCacheDir().getPath() + "/"
								+ result;
						try {
							FileInputStream fis = new FileInputStream(newPath);
							int size = (fis.available() / 1024);
							MyUtil.LOG_V(TAG, "上传压缩图片大小：" + size + "kb");
						} catch (Exception e) {
							e.printStackTrace();
						}
						MyUtil.LOG_V(TAG, "上传压缩图片位置：" + newPath);
						upLoadPic(newPath);
					}
				};
			}.execute();
		}
	}

	private void upLoadPic(String filePath) {
		dialog.setShowText("正在上传..."+filePath);
		dialog.show();
		BTPFileResponse response = BmobProFile.getInstance(mContext).upload(
				filePath, new UploadListener() {
					@Override
					public void onSuccess(String fileName, String url,
							BmobFile file) {
						dialog.cancel();
						MyUtil.LOG_V(TAG, "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());
						final boolean jy = TextUtils.isEmpty(MyApplication.user.getIcon())||TextUtils.isEmpty(MyApplication.user.getAvatar());
						String imageUrl = file.getUrl();
						ImageLoader.getInstance().displayImage(imageUrl, iv_icon);
						MyApplication.user.setIcon(imageUrl);
						MyApplication.user.setAvatar(imageUrl);   //聊天突图像
						MyApplication.user.update(mContext,
								MyApplication.user.getObjectId(),
								new UpdateListener() {
									@Override
									public void onSuccess() {
										dialog.cancel();
										int row = dao.updata(MyApplication.user);
										MyUtil.LOG_V(TAG, row + "上传图像成功，保存数据库："+ MyApplication.user);
										MyUtil.showToast(mContext, -1, "上传成功");
										if(jy){
											MyApplication.property.rewardJy(Constant.REWARD_JY_ICON, mContext, false);
											showRewardJyPo("上传图像", Constant.REWARD_JY_ICON);
										}
									}

									@Override
									public void onFailure(int code, String msg) {
										dialog.cancel();
										MyUtil.showToast(mContext, -1, "保存失败："+ msg);
										MyUtil.LOG_E(TAG, msg);
									}
								});

					}

					@Override
					public void onProgress(int progress) {
						dialog.cancel();
						MyUtil.LOG_V(TAG, "onProgress :" + progress);
					}

					@Override
					public void onError(int statuscode, String errormsg) {
						dialog.cancel();
						MyUtil.LOG_V(TAG, "文件上传失败：" + errormsg);
						MyUtil.showToast(mContext, -1, "上传失败" + errormsg);
						iv_icon.setImageResource(R.drawable.open_user_icon_def);
					}
				});
	}

	// 获取积分展示
	final Handler mHandler = new Handler();
	private int pointNum = -1;
	private String pointErr;
	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (tv_pointnum != null) {
				if (pointNum >= 0) {
					tv_pointnum.setText(pointNum + "积分");
				} else {
					tv_pointnum.setText("pointErr");
				}
			}
		}
	};

	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 * 
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		if (pointTotal == 0) {
			if (MyApplication.user != null) {
				// 奖励虚拟货币
				AppConnect.getInstance(this).awardPoints(
						MyApplication.user.getPoint(), this);
			}
		}
		pointNum = pointTotal;
		mHandler.post(mUpdateResults);
		if (MyApplication.user != null) {
			MyApplication.user.setPoint(pointTotal);
			MyApplication.user.update(mContext,
					MyApplication.user.getObjectId(), new UpdateListener() {
						@Override
						public void onSuccess() {
							dialog.cancel();
							new UserDaoImpl().updata(MyApplication.user);
							MyUtil.LOG_E(TAG, "同步用户积分成功" + MyApplication.user);
						}

						@Override
						public void onFailure(int code, String msg) {
							MyUtil.LOG_E(TAG, msg);
						}
					});
		}
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {
		pointErr = error;
		mHandler.post(mUpdateResults);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
