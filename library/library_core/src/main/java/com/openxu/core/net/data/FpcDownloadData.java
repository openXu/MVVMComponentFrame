package com.openxu.core.net.data;

/**
 * Author: openXu
 * Time: 2019/4/11 11:17
 * class: FpcDownloadData
 * Description: 文件下载回调数据对象
 */
public class FpcDownloadData {

    private int process;
    private int total;
    private float percent;

    @Override
    public String toString() {
        return "FpcDownloadData{" +
                "process=" + process +
                ", total=" + total +
                ", percent=" + percent +
                '}';
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
