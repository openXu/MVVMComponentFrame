package com.openxu.core.zxing.activity;

import java.io.Serializable;

/**
 * Created by ML on 2017/4/11.
 */

public class CaptureInfo implements Serializable {
    boolean decodeLoacal = false;
    String title;
    String info1;
    String info0;
    String info2;

    public boolean isDecodeLoacal() {
        return decodeLoacal;
    }

    public void setDecodeLoacal(boolean decodeLoacal) {
        this.decodeLoacal = decodeLoacal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo0() {
        return info0 == null ? "" : info0;
    }

    public void setInfo0(String info0) {
        this.info0 = info0;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }
}
