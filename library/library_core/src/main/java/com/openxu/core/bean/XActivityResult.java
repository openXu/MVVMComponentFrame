package com.openxu.core.bean;

import android.content.Intent;

/**
 * Author: openXu
 * Time:   2019/4/16 10:54
 * class:  XActivityResult
 * Description:
 * Update:
 */
public class XActivityResult {
    public int resultCode;
    public Intent intent;

    public XActivityResult(int resultCode, Intent intent) {
        this.resultCode=resultCode;
        this.intent=intent;
    }
}
