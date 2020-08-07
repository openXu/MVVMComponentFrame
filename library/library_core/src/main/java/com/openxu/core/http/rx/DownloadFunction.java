package com.openxu.core.http.rx;


import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XTimeUtils;
import com.openxu.core.http.data.FpcDownloadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author: openXu
 * Time: 2019/3/5 16:40
 * class: DownloadFunction
 * Description: 文件下载操作符
 */
public class DownloadFunction implements Function<ResponseBody, ObservableSource<FpcDownloadData>> {

    private File file;
    private FpcDownloadData downloadData;

    public DownloadFunction(String catchPath) {
        file = new File(catchPath);
        downloadData = new FpcDownloadData();
    }

    @Override
    public ObservableSource<FpcDownloadData> apply(ResponseBody responseBody) throws Exception {
        Observable<FpcDownloadData> observable = Observable.create(emitter -> {
//            XLog.i("开始下载：" + responseBody.string());//这个只能调用一次
//            XLog.i("开始下载：" + responseBody.contentLength());
//            XLog.i("开始下载：" + responseBody.contentType());
//            XLog.i("开始下载：" + responseBody.byteStream().available());
            float preStr = 0.0f;
            int total = 23068672;//(int)responseBody.contentLength();  TODO 获取下载文件大小
            InputStream in = null;
            FileOutputStream fos = null;
            int count = 0;
            try {
                in = responseBody.byteStream();
//                XLog.i("开始下载：" + responseBody.contentLength());
//                XLog.i("开始下载：" + responseBody.contentType());


                downloadData.setProcess(0);
                downloadData.setPercent(preStr);
                downloadData.setTotal(total);
                XLog.i("开始下载：" + XTimeUtils.date2Str(new Date(), "HH:mm:ss") + "  " + downloadData + "   " + Thread.currentThread());
                emitter.onNext(downloadData);

                fos = new FileOutputStream(file);
                byte[] b = new byte[1 * 1024 * 1024];
                int len;
                while ((len = in.read(b)) != -1) {
                    count += len;
                    downloadData.setProcess(count);
                    preStr = ((float) count / (float) downloadData.getTotal());
                    preStr = Float.isNaN(preStr) ? 0.0f : preStr;
                    downloadData.setPercent(preStr);
//                    XLog.w("下载：" + FTimeUtils.date2Str(new Date(), "HH:mm:ss") +"  "+downloadData);
                    emitter.onNext(downloadData);
                    fos.write(b, 0, len);
                }
                emitter.onComplete();
            } catch (FileNotFoundException e) {
                XLog.e("文件下载错误:" + e);
            } catch (IOException e) {
                XLog.e("文件下载错误:" + e);
            } finally {
                try {
                    if (null != in)
                        in.close();
                    if (null != fos)
                        fos.close();
                } catch (IOException e) {
                    XLog.e("文件下载错误:" + e);
                }
            }
        });
        return observable;
    }
}
