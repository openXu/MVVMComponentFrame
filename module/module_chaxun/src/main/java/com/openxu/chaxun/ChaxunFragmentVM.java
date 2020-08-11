package com.openxu.chaxun;

import android.app.Application;
import android.text.TextUtils;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openxu.chaxun.bean.OpenPart;
import com.openxu.chaxun.bean.OpenSent;
import com.openxu.chaxun.bean.OpenSymbol;
import com.openxu.chaxun.bean.zhongwen.Mean;
import com.openxu.chaxun.bean.zhongwen.Part;
import com.openxu.chaxun.bean.zhongwen.Symbol;
import com.openxu.chaxun.bean.zhongwen.Zhongwen;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.http.NetworkManager;
import com.openxu.core.http.callback.ResponseCallback;
import com.openxu.core.http.data.XResponse;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.XTimeUtils;
import com.openxu.db.bean.Word;
import com.openxu.db.dao.WordDao;
import com.openxu.db.manager.GreenDaoManager;

import org.greenrobot.greendao.query.WhereCondition;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Author: openXu
 * Time: 2019/04/23 09:36
 * class: HomeFragmentVM
 * Description:
 * Update:
 */
public class ChaxunFragmentVM extends XBaseViewModel {

    public MutableLiveData<Word> wordData = new MutableLiveData<>();
    public MutableLiveData<Boolean> searchSuc = new MutableLiveData<>();
    public MutableLiveData<Zhongwen> zhongwenData = new MutableLiveData<>();
    public MutableLiveData<Boolean> zhongwenSuc = new MutableLiveData<>();

    public ChaxunFragmentVM(@NonNull Application application) {
        super(application);
    }


