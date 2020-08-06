package com.openxu.vedio.ui;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.core.base.XBaseFragment;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.vedio.Constant;
import com.openxu.vedio.R;
import com.openxu.vedio.RouterPathVedio;
import com.openxu.vedio.databinding.FragmentIjkplayerBinding;
import com.openxu.vedio.databinding.FragmentWebviewBinding;

import java.lang.reflect.Method;


/**
 * webview播放flv协议直播数据流，引入了flv.js
 */
@Route(path = RouterPathVedio.PAGE_WEBVIEW)
public class WebViewFragment extends XBaseFragment<FragmentWebviewBinding, XBaseViewModel> {

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_webview;
    }

    @Override
    public void initView() {

        //支持javascript
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.addJavascriptInterface(this, "webViewFragment");
        // 设置可以支持缩放
        binding.webview.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        binding.webview.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        binding.webview.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        binding.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        binding.webview.getSettings().setLoadWithOverviewMode(true);

        binding.webview.getSettings().setAllowFileAccess(true);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = binding.webview.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(binding.webview.getSettings(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webview.getSettings().setDomStorageEnabled(true);// 必须保留，否则无法播放优酷视频，其他的OK
//        binding.webview.setWebChromeClient(new MyWebChromeClient());// 重写一下，有的时候可能会出现问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.webview.getSettings().setMixedContentMode(binding.webview.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //如果不设置WebViewClient，请求会跳转系统浏览器
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                XLog.d("页面打开了");
                binding.webview.loadUrl("javascript:set_url('"+ Constant.flvPath+"')");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            /* @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                if (url.toString().contains("sina.cn")){
//                    view.loadUrl("http://ask.csdn.net/questions/178242");
                    return true;
                }

                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().contains("sina.cn")){
//                        view.loadUrl("http://ask.csdn.net/questions/178242");
                        return true;
                    }
                }

                return false;
            }*/

        });
        String url = "http://v.163.com/paike/V8H1BIE6U/VAG52A1KT.html";

        CookieManager cookieManager = CookieManager.getInstance();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("android");

        cookieManager.setCookie(url, stringBuffer.toString());
        cookieManager.setAcceptCookie(true);

//        binding.webview.loadUrl(url);
        binding.webview.loadUrl("file:///android_asset/test.html");

    }

    @JavascriptInterface
    public void helloAndroid(String msg) {
        XToast.success("js调用Android:"+msg);
    }
    private class MyWebChromeClient extends WebChromeClient {
        WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("ToVmp","onShowCustomView");
            fullScreen();

            binding.webview.setVisibility(View.GONE);
            mCallback = callback;
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            Log.i("ToVmp","onHideCustomView");
            fullScreen();
            binding.webview.setVisibility(View.VISIBLE);
            super.onHideCustomView();

        }
    }
    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i("ToVmp","横屏");
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.i("ToVmp","竖屏");
        }
    }
    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.webview.destroy();
    }
}
