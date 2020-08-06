package com.openxu.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class ImageUtil {

	public final static String TAG = "ImageUtil";

	// 使用BitmapFactory.Options的inSampleSize参数来缩放
	/*
	 * @SuppressWarnings("deprecation") public static Drawable
	 * resizeImage(String path, int width, int height) { BitmapFactory.Options
	 * options = new BitmapFactory.Options(); options.inJustDecodeBounds =
	 * true;// 不加载bitmap到内存中 File file = new File(path); if (!file.exists()) {
	 * return null; } BitmapFactory.decodeFile(path, options); int outWidth =
	 * options.outWidth; int outHeight = options.outHeight; options.inDither =
	 * false; options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	 * options.inSampleSize = 1;
	 * 
	 * if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) { int
	 * sampleSize = (outWidth / width + outHeight / height) / 2;
	 * Log.d("ImageUtil", "sampleSize = " + sampleSize); options.inSampleSize =
	 * sampleSize; } options.inJustDecodeBounds = false; return new
	 * BitmapDrawable(BitmapFactory.decodeFile(path, options)); }
	 */

	/**
	 * 按比例获取图片，拉伸成方块
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeImageBmp(String path, float width, float height) {
		// 1、判断是否存在
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}

		// 2、获取图片的宽和高
		Options opts = new Options();
		opts.inJustDecodeBounds = true; // 只加载元文件，不加载图片本身,不会产生OOM
		BitmapFactory.decodeFile(path, opts);
		int picHeight = opts.outHeight; // 拿到宽高
		int picWidth = opts.outWidth;
		// 3、获取比例值
		int ratio = 1;
		if (picWidth != 0 && picHeight != 0 && width != 0 && height != 0) {
			int ratioX = (int) ((float) picHeight / height);
			int ratioY = (int) ((float) picWidth / width);
			ratio = ratioX > ratioY ? ratioX : ratioY;// 按大比例的缩放
		}
		// 4、生成图片
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = ratio; // 确定缩放的比例
		Bitmap bmp = BitmapFactory.decodeFile(path, opts);

		if (bmp == null) {
			return null;
		}
		// 拉伸到标准长度
		Bitmap newBitmap = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		Canvas cv = new Canvas(newBitmap);
		Rect rect = new Rect(0, 0, (int) width, (int) height);
		cv.drawBitmap(bmp, null, rect, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;

	}

	/**
	 * 拉伸生成一个正方形的图片
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap CreateBoxBitmap(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int length = width > height ? width : height;
		// 拉伸到标准长度
		Bitmap newBitmap = Bitmap
				.createBitmap(length, length, Config.ARGB_8888);
		Canvas cv = new Canvas(newBitmap);
		Rect rect = new Rect(0, 0, length, length);
		cv.drawBitmap(bmp, null, rect, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;

	}

	/**
	 * 按比例获取图片，不拉伸
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeImageBmpWithoutPush(String path, float width,
			float height) {
		// 1、判断是否存在
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}

		// 2、获取图片的宽和高
		Options opts = new Options();
		opts.inJustDecodeBounds = true; // 只加载元文件，不加载图片本身,不会产生OOM
		BitmapFactory.decodeFile(path, opts);
		int picHeight = opts.outHeight; // 拿到宽高
		int picWidth = opts.outWidth;
		// 3、获取比例值
		int ratio = 1;
		if (picWidth != 0 && picHeight != 0 && width != 0 && height != 0) {
			int ratioX = (int) ((float) picHeight / height);
			int ratioY = (int) ((float) picWidth / width);
			ratio = ratioX > ratioY ? ratioX : ratioY;// 按大比例的缩放
		}
		// 4、生成图片
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = ratio; // 确定缩放的比例
		Bitmap bmp = BitmapFactory.decodeFile(path, opts);

		return bmp;

	}

	// 缩放图片
	public static Bitmap zoomImg(String img, int newWidth, int newHeight) {
		// 图片源
		Bitmap bm = BitmapFactory.decodeFile(img);
		if (null != bm) {
			return zoomImg(bm, newWidth, newHeight);
		}

		return null;
	}

	public static Bitmap zoomImg(Context context, String img, int newWidth,
			int newHeight) {
		// 图片源
		try {
			Bitmap bm = BitmapFactory.decodeStream(context.getAssets()
					.open(img));
			if (null != bm) {
				return zoomImg(bm, newWidth, newHeight);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	// 缩放图片
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	/**
	 * 保存图片到本地
	 * 
	 * @param combineBitmap
	 */
	public static void saveBitmap(Context context, String ImageName,
			Bitmap Image) {
		String path = context.getFilesDir().getAbsolutePath() + File.separator
				+ ImageName + ".png";
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		File imageFile = new File(path);

		FileOutputStream fstream;
		BufferedOutputStream bStream = null;
		try {
			fstream = new FileOutputStream(imageFile);
			bStream = new BufferedOutputStream(fstream);
			bStream.write(byteArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bStream != null) {
				try {
					bStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static int imgSize = 100;

	/**
	 * 判断大小
	 * 
	 * @param path
	 * @return kb
	 */
	public static boolean detectSize(String path) {
		File file = new File(path);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			int size = (fis.available() / 1024);
			// MailUtil.LOG_V(TAG, "大小："+size + " 符合："+(size<=imgSize));
			return size <= imgSize;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 缩放图片，并保存到本地
	public static boolean reSizeAndSave(Context context, String path) {
		// 压缩图片
		// Bitmap bitmap = ((BitmapDrawable) resizeImage2(path, 100,
		// 100)).getBitmap();
		Bitmap bitmap = resizeImage(path, 100, 100);
		// Bitmap bitmap = BitmapFactory.decodeFile(path);
		byte[] img = compressImage(bitmap);
		String fileType = path.substring(path.lastIndexOf("."));
		File file = context.getCacheDir();
		String imgPath = file.getPath() + "/" + "icon" + fileType;
		try {
			FileOutputStream out = new FileOutputStream(imgPath);
			MyUtil.LOG_D(TAG, "压缩图片位置：" + imgPath);
			out.write(img);
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// MailUtil.LOG_V(TAG, "文件大小："+(img.length/1024)+"kb");
		// 写入文件
	}

	/**
	 * 质量压缩,该方法是压缩图片的质量, 注意它不会减少图片的像素, 比方说, 你的图片是300K的, 1280*700像素的, 经过该方法压缩后,
	 * File形式的图片是在100以下, 以方便上传服务器
	 */
	private static byte[] compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		// MailUtil.LOG_V(TAG, "压缩前大小：" + baos.toByteArray().length / 1024);
		int options = 100;
		while (options > 0 && baos.toByteArray().length / 1024 > imgSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		// MailUtil.LOG_V(TAG, "压缩后大小：" + baos.toByteArray().length / 1024);
		return baos.toByteArray();
	}

	/**
	 * 返回图片宽和高
	 * 
	 * @param path
	 * @return
	 */
	public static int[] getBitmapSize(String path) {
		int[] size = { 0, 0 };
		Options options = new Options();
		options.inJustDecodeBounds = true;// 不加载bitmap到内存中
		BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		size[0] = outWidth;
		size[1] = outHeight;
		return size;
	}

	// 使用BitmapFactory.Options的inSampleSize参数来缩放
	public static Bitmap resizeImage(String path, int width, int height) {
		Options options = new Options();
		options.inJustDecodeBounds = true;// 不加载bitmap到内存中
		BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Config.ARGB_8888;
		options.inSampleSize = 1;

		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			options.inSampleSize = sampleSize;
		}

		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
		// return new BitmapDrawable(BitmapFactory.decodeFile(path, options));
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