    public void jsSearch(String text){
        WordDao wordDao = (WordDao)GreenDaoManager.getInstance().getDao(WordDao.class);
        List<Word> words = wordDao.queryBuilder()
                .where(new WhereCondition.StringCondition(
                        WordDao.Properties.English.columnName + " = \"" + text + "\""
                )).build().list();
        XLog.d("数据库中查询单词："+words);
        if(words!=null&&words.size()>0){
            wordData.setValue(words.get(0));
            return;
        }
        final Word[] word = new Word[1];
        NetworkManager.getInstance().build()
                .viewModel(this)   //ViewModel对象
                .url(ChaxunConstant.URL_JS_CIDIAN + "&w=" + text)
                .showDialog(false)
                .doGet(new ResponseCallback<XResponse>() {
                    @Override
                    public void onSuccess(XResponse result){
                        word[0] = new Word();
                        String wordStr = result.getResult();
                        JSONObject json = JSON.parseObject(wordStr);
                        XLog.d("JSON:"+wordStr);
                        word[0].setFirst(text.substring(0, 1).toUpperCase());   //首字母
                        word[0].setEnglish(json.getString("word_name"));        //英文
//					word.setLev(MyApplication.property.tableName);       //级数
                        word[0].setIsLocal("0");  //是否本地
                        word[0].setAddDate(XTimeUtils.date2Str(new Date(), XTimeUtils.DATE));
                        word[0].setRemenber("0");//是否记住
                        word[0].setDate("");     //日期
                        //复数： goes 过去式： went 过去分词： gone 现在分词： going 第三人称单数： goes
                        String exchange  = "";
                        if(json.containsKey("exchange")){
                            JSONObject exchangeJson = JSON.parseObject(json.getString("exchange"));
                            XLog.i("形式："+json.getString("exchange"));
                            if(exchangeJson.containsKey("word_pl")){
                                String word_pl = exchangeJson.getString("word_pl");
                                if(!TextUtils.isEmpty(word_pl)){
                                    exchange += ("复数:"+word_pl+",");
                                }
                            }
                            if(exchangeJson.containsKey("word_past")){
                                String word_past = exchangeJson.getString("word_past");
                                if(!TextUtils.isEmpty(word_past)){
                                    exchange += ("过去式:"+word_past+",");
                                }
                            }
                            if(exchangeJson.containsKey("word_done")){
                                String word_done = exchangeJson.getString("word_done");
                                if(!TextUtils.isEmpty(word_done)){
                                    exchange += ("过去分词:"+word_done+",");
                                }
                            }
                            if(exchangeJson.containsKey("word_ing")){
                                String word_ing = exchangeJson.getString("word_ing");
                                if(!TextUtils.isEmpty(word_ing)){
                                    exchange += ("现在分词:"+word_ing+",");
                                }
                            }
                            if(exchangeJson.containsKey("word_third")){
                                String word_third = exchangeJson.getString("word_third");
                                if(!TextUtils.isEmpty(word_third)){
                                    exchange += ("第三人称单数:"+word_third+",");
                                }
                            }
                            if(exchange.endsWith(",")){
                                exchange = exchange.substring(0, exchange.lastIndexOf(","));
                            }
                            XLog.i("形式："+exchange);
                        }
                        word[0].setExchange(exchange);

                        //发音//中文
                        String partsStr = "";
                        if(json.containsKey("symbols")) {
                            String symbolsStr = json.getString("symbols");
                            XLog.i("发音：" + symbolsStr);
                            List<OpenSymbol> symbols = JSON.parseArray(symbolsStr, OpenSymbol.class);
                            if (symbols != null && symbols.size() > 0) {
                                for (OpenSymbol symbol : symbols) {
                                    if (!TextUtils.isEmpty(symbol.getPh_en())) {
                                        word[0].setPh_en(symbol.getPh_en());
                                    }
                                    if (!TextUtils.isEmpty(symbol.getPh_en_mp3())) {
                                        word[0].setPh_en_mp3(symbol.getPh_en_mp3());
                                    }
                                    if (!TextUtils.isEmpty(symbol.getPh_am())) {
                                        word[0].setPh_am(symbol.getPh_am());
                                    }
                                    if (!TextUtils.isEmpty(symbol.getPh_am_mp3())) {
                                        word[0].setPh_am_mp3(symbol.getPh_am_mp3());
                                    }
                                    List<OpenPart> parts = JSON.parseArray(symbol.getParts(), OpenPart.class);
                                    if (parts != null && parts.size() > 0) {
                                        for (OpenPart part : parts) {
                                            partsStr += (part.getPart() + part.getMeans() + ",");
                                        }
                                    }
                                }
                                if (partsStr.endsWith(",")) {
                                    partsStr = partsStr.substring(0, partsStr.lastIndexOf(","));
                                }
                                XLog.i("中文：" + partsStr);
                                word[0].setParts(partsStr);
                            }
                        }
                        //例句
                        //③从服务器获取单词
                        NetworkManager.getInstance().build()
                                .viewModel(ChaxunFragmentVM.this)   //ViewModel对象
                                .url(ChaxunConstant.URL_JS_CIDIAN_XML+"&w="+text)
                                .showDialog(false)
                                .doGet(new ResponseCallback<XResponse>() {
                                    @Override
                                    public void onSuccess(XResponse result) throws XmlPullParserException, IOException {
                                        List<OpenSent> sents = new ArrayList<>();
                                        OpenSent sent = null;
                                        XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
                                        parser.setInput(new StringReader(result.getResult()));
                                        int eventType = parser.getEventType();
                                        while (eventType != XmlPullParser.END_DOCUMENT) {
                                            switch (eventType) {
                                                case XmlPullParser.START_TAG:
                                                    if (parser.getName().equals("sent")) {
                                                        sent = new OpenSent();
                                                    } else if (parser.getName().equals("orig")) {
                                                        eventType = parser.next();
                                                        sent.setOrig(parser.getText());
                                                    } else if (parser.getName().equals("trans")) {
                                                        eventType = parser.next();
                                                        sent.setTrans(parser.getText());
                                                    }
                                                    break;
                                                case XmlPullParser.END_TAG:
                                                    if (parser.getName().equals("sent")) {
                                                        if(sents.size()<=2){
                                                            sents.add(sent);
                                                            sent = null;
                                                        }
                                                    }
                                                    break;
                                            }
                                            eventType = parser.next();
                                        }
                                        //[{"":"","":""},{}]
                                        if(sents.size()>0){
                                            String sentStrs = "[";
                                            for(OpenSent sentaa : sents){
                                                String sentStr = "{\"orig\":\""+sentaa.getOrig()+"\","+"\"trans\":\""+sentaa.getTrans()+"\"}";
                                                sentStrs+=(sentStr+",");
                                            }
                                            if(sentStrs.endsWith(","))
                                                sentStrs = sentStrs.substring(0, sentStrs.lastIndexOf(","));
                                            sentStrs += "]";
                                            word[0].setSents(sentStrs);
                                        }
                                    }

                                    @Override
                                    public void onFail(String message) {
                                        super.onFail(message);
                                        searchSuc.setValue(false);
                                    }
                                });
                        wordData.setValue(word[0]);
                        XLog.d("爬出单词："+ word[0]);
                        return;
                    }
                    @Override
                    public void onFail(String message) {
                        super.onFail(message);
                        searchSuc.setValue(false);
                    }
                });


        }

