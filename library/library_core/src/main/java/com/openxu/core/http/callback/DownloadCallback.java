package com.openxu.core.http.callback;


import com.openxu.core.http.data.FpcDownloadData;

/**
 * Author: openXu
 * Time: 2019/3/11 9:27
 * class: DownloadCallback
 * Description: 下载文件回调
 *
 *
 */
public abstract class DownloadCallback extends BaseCallback{
    public void onStart() {
    }
    public void onProgress(FpcDownloadData download) {
    }

}
