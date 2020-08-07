package com.openxu.fanyi;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.core.base.XBaseFragment;
import com.openxu.core.utils.XBarUtils;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XMD5Utils;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.fanyi.adapter.MyspinnerAdapter;
import com.openxu.fanyi.bean.Fanyi;
import com.openxu.fanyi.databinding.FanyiFragmentMainFanyiBinding;
import com.openxu.pf.PfConstant;
import com.openxu.pf.Pf;
import com.openxu.xftts.VoicePlayerImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Author: openXu
 * Time: 2019/04/23 09:36
 * class: FanyiFragment
 * Description: 翻译
 * Update:
 */
@Route(path = RouterPathFanyi.PAGE_FRAGMENT_FANYI)
public class FanyiFragment extends XBaseFragment<FanyiFragmentMainFanyiBinding, FanyiFragmentVM> implements View.OnClickListener {

    private String[] types;
    private Map<String, String> typeMap;
    private LinearLayout drawdownlayout;
    private ListView listView;
    private MyspinnerAdapter adapter;
    private PopupWindow popupWindow;

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fanyi_fragment_main_fanyi;
    }

    @Override
    public void initTitleView() {
        super.initTitleView();
        //将title上面流出提示栏的高度
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) binding.llTop.getLayoutParams();
            ll.height = XBarUtils.getStatusBarHeight();
            binding.llTop.setLayoutParams(ll);
        }
    }

    @Override
    public void initView() {
        binding.llChina.setVisibility(View.INVISIBLE);
        binding.tvType.setOnClickListener(this);
        binding.llGo.setOnClickListener(this);
        binding.llVoice.setOnClickListener(this);
        binding.llCopy.setOnClickListener(this);
        binding.llShare.setOnClickListener(this);
        binding.llClear.setOnClickListener(this);

        binding.etText.setOnKeyListener(onKeyListener);
        binding.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(binding.etText.getText().toString().trim()))
                    binding.llClear.setVisibility(View.INVISIBLE);
                else
                    binding.llClear.setVisibility(View.VISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        setPf();
    }

    protected void setPf() {
        binding.tvType.setBackgroundResource(Pf.getInstance(getContext()).item_selector);
    }
    public void setAd(){
        XLog.i("翻译fragment加广告");
        binding.miniAdLinearLayout3.setVisibility(View.GONE);
    }
    @Override
    public void registObserve() {
    }
    @Override
    public void initData() {
        types = getResources().getStringArray(R.array.fanyiType);
        adapter = new MyspinnerAdapter(getContext(), null, types , MyspinnerAdapter.TYPE_2);
        binding.tvType.setText((CharSequence) adapter.getItem(0));
        typeMap = new HashMap<String, String>();
        typeMap.put("中文", "zh");
        typeMap.put("粤语", "yue");
        typeMap.put("英语", "en");
        typeMap.put("日语", "jp");
        typeMap.put("韩语", "kor");
        typeMap.put("西班牙语", "spa");
        typeMap.put("法语", "fra");
        typeMap.put("泰语", "th");
        typeMap.put("阿拉伯语", "ara");
        typeMap.put("俄罗斯语", "ru");
        typeMap.put("葡萄牙语", "pt");
        typeMap.put("文言文", "wyw");
        typeMap.put("白话文", "zh");
        typeMap.put("德语", "de");
        typeMap.put("意大利语", "it");
        typeMap.put("荷兰语", "nl");
        typeMap.put("希腊语", "el");

    }
    //监听软键盘按键
    private View.OnKeyListener onKeyListener = (v, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            String fromStr = binding.etText.getText().toString().trim();
            if (TextUtils.isEmpty(fromStr))
                return true;
            /* 隐藏软键盘 */
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        v.getApplicationWindowToken(), 0);
            }