    Zhongwen zhongwen;
    public void jsSearchZh(final String text){
        String urlStr = ChaxunConstant.URL_JS_CIDIAN + "&w=" + text;
        XLog.d("金山词霸查中文:"+urlStr);
        NetworkManager.getInstance().build()
                .viewModel(this)   //ViewModel对象
                .url(urlStr)
                .showDialog(false)
                .doGet(new ResponseCallback<XResponse>() {
                    @Override
                    public void onSuccess(XResponse result) throws Exception {
                        zhongwen = new Zhongwen();
                        JSONObject json = JSON.parseObject(result.getResult());
                        zhongwen.word_name = json.getString("word_name");   //中文名
                        zhongwen.symbols = new ArrayList<Symbol>();
                        JSONArray symbolArray = json.getJSONArray("symbols");  //释义
                        if(symbolArray!=null){
                            Iterator<Object> symbolit = symbolArray.iterator();
                            while(symbolit.hasNext()){
                                Symbol symbol = new Symbol();
                                JSONObject symbolJson = (JSONObject) symbolit.next();
                                symbol.word_symbol = symbolJson.getString("word_symbol");   //拼音
                                symbol.parts = new ArrayList<>();
                                JSONArray partArray = symbolJson.getJSONArray("parts");   //英语
                                if(partArray!=null){
                                    Iterator<Object> partit = partArray.iterator();
                                    while(partit.hasNext()){
                                        Part part = new Part();
                                        JSONObject partJson = (JSONObject) partit.next();
                                        part.part_name = partJson.getString("part_name");  //词性
                                        part.means = new ArrayList<>();
                                        JSONArray meanArray = partJson.getJSONArray("means");   //英语
                                        if(meanArray!=null){
                                            Iterator<Object> meanit = meanArray.iterator();
                                            while(meanit.hasNext()){
                                                Mean mean = new Mean();
                                                JSONObject meanJson = (JSONObject) meanit.next();
                                                mean.word_mean = meanJson.getString("word_mean");  //词性
                                                part.means.add(mean);
                                            }
                                        }
                                        symbol.parts.add(part);
                                    }
                                }
                                zhongwen.symbols.add(symbol);
                            }
                        }
                        //例句
                        //③从服务器获取单词
                        String urlStr1 = ChaxunConstant.URL_JS_CIDIAN_XML+"&w="+text;
                        NetworkManager.getInstance().build()
                                .viewModel(ChaxunFragmentVM.this)   //ViewModel对象
                                .url(urlStr)
                                .showDialog(false)
                                .doGet(new ResponseCallback<XResponse>() {
                                    @Override
                                    public void onSuccess(XResponse result) throws Exception {
                                        XLog.d("中文:"+result.getResult());

                                        List<ChaxunFragment.Sent> sents = new ArrayList<ChaxunFragment.Sent>();
                                        ChaxunFragment.Sent sent = null;
                                        XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
                                        parser.setInput(new StringReader(result.getResult())); // 设置输入流
                                        int eventType = parser.getEventType();
                                        while (eventType != XmlPullParser.END_DOCUMENT) {
                                            switch (eventType) {
                                                case XmlPullParser.START_TAG:
                                                    if (parser.getName().equals("sent")) {
                                                        sent = new ChaxunFragment.Sent();
                                                    } else if (parser.getName().equals("orig")) {
                                                        eventType = parser.next();
                                                        sent.setOrig(parser.getText());
                                                    } else if (parser.getName().equals("trans")) {
                                                        eventType = parser.next();
                                                        sent.setTrans(parser.getText());
                                                    }
                                                    break;
                                                case XmlPullParser.END_TAG:
                                                    if (parser.getName().equals("sent")) {
                                                        if(sents.size()<=2){
                                                            sents.add(sent);
                                                            sent = null;
                                                        }
                                                    }
                                                    break;
                                            }
                                            eventType = parser.next();
                                        }
                                        //[{"":"","":""},{}]
                                        if(sents.size()>0){
                                            String sentStrs = "[";
                                            for(ChaxunFragment.Sent sentaa : sents){
                                                String sentStr = "{\"orig\":\""+sentaa.getOrig()+"\","+"\"trans\":\""+sentaa.getTrans()+"\"}";
                                                sentStrs+=(sentStr+",");
                                            }
                                            if(sentStrs.endsWith(","))
                                                sentStrs = sentStrs.substring(0, sentStrs.lastIndexOf(","));
                                            sentStrs += "]";
                                            zhongwen.sents = sentStrs;
                                        }
                                    }
                                });
                        XLog.d("爬出单词："+zhongwen);
                        if(null!=zhongwen&&!TextUtils.isEmpty(zhongwen.word_name)) {
                            zhongwenData.setValue(zhongwen);
                        }else {
                            zhongwenSuc.setValue(false);
                        }
                    }
                });

    }
}
