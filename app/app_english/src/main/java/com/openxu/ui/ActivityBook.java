package com.openxu.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.db.impl.GetWordDaoImpl;
import com.openxu.db.impl.WordDaoImpl;
import com.openxu.english.R;
import com.openxu.utils.BookUtils;
import com.openxu.utils.MyUtil;
import com.openxu.utils.Property;
import com.openxu.view.SelectBookDialog;
import com.openxu.view.SelectBookDialog.SelectBookListener;
import com.openxu.view.dialog.DialogTips;

/**
 * 我的词书（单词库）
 * 
 * @author openXu
 */
public class ActivityBook extends BaseActivity implements OnClickListener {

	private ListView lv_list;
	private List<Book> list;
	private MyBookAdapter adapter;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_book);
		lv_list = (ListView) findViewById(R.id.lv_booklist);
		adapter = new MyBookAdapter();
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final Book book = list.get(position);
				if (book.isDownLoad) {
					SelectBookDialog dialog = new SelectBookDialog(mContext);
					dialog.fillData(book);
					dialog.setListener(new SelectBookListener() {
						@Override
						public void selectbook() {
							MyApplication.property.setLevel(book.level);
							adapter.notifyDataSetChanged();
							finish();
						}
					});
					dialog.show();
				} else {
					// 提示下载
					DialogTips dialog = new DialogTips(mContext, book.name,
							"即将为您下载词库...", "确定", true, true);
					// 设置成功事件
					dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							downLoadDb1(book);
						}
					});
					// 显示确认对话框
					dialog.show();
					dialog = null;
				}

			}
		});

		/*
		 * if(MyApplication.property.initAd()){ // 迷你广告调用方式
		 * AppConnect.getInstance
		 * (mContext).setAdBackColor(mContext.getResources(
		 * ).getColor(MyApplication.pf.ad_bg));//设置迷你广告背景颜色
		 * AppConnect.getInstance
		 * (mContext).setAdForeColor(mContext.getResources(
		 * ).getColor(MyApplication.pf.title_bg));//设置迷你广告文字颜色 LinearLayout
		 * miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
		 * AppConnect.getInstance(mContext).showMiniAd(mContext, miniLayout,
		 * 10);// 10秒刷新一次 }
		 */

	}

	@Override
	protected void setPf() {
		super.setPf();
		lv_list.setSelector(MyApplication.getApplication().pf.item_selector);
	}

	@Override
	protected void initData() {
		list = new ArrayList<Book>();
		WordDaoImpl dao = new WordDaoImpl();
		int num = dao.getTotalCount(WordDBHelper.TABLE_WORDg);
		int remenber = dao.getTotalCount(WordDBHelper.TABLE_WORDg,WordDBHelper.BOOLEAN_TRUE);
		Book book = new Book(R.drawable.open_icon_g, Property.VALUE_LEVEL_G,Property.BOOK_NAME_G, num, 0, remenber, true, "", "");
		list.add(book);
		num = dao.getTotalCount(WordDBHelper.TABLE_WORD4);
		remenber = dao.getTotalCount(WordDBHelper.TABLE_WORD4,WordDBHelper.BOOLEAN_TRUE);
		book = new Book(R.drawable.open_icon_4, Property.VALUE_LEVEL_4,Property.BOOK_NAME_4, num, 0, remenber, true, "", "");
		list.add(book);
		num = dao.getTotalCount(WordDBHelper.TABLE_WORD6);
		remenber = dao.getTotalCount(WordDBHelper.TABLE_WORD6,WordDBHelper.BOOLEAN_TRUE);
		book = new Book(R.drawable.open_icon_6, Property.VALUE_LEVEL_6,Property.BOOK_NAME_6, num, 0, remenber, true, "", "");
		list.add(book);
		num = dao.getTotalCount(WordDBHelper.TABLE_WORD8);
		remenber = dao.getTotalCount(WordDBHelper.TABLE_WORD8,WordDBHelper.BOOLEAN_TRUE);
		book = new Book(R.drawable.open_icon_8, Property.VALUE_LEVEL_Z8,Property.BOOK_NAME_8, num, 0, remenber, true, "", "");
		list.add(book);
		GetWordDaoImpl getDao = new GetWordDaoImpl();
		List<String> downLoadList = MyApplication.property.getDownloadDb();
		for (String dl : downLoadList)
			MyUtil.LOG_I(TAG, "已经下载的词库" + dl);
		for (int i = 0; i < BookUtils.DB_TABLE_NAMES.length; i++) {
			String tableName = BookUtils.DB_TABLE_NAMES[i];
			boolean hasDownLoad = false;
			if (downLoadList != null && downLoadList.size() > 0) {
				for (String downName : downLoadList) {
					if (tableName.equalsIgnoreCase(downName))
						hasDownLoad = true;
				}
			}
			if (hasDownLoad) {
				// 已经下载
				num = getDao.getTotalCount(tableName);
				remenber = getDao.getTotalCount(tableName,WordDBHelper.BOOLEAN_TRUE);
				book = new Book(BookUtils.DB_BOOK_ICON[i],BookUtils.DB_TABLE_LEV[i], BookUtils.BOOK_NAMES[i],
						num, BookUtils.DB_SIZE[i], remenber, true, BookUtils.DB_DOWN_PATH[i],BookUtils.DB_TABLE_NAMES[i] + ".db");
			} else {
				book = new Book(BookUtils.DB_BOOK_ICON[i],BookUtils.DB_TABLE_LEV[i], BookUtils.BOOK_NAMES[i],
						BookUtils.WORD_NUM[i], BookUtils.DB_SIZE[i], 0, false,BookUtils.DB_DOWN_PATH[i], BookUtils.DB_TABLE_NAMES[i] + ".db");
			}
			list.add(book);
		}

		adapter.notifyDataSetChanged();
	}

	public class Book {
		public Book(int icon, int level, String name, int num, int size,int remb,
				boolean isDownLoad, String downPath, String dbName) {
			this.icon = icon;
			this.level = level;
			this.name = name;
			this.num = num;
			this.remb = remb;
			this.isDownLoad = isDownLoad;
			this.dbName = dbName;
			this.downPath = downPath;
			this.size = size;
		}

		public int icon, level;
		public String name;
		public int num, remb, size;
		public boolean isDownLoad;
		public String downPath, dbName;
	}

	class Holder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_pro;
		TextView tv_num;
		ImageView line;
		TextView tv_download;
	}

	class MyBookAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			Book book = list.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_book, null);
				holder = new Holder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_pro = (TextView) convertView.findViewById(R.id.tv_pro);
				holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
				holder.line = (ImageView) convertView.findViewById(R.id.line);
				holder.tv_download = (TextView) convertView.findViewById(R.id.tv_download);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.iv_icon.setImageResource(book.icon);
			String name = book.name;
			if (MyApplication.getApplication().property.level == book.level) {
				name += "<font color=\"#ff0000\"> (当前背诵)</font>";
			}
			holder.line.setVisibility(View.VISIBLE);
			holder.tv_name.setText(Html.fromHtml(name));
			holder.tv_pro.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
			holder.tv_pro.setText(MyUtil.getFloatStr(book.remb, book.num));
			holder.tv_num.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
			holder.tv_num.setText("共 " + book.num + " 词");
			if(book.size!=0){
				float size = book.size*1.0f/1024/1024;
				DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String sizeStr= decimalFormat.format(size);//format 返回的是字符串
				holder.tv_download.setText("下载词库\n("+sizeStr+" MB)");
				holder.tv_download.setTextColor(mContext.getResources().getColor(MyApplication.pf.title_bg));
			}
			if (position == list.size() - 1) {
				holder.line.setVisibility(View.INVISIBLE);
			}
			if (book.isDownLoad) {
				holder.tv_download.setVisibility(View.GONE);
			} else {
				holder.tv_download.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

	/**
	 * 下载词库
	 * 
	 * @param downurl
	 */
	private void downLoadDb(String downName, final String dbName,
			final int level) {
		dialog.setShowText("下载中...");
		dialog.show();
		BmobProFile.getInstance(mContext).download(downName,
				new DownloadListener() {
					@Override
					public void onSuccess(String fullPath) {
						MyUtil.showToast(mContext, -1, "下载成功");
						setDb(fullPath, dbName, level);
						Log.i("bmob", "下载成功：" + fullPath);
					}

					@Override
					public void onProgress(String localPath, int percent) {
						Log.i("bmob", "download-->onProgress :" + percent);
						dialog.setShowText("下载中..." + percent + "%");
					}

					@Override
					public void onError(int statuscode, String errormsg) {
						dialog.dismiss();
						MyUtil.showToast(mContext, -1, "下载失败" + errormsg);
						Log.i("bmob", "下载出错：" + statuscode + "--" + errormsg);
					}
				});
	}

	// 旧版
	private void downLoadDb1(final Book book) {
		dialog.setShowText("下载中...");
		dialog.show();
		String catchDir = mContext.getCacheDir().getAbsolutePath();
		MyUtil.LOG_I(TAG, "得到下载缓存目录：" + catchDir);
		final String fullPath = catchDir + File.separator + book.dbName;
		new AsyncTask<Void, String, Integer>() {
			
			@Override
			protected Integer doInBackground(Void... params) {
				HttpClient client = new DefaultHttpClient();
				try {
					HttpGet httpGet = new HttpGet(book.downPath);
					// 执行
					HttpResponse ressponse = client.execute(httpGet);
					int code = ressponse.getStatusLine().getStatusCode();
					if (code == HttpStatus.SC_OK) {
						InputStream in = ressponse.getEntity().getContent();
						// 设置本地保存的文件
						int all = book.size+610000;
						int conut = 0;
						MyUtil.LOG_I(TAG, "得到下载文件大小：" + all);
						FileOutputStream out = new FileOutputStream(fullPath);
						MyUtil.LOG_I(TAG, "下载数据库：" + fullPath);
						byte[] buffer = new byte[2048];
						int length;
						while ((length = in.read(buffer)) > 0) {
							out.write(buffer, 0, length);
							conut += 2048;
							MyUtil.LOG_I(TAG, "已经下载：" + conut);
							publishProgress(MyUtil.getFloatStr(conut, all));
						}
						// 最后关闭就可以了
						out.flush();
						out.close();
						in.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return 11;
				} finally {
					client.getConnectionManager().shutdown();
				}
				return 10;
			}

			protected void onProgressUpdate(String... values) {
				dialog.setShowText("下载中..." + values[0]);
			};

			protected void onPostExecute(Integer result) {
				switch (result) {
				case 10:
					MyUtil.showToast(mContext, -1, "下载完成");
					setDb(fullPath, book.dbName, book.level);
					break;
				case 11:
					dialog.dismiss();
					MyUtil.showToast(mContext, -1, "下载失败");
					break;
				}
			}
		}.execute();
	}

	private void setDb(final String srcPath, final String dbName,
			final int level) {
		dialog.setShowText("正在配置，请稍后...");
		new AsyncTask<Void, String, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				float conut = 0;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 还原数据库
				MyUtil.LOG_V(TAG, "配置数据库");
				String dbDestPath = "/data/data/" + mContext.getPackageName()
						+ "/databases";
				try {
					FileInputStream in = new FileInputStream(srcPath);
					int all = in.available();
					FileOutputStream out = new FileOutputStream(dbDestPath + File.separator + dbName);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
						conut += 1024;
						publishProgress(MyUtil.getFloatStr(conut, all));
					}
					// 最后关闭就可以了
					out.flush();
					out.close();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					return 11;
				}
				return 10;
			}

			protected void onProgressUpdate(String... values) {
				dialog.setShowText("正在配置，请稍后..." + values[0]);
			};

			protected void onPostExecute(Integer result) {
				dialog.dismiss();
				switch (result) {
				case 10:
					File file = new File(srcPath);
					file.delete();
					MyApplication.property.setDownloadDb(dbName.substring(0,dbName.lastIndexOf(".")));
					MyApplication.property.setLevel(level);
					MyUtil.LOG_V(TAG, "当前词库等级" + MyApplication.property.level);
					adapter.notifyDataSetChanged();
					finish();
					break;
				case 11:
					MyUtil.showToast(mContext, -1, "配置失败");
					break;
				}
			};
		}.execute();
	}
}