//				fanyi(fromStr);
            fanyi_new(fromStr);
            return true;
        }
        return false;
    };

    @Override
    public void onClick(View v) {
        String text = "";
        if(v.getId() == R.id.tv_type){
            showWindow(binding.tvType, binding.tvType);
        }else if(v.getId() == R.id.ll_go){
            // 翻译
            String fromStr = binding.etText.getText().toString().trim();
            if (TextUtils.isEmpty(fromStr))
                return;
            InputMethodManager imm = (InputMethodManager )v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken() , 0 );
//			fanyi(fromStr);
            fanyi_new(fromStr);
        }else if(v.getId() == R.id.ll_voice){
            text = binding.tvText.getText().toString().trim();
            if(TextUtils.isEmpty(text))
                return;
            XLog.i("读取翻译结果："+to);
            boolean isSport = VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).setVoiceMan(to);
            if(isSport){
                VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).play(text);
            }else{
                XToast.error("抱歉~目前不支持"+to_zh+"发音");
            }
        }else if(v.getId() == R.id.ll_copy){
            text = binding.tvText.getText().toString().trim();
            if(TextUtils.isEmpty(text))
                return;
            // 复制
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                int version = Integer.valueOf(Build.VERSION.SDK);
                if (version >= 11) {
                    Method method = cmb.getClass().getMethod("setText",CharSequence.class);
                    method.invoke(cmb, text);
                    XToast.success("已复制到剪切板");
                } else {
                    XToast.error("您的系统不支持此功能");
                }
            } catch (Exception e) {
            }
        }else if(v.getId() == R.id.ll_share){
            text = binding.tvText.getText().toString().trim();
            if(TextUtils.isEmpty(text))
                return;
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "选择要使用的应用"));
        }else if(v.getId() == R.id.ll_clear){
            binding.etText.setText("");
            binding.etText.setText("");
            binding.llClear.setVisibility(View.INVISIBLE);
        }
    }

    private void showWindow(View view, final TextView txt) {
        drawdownlayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.fanyi_myspinner_dropdown, null);
        listView = (ListView) drawdownlayout.findViewById(R.id.listView);
        listView.setBackgroundResource(Pf.getInstance(getActivity().getApplicationContext()).item_selector);
        listView.setAdapter(adapter);
        listView.setSelector(Pf.getInstance(getActivity().getApplicationContext()).item_selector);
        popupWindow = new PopupWindow(view);
        // 设置弹框的宽度为布局文件的宽
        popupWindow.setWidth(binding.tvType.getWidth());
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击弹框外部，弹框消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(drawdownlayout);
        // 设置弹框出现的位置，在v的正下方横轴偏移textview的宽度，为了对齐~纵轴不偏移
        popupWindow.showAsDropDown(view, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
            @Override
            public void onDismiss() {
//				spinnerlayout.setBackgroundResource(R.drawable.preference_single_item);
            }
        });
        // listView的item点击事件
        listView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            txt.setText(types[arg2]);// 设置所选的item作为下拉框的标题
            // 弹框消失
            popupWindow.dismiss();
            popupWindow = null;
        });
    }


    /**
     * 百度翻译API服务全新升级至百度翻译开放云平台
     * 支持全球27个语种的高质量互译服务
     * 新平台服务更稳定，更可靠，SLA高达99.99%
     * 单次请求字符长度达100万，长文本不必再做字符截断
     * 迁移步骤简单，接口管理更有序
     * @param str
     */
    private void fanyi_new(String str) {
        binding.llChina.setVisibility(View.VISIBLE);
        binding.tvYw.setText("正在进行翻译,请稍后...");
        binding.tvText.setText("");
        String type = binding.tvType.getText().toString();
        try {
            if("语言自动检测".equals(type)){
                from = "auto";
                to = "auto";
            }else{
                from = type.substring(0, type.indexOf("-")).trim();
                to_zh = type.substring(type.indexOf(">")+1).trim();
                from = typeMap.get(from);
                to = typeMap.get(to_zh);
            }
//			q = URLEncoder.encode(q, "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //1、将请求参数 APPID (appid), 翻译query(q), 随机数(salt), 按照 appid q salt的顺序拼接得到串1。
        Random random = new Random();
        int salt = random.nextInt(1000000000);
        String sign = FanyiConstant.APP_ID+str+salt;
        XLog.v(sign);
        //2、在串1后拼接由平台分配的私钥(secret key) 得到串2。
        sign += FanyiConstant.KEY_P;
        XLog.v(sign);
        //3、对串2做md5，得到sign。
        sign = XMD5Utils.md5(sign);
        XLog.v(sign);
        //http://api.fanyi.baidu.com/api/trans/vip/translate?q=hello&appid=2015063000000001&salt=1435660288&from=en&to=zh&sign=2f7b6bfb034a64a978707bd303d20cce
        String urlStr = FanyiConstant.URL_BAIDU_FANYI_NEW +"&salt="+salt+"&sign="+sign+ "&q=" + str+ "&from="+from+"&to="+to;
        Map<String, String> params = new HashMap<>();
        params.put("appid", FanyiConstant.APP_ID);
        params.put("salt", salt+"");
        params.put("sign", sign);
        params.put("q", str);
        params.put("from", from);
        params.put("to", to);
        viewModel.fanyi(FanyiConstant.URL_BAIDU_FANYI_NEW, params);
//        viewModel.fanyi(type, from, to, to_zh);
    }
    private String from = "";
    private String to = "";
    private String to_zh = "";

    private void showFanyi(List<Fanyi> fanyis) {
        if (fanyis != null && fanyis.size() > 0) {
            String china = "";
            if(fanyis.size()>1)
                for(Fanyi fanyi : fanyis)
                    china += (fanyi.getDst()+"\n");
            else
                china = fanyis.get(0).getDst();
            XLog.e( "翻译结果：" + china);
            binding.tvText.setText(china);
        }
    }
}
