package com.openxu.core.crash;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.openxu.core.R;
import com.openxu.core.base.XBaseActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.databinding.CoreActivityCrashErrorDefBinding;
import com.openxu.core.utils.XFileUtils;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;

/**
 * Author: openXu
 * Time: 2019/3/14 17:00
 * class: DefaultErrorActivity
 * Description:
 */
public class DefaultErrorActivity extends XBaseActivity<CoreActivityCrashErrorDefBinding, XBaseViewModel> {
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.core_activity_crash_error_def;
    }

    @Override
    public void initView() {
        XFileUtils.writeLogFile(CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()), "errorLog");
        String hint = "抱歉，应用程序遇到一点小问题查看详情";
        SpannableString spannableString = new SpannableString(hint);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_red));
        spannableString.setSpan(foregroundColorSpan, hint.indexOf("查看详情"), hint.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AlertDialog dialog = new AlertDialog.Builder(DefaultErrorActivity.this)
                        .setTitle("错误信息")
                        .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()))
                        .setPositiveButton("关闭", null)
                        .setNeutralButton("复制到粘贴板",
                                (dialog1, which) -> copyErrorToClipboard())
                        .show();
                TextView textView = dialog.findViewById(android.R.id.message);
                if (textView != null)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_level_mid));

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, hint.indexOf("查看详情"), hint.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvMsg.setText(spannableString);
        //重启
        binding.tvRestart.setOnClickListener(v -> {
//            ARouter.getInstance().build(RouterPath.MBase.PAGE_SPLASH).navigation();
            CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, CustomActivityOnCrash.getConfigFromIntent(getIntent()));
        });
        //退出
        binding.tvClose.setOnClickListener(v -> {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        });
    }

    @Override
    public void registObserve() {
    }

    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        //Are there any devices without clipboard...?
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error information", errorInformation);
            clipboard.setPrimaryClip(clip);
            XToast.success("已复制到粘贴板");
        }
    }

    @Override
    public void initData() {
        //可以获取到的四个信息:
        //将堆栈跟踪作为字符串获取。
        String stackString = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
        XLog.d("将堆栈跟踪作为字符串获取==>" + stackString);
        //获取错误报告的Log信息
        String logString = CustomActivityOnCrash.getActivityLogFromIntent(getIntent());
        XLog.d("获取错误报告的Log信息==>" + logString);
        // 获取所有的信息
        String allString = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());
        XLog.d("获取所有的信息==>" + allString);
        //获得配置信息,比如设置的程序崩溃显示的页面和重新启动显示的页面等等信息
        CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
    }

}
