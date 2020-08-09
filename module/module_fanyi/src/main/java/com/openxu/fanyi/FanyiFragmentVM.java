package com.openxu.fanyi;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.http.ApiService;
import com.openxu.core.http.NetworkManager;
import com.openxu.core.http.data.XResponse;
import com.openxu.core.http.rx.ParseDataFunction;
import com.openxu.core.http.rx.XTransformer;
import com.openxu.core.utils.XLog;
import com.openxu.fanyi.bean.Fanyi;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * Author: openXu
 * Time: 2019/04/23 09:36
 * class: HomeFragmentVM
 * Description:
 * Update:
 */
public class FanyiFragmentVM extends XBaseViewModel {

    public FanyiFragmentVM(@NonNull Application application) {
        super(application);
    }

//    public void fanyi(String type, String from, String to, String to_zh) {
    public void fanyi(String url, Map<String, String> map) {

        NetworkManager.getInstance().create(ApiService.class)
                .build()
                .rxGetAllPath(url, map)
                .compose(new XTransformer<Fanyi>().baseTransformer(new ParseDataFunction(Fanyi.class)))
                .subscribe(new Consumer<XResponse<Fanyi>>() {
                    @Override
                    public void accept(XResponse<Fanyi> o) throws Exception {
                        XLog.d("请求数据结果："+o);
                    }
                });
     /*   NetworkManager.RequstBuilder<ApiService> builder = NetworkManager.getInstance().create();
        Observable observable = builder.build(ApiService.class)
                .rxGetAllPath(url, new HashMap<>())
                .compose(XTransformer.baseTransformer(new ParseDataFunction(Fanyi.class, builder)));

        observable.subscribe(new Consumer() {
            @Override
            public void accept(Object o)  {

            }
        });*/
/*        accept(NetworkManager.getInstance().create()
                .method(NetworkManager.Method.GET)
                .viewModel(this)
                .url(url)
        .build(ApiService.class).);*/


     /*  NetworkManager.getInstance().newBuilder()
                .method(NetworkManager.Method.GET)
                .viewModel(this)
                .url(url)
//                .putParam("userid", SharedData.getInstance().getUser().getUserID())
               .build(new ResponseCallback(Fanyi.class) {
                   @Override
                   public void onSuccess(String msg, String result) throws Exception {

                   }
               });*/
               /* .build(new ResponseCallback() {
                    @Override
                    public void onSuccess(String msg, FpcDataSource data) throws Exception {
                        *//*if (data.getTables().size() > 0 && data.getTables().get(0).getDatas().size() > 0) {
                            List<HomeCardNum> homeCardNums = ParseNetData.parseData(data.getTables().get(0), HomeCardNum.class);
                            RxBus.get().post("homeCardNums", homeCardNums);
                        }*//*
                        binding.tvYw.setText("译文");
                        // {"from":"zh",
                        // "to":"en",
                        // "trans_result":[
                        // {"src":"\u4eca\u5929","dst":"Today"},
                        // {"src":"\u660e\u5929","dst":"Tomorrow"}]}
                        XLog.e( "翻译结果：" + responseInfo.result);
                        JSONObject json = JSON.parseObject(responseInfo.result);
                        String trans = json.getString("trans_result");
                        if (TextUtils.isEmpty(trans))
                            return;
                        List<Fanyi> fanyis = JSON.parseArray(trans, Fanyi.class);
                        to = json.getString("to");
                        showFanyi(fanyis);
                    }

                    @Override
                    public void onFail(String message) {
                        super.onFail(message);
                        binding.tvYw.setText("翻译失败：" + msg);
                        XLog.e( "翻译失败：" + msg);
                    }
                });
*/
    }
    //1.1.请求任务数量信息
    public void requestTaskCountData() {
       /* NetworkManager.getInstance().newBuilder()
                .method(NetworkManager.Method.GET)
                .viewModel(this)
                .url(ServerApi.Module_Query_ItemCounts_ForAndroid)
                .putParam("userid", SharedData.getInstance().getUser().getUserID())
                .build(new ResponseCallback() {
                    @Override
                    public void onSuccess(String msg, FpcDataSource data) throws Exception {
                        if (data.getTables().size() > 0 && data.getTables().get(0).getDatas().size() > 0) {
                            List<HomeCardNum> homeCardNums = ParseNetData.parseData(data.getTables().get(0), HomeCardNum.class);
                            RxBus.get().post("homeCardNums", homeCardNums);
                        }
                    }
                });*/
    }

}
