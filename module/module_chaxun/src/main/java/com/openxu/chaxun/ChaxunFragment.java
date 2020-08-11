package com.openxu.chaxun;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.chaxun.adapter.ChaciHintAdapter;
import com.openxu.chaxun.bean.zhongwen.Zhongwen;
import com.openxu.chaxun.databinding.ChaxunFragmentMainChaxunBinding;
import com.openxu.core.base.XBaseFragment;
import com.openxu.core.utils.XBarUtils;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XNetworkUtils;
import com.openxu.core.utils.XSharedData;
import com.openxu.core.utils.XTimeUtils;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.db.bean.Word;
import com.openxu.db.dao.WordDao;
import com.openxu.db.manager.GreenDaoManager;
import com.openxu.pf.Pf;
import com.openxu.xftts.VoicePlayerImpl;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Author: openXu
 * Time: 2019/04/23 09:36
 * class: FanyiFragment
 * Description: 单词查询
 * Update:
 */
@Route(path = RouterPathChaxun.PAGE_FRAGMENT_CHAXUN)
public class ChaxunFragment extends XBaseFragment<ChaxunFragmentMainChaxunBinding, ChaxunFragmentVM> implements View.OnClickListener {
    private String TAG = "FragmentChaxun";

    private String clickStr = "";
    private ChaciHintAdapter hintAdapter;

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.chaxun_fragment_main_chaxun;
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
        GreenDaoManager.init(getContext());
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        //将title上面流出提示栏的高度
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) binding.llTop.getLayoutParams();
            ll.height = XBarUtils.getStatusBarHeight();
            binding.llTop.setLayoutParams(ll);
        }
        hintAdapter = new ChaciHintAdapter(getContext(),new String[]{});
        binding.lvHint.setAdapter(hintAdapter);
        binding.lvHint.setVisibility(View.GONE);
        binding.lvHint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                hideSoftInputFromWindow();
                clickStr = (String) hintAdapter.getItem(position);
                binding.etText.setText(clickStr);
                binding.etText.setSelection(clickStr.length());
                binding.lvHint.setVisibility(View.GONE);
                binding.llSearching.setVisibility(View.VISIBLE);
                binding.llContentEnglish.setVisibility(View.GONE);
                jsSearch(clickStr);
            }
        });

        binding.ivXx.setVisibility(View.INVISIBLE);
        binding.etText.setOnKeyListener(onKeyListener);
        binding.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = binding.etText.getText().toString().trim();
                if(TextUtils.isEmpty(text)){
                    binding.llSearching.setVisibility(View.GONE);
                    binding.llContentEnglish.setVisibility(View.GONE);
                    binding.llContentChina.setVisibility(View.GONE);
                    binding.ivXx.setVisibility(View.INVISIBLE);
                    binding.lvHint.setVisibility(View.GONE);
                    showHistory();
                }else{
                    binding.llHistory.setVisibility(View.GONE);
                    binding.ivXx.setVisibility(View.VISIBLE);
                    binding.llContentChina.setVisibility(View.GONE);
                    if(text.matches("[a-zA-Z]+")){
//						jsSearch(text, true, true);
                        if(!clickStr.equals(text)){
                            getWords(text);
                        }
                    }else{
                        showHistory();
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.ivXx.setOnClickListener(this);
        binding.llSearch.setOnClickListener(this);

        binding.llSearching.setVisibility(View.GONE);
        binding.llContentEnglish.setVisibility(View.GONE);
        binding.tvSearching.setOnClickListener(this);

        //中文展示区
        binding.llContentChina.setVisibility(View.GONE);
        setPf();
    }

    protected void setPf() {
    }
    @Override
    public void registObserve() {

        viewModel.wordData.observe(this, word -> showWord(word));
        viewModel.searchSuc.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean suc) {
                if(!suc){
                    XToast.warning("未查询到结果");
                    binding.tvSearching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果</font>"));
                    binding.llContentEnglish.setVisibility(View.GONE);
                    showHistory();
                }
            }
        });
        viewModel.zhongwenData.observe(this, new Observer<Zhongwen>() {
            @Override
            public void onChanged(Zhongwen zhongwen) {
                showChinaWordJS(zhongwen);
            }
        });
        viewModel.zhongwenSuc.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean suc) {
                if(!suc){
                    XToast.warning("未查询到结果");
                    binding.tvSearching.setText(Html.fromHtml("<font color=\"#a1a3a6\">未查询到结果</font>"));
                    binding.llContentChina.setVisibility(View.GONE);
                    showHistory();
                }
            }
        });

    }
    @Override
    public void initData() {
        String text = binding.etText.getText().toString().trim();
        if(TextUtils.isEmpty(text)){
            showHistory();
        }
    }
    //监听软键盘按键
    private View.OnKeyListener onKeyListener = (v, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            hideSoftInputFromWindow();
            search();
            return true;
        }
        return false;
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_search){
            search();
        }else if(v.getId() == R.id.tv_searching){
            String text = binding.tvSearching.getText().toString().trim();
            if(text.contains("..."))
                return;
            else if(text.contains(">>"))
                search();
        }else if(v.getId() == R.id.iv_xx){
            binding.etText.setText("");
        }
    }

    private void search(){
        binding.lvHint.setVisibility(View.GONE);
        binding.llSearching.setVisibility(View.VISIBLE);
        binding.llContentEnglish.setVisibility(View.GONE);

        String fromStr = binding.etText.getText().toString().trim();
        if (TextUtils.isEmpty(fromStr))
            return;
        binding.tvSearching.setText(Html.fromHtml("<font color=\"#a1a3a6\">正在查询中...</font>"));
        InputMethodManager imm = (InputMethodManager )getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(binding.etText.getApplicationWindowToken() , 0 );
        String from = "";
        String to = "";
        try {
            if(fromStr.matches("[a-zA-Z]+")){
                jsSearch(fromStr);
                return;
            }else{
                from = "zh";
                to = "en";
            }
            fromStr = URLEncoder.encode(fromStr, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.llContentEnglish.setVisibility(View.GONE);
        binding.llContentChina.setVisibility(View.GONE);
        binding.tvSearching.setText(Html.fromHtml("<font color=\"#a1a3a6\">查询中...</font>"));
        binding.llSearching.setVisibility(View.VISIBLE);
//        searchZhForBaidu(fromStr, from, to);

        viewModel.jsSearchZh(fromStr);
    }


    private void showHistory(){
        binding.llHistory.removeAllViews();
        List<String> history = getSearchHistory();
        if(history!=null&&history.size()>0){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for(final String h : history){
                View view = inflater.inflate(R.layout.item_search_history, null);
                final TextView tv_word = (TextView) view.findViewById(R.id.tv_word);
                ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteHistory(h);
                        showHistory();
                    }
                });
                tv_word.setText(h);
                tv_word.setBackgroundResource(Pf.getInstance(getContext()).search_bag);
                tv_word.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.etText.setText(tv_word.getText().toString().trim());
                        binding.etText.setSelection(tv_word.getText().toString().trim().length());
                        binding.lvHint.setVisibility(View.GONE);
                        binding.llSearching.setVisibility(View.VISIBLE);
                        binding.llContentEnglish.setVisibility(View.GONE);

                        search();
                    }
                });
                binding.llHistory.addView(view);
            }
            binding.llHistory.setVisibility(View.VISIBLE);
        }else{
            binding.llHistory.setVisibility(View.GONE);
        }
    }

    private void getWords(final String text){
        WordDao wordDao = (WordDao) GreenDaoManager.getInstance().getDao(WordDao.class);
        List<Word> words = wordDao.queryBuilder()
                .where(new WhereCondition.StringCondition(
                        WordDao.Properties.English.columnName + " LIKE \"" + text + "\" LIMIT 6"
                )).build().list();
        String[] strs = new String[words.size()];
        for(int i = 0; i<words.size(); i++){
            strs[i] = words.get(i).getEnglish();
        }
        if(strs.length>0){
            binding.llSearching.setVisibility(View.GONE);
            binding.llContentEnglish.setVisibility(View.GONE);
            binding.lvHint.setVisibility(View.VISIBLE);
            hintAdapter.setStrs(strs);
            binding.llHistory.setVisibility(View.GONE);
        }else{
            binding.lvHint.setVisibility(View.GONE);
            binding.tvSearching.setText(Html.fromHtml("<font color=\""+Pf.getInstance(getContext()).sents_en_color+"\">本地词库无结果，联网查询>></font>"));
            binding.llContentEnglish.setVisibility(View.GONE);
            binding.llSearching.setVisibility(View.VISIBLE);

            showHistory();
        }
    }

    /**
     * 英语单词（金山词霸）
     * 英语单词-->中文
     */
    private void jsSearch(final String text) {
        binding.llContentChina.setVisibility(View.GONE);
        binding.llHistory.setVisibility(View.GONE);

        viewModel.jsSearch(text);
    }

    /**
     * 展示英语单词
     * @param word
     */
    private void showWord(final Word word) {
        binding.llHistory.setVisibility(View.GONE);
        addHistory(word.getEnglish());
        try{
            binding.llSearching.setVisibility(View.GONE);
            binding.llContentEnglish.setVisibility(View.VISIBLE);
            binding.ivLove.setVisibility(View.VISIBLE);
            binding.tvEnglish.setText(word.english);   //英文
            //发音
            if(TextUtils.isEmpty(word.ph_en)&&TextUtils.isEmpty(word.ph_am)){
                binding.llPh.setVisibility(View.GONE);
                binding.ivLocalVoice.setVisibility(View.VISIBLE);
            }else{
                binding.llPh.setVisibility(View.VISIBLE);
                binding.ivLocalVoice.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(word.ph_en)){   //英式
                    binding.tvPhEn.setText(Html.fromHtml("<font color=\"#6EB0E5\">英</font> "+word.ph_en));
                    binding.tvPhEn.setVisibility(View.VISIBLE);
                    binding.ivPhEn.setVisibility(View.VISIBLE);
                }else{
                    binding.tvPhLine.setVisibility(View.GONE);
                    binding.tvPhEn.setVisibility(View.GONE);
                    binding.ivPhEn.setVisibility(View.GONE);
                }
                if(!TextUtils.isEmpty(word.ph_am)){
                    binding.tvPhAm.setText(Html.fromHtml("<font color=\"#6EB0E5\">美</font> "+word.ph_am));
                    binding.tvPhAm.setVisibility(View.VISIBLE);
                    binding.ivPhAm.setVisibility(View.VISIBLE);
                }else{
                    binding.tvPhLine.setVisibility(View.GONE);
                    binding.tvPhAm.setVisibility(View.GONE);
                    binding.ivPhAm.setVisibility(View.GONE);
                }
            }

            //中文binding.tvParts
            if(!TextUtils.isEmpty(word.parts)){
                String parts = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatParts(word.parts);
                binding.tvParts.setText(Html.fromHtml(parts));
                binding.tvParts.setVisibility(View.VISIBLE);
            }else{
                binding.tvParts.setVisibility(View.GONE);
            }
            //形式
            if(!TextUtils.isEmpty(word.exchange)){
                String exchangeStr = "<font color=\"#6EB0E5\">其他形式</font>"+"<br>" +word.exchange;
                binding.tvExchange.setText(Html.fromHtml(exchangeStr));
                binding.tvExchange.setVisibility(View.VISIBLE);
            }else{
                binding.tvExchange.setVisibility(View.GONE);
            }
            //例句binding.tvSent
            if(!TextUtils.isEmpty(word.sents)){
                String sentStr = OpenWordUtil.formatSent(Pf.getInstance(getContext()), word.sents, word.english);
                if(!TextUtils.isEmpty(sentStr)){
                    binding.tvSent.setText(Html.fromHtml(sentStr));
                    binding.tvSent.setVisibility(View.VISIBLE);
                }else
                    binding.tvSent.setVisibility(View.GONE);
            }else{
                binding.tvSent.setVisibility(View.GONE);
            }
            //本地单词发音
            binding.ivLocalVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!XNetworkUtils.isConnected()){
                        XToast.warning("请检查网络");
                        return;
                    }
                    XLog.i("使用讯飞播报："+word.getEnglish());
                    boolean isSport = VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).setVoiceMan("en");
                    VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).play(word.getEnglish());
                }
            });
            //网络单词英式
            binding.ivPhEn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voice(word.ph_en_mp3);
                }
            });
            //网络单词美式
            binding.ivPhAm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voice(word.ph_am_mp3);
                }
            });
            //单词收藏
            if(!TextUtils.isEmpty(word.love) && word.love.equals("1")){
                binding.ivLove.setImageResource(R.mipmap.love_2);  //已经收藏
            }else{
                binding.ivLove.setImageResource(R.mipmap.love_1);
            }

            binding.ivLove.setOnClickListener(v -> {
                if(!TextUtils.isEmpty(word.love) && word.love.equals("1")){
                    word.setLove("0");
                    binding.ivLove.setImageResource(R.mipmap.love_1);
                }else{
                    word.setLove("1");
                    binding.ivLove.setImageResource(R.mipmap.love_2);
                    if(!"1".equals(word.isLocal)) {
                        WordDao wordDao = (WordDao)GreenDaoManager.getInstance().getDao(WordDao.class);
                        wordDao.insert(word);
                        word.setIsLocal("1");
                    }
                }
            });

        }catch(Exception e){
        }

    }
    private void voice(final String url){
        if(!XNetworkUtils.isConnected()){
            XToast.warning("请检查网络");
            return;
        }
        new AsyncTask<Void, Void, Integer>() {
            MediaPlayer mp = new MediaPlayer();
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * 展示中文
     * @param word
     */
    private void showChinaWordBaidu(final Word word) {
        binding.llHistory.setVisibility(View.GONE);
        addHistory(word.english);
        XLog.i("中文词典："+word);
        binding.llSearching.setVisibility(View.GONE);
        binding.llContentEnglish.setVisibility(View.VISIBLE);
        binding.ivLove.setVisibility(View.GONE);
        binding.tvExchange.setVisibility(View.GONE);
        binding.tvSent.setVisibility(View.GONE);
        binding.tvEnglish.setText(word.english);   //中文
        //发音
        if(TextUtils.isEmpty(word.ph_en)){
            binding.llPh.setVisibility(View.GONE);
            binding.ivLocalVoice.setVisibility(View.GONE);
        }else{
            binding.llPh.setVisibility(View.VISIBLE);
            binding.ivLocalVoice.setVisibility(View.GONE);
            binding.tvPhEn.setText(Html.fromHtml("<font color=\"#6EB0E5\">拼音 </font> "+word.ph_en));
            binding.tvPhEn.setVisibility(View.VISIBLE);
            binding.ivPhEn.setVisibility(View.GONE);
            binding.tvPhLine.setVisibility(View.GONE);
            binding.ivPhAm.setVisibility(View.GONE);
            binding.tvPhAm.setVisibility(View.GONE);
        }
        //中文binding.tvParts
        if(!TextUtils.isEmpty(word.parts)){
            String parts = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatParts(word.parts);
            binding.tvParts.setText(Html.fromHtml(parts));
            binding.tvParts.setVisibility(View.VISIBLE);
        }else{
            binding.tvParts.setVisibility(View.GONE);
        }

    }


    static class Sent{
        public String orig;
        public String trans;
        public String getOrig() {
            return orig;
        }
        public void setOrig(String orig) {
            this.orig = orig;
        }
        public String getTrans() {
            return trans;
        }
        public void setTrans(String trans) {
            this.trans = trans;
        }
    }




    /**
     * 展示中文
     */
    private void showChinaWordJS(final Zhongwen zhongwen) {
        binding.llHistory.setVisibility(View.GONE);
        addHistory(zhongwen.word_name);
        binding.llSearching.setVisibility(View.GONE);
        binding.llContentChina.setVisibility(View.VISIBLE);
        binding.tvZhName.setText(zhongwen.word_name);
        String symbols = "<font color=\"#6EB0E5\">释义</font>"+"<br>" +OpenWordUtil.formatZhongwenSymbols(zhongwen.symbols);
        binding.tvZhSymbols.setText(Html.fromHtml(symbols));
        binding.ivZhVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLog.i("使用讯飞播报："+zhongwen.word_name);
                boolean isSport = VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).setVoiceMan("zh");
                VoicePlayerImpl.getInstance(getActivity().getApplicationContext()).play(zhongwen.word_name);
            }
        });
        addHistory(zhongwen.word_name);
        //例句binding.tvSent
        if(!TextUtils.isEmpty(zhongwen.sents)){
            String sentStr = OpenWordUtil.formatSentZh(Pf.getInstance(getContext()), zhongwen.sents, zhongwen.word_name);
            if(!TextUtils.isEmpty(sentStr)){
                binding.tvZhSent.setText(Html.fromHtml(sentStr));
                binding.tvZhSent.setVisibility(View.VISIBLE);
            }else
                binding.tvZhSent.setVisibility(View.GONE);
        }else{
            binding.tvZhSent.setVisibility(View.GONE);
        }

    }

    private int HISTORY_NUM = 20;
    private String splitHistory = ",";
    private void addHistory(String word){
        if(TextUtils.isEmpty(word))
            return;
        word = word.trim();
        String historyStr = XSharedData.getInstance(getContext()).getData("KEY_SEARCH_HOS",String.class,"");
        if(historyStr.contains(splitHistory)){
            if(historyStr.contains(word+splitHistory)){
                historyStr = historyStr.replace(word+splitHistory, "");
                historyStr = word+splitHistory+historyStr;
                XSharedData.getInstance(getContext()).saveData("KEY_SEARCH_HOS", historyStr);
                XLog.v("添加历史记录："+historyStr);
                return;
            }
        }else{
            if(word.equalsIgnoreCase(historyStr)){
                XLog.v("添加历史记录："+word);
                return;
            }
        }

        if(!TextUtils.isEmpty(historyStr)){
            String[] history = historyStr.split(splitHistory);
            int cont = 1;
            String newHistoryStr = word+splitHistory;
            for(int i = 0; i<history.length; i++){
                String h = history[i];
                if(!TextUtils.isEmpty(h)){
                    newHistoryStr += (h+splitHistory);
                    cont ++;
                    if(cont>=HISTORY_NUM)
                        break;
                }
            }
            XSharedData.getInstance(getContext()).saveData("KEY_SEARCH_HOS", historyStr);
            XLog.v("添加历史记录："+newHistoryStr);
        }else{
            XSharedData.getInstance(getContext()).saveData("KEY_SEARCH_HOS", word);
            XLog.v("添加历史记录："+word);
        }
    }
    private List<String> getSearchHistory(){
        String historyStr = XSharedData.getInstance(getContext()).getData("KEY_SEARCH_HOS",String.class,"");
        List<String> list = new ArrayList<String>();
        if(!TextUtils.isEmpty(historyStr)){
            String[] history = historyStr.split(",");
            if(history!=null&&history.length>0){
                for(String h : history){
                    if(!TextUtils.isEmpty(h))
                        list.add(h);
                }
            }
            XLog.v("展示历史记录："+list.size());
            return list;
        }else
            return null;
    }
    private void deleteHistory(String word){
        word = word.trim();
        String historyStr = XSharedData.getInstance(getContext()).getData("KEY_SEARCH_HOS",String.class,"");
        historyStr = historyStr.replaceAll(word, "");
        historyStr = historyStr.replaceAll(splitHistory+splitHistory, splitHistory);
        XSharedData.getInstance(getContext()).saveData("KEY_SEARCH_HOS", historyStr);
    }
}
