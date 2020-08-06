package com.openxu.english.main;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;
import com.openxu.english.bean.Fanyi;
import com.openxu.core.net.NetworkManager;
import com.openxu.core.net.callback.ResponseCallback;

import org.json.JSONObject;

import java.util.List;

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
    public void fanyi(String url) {
       NetworkManager.getInstance().newBuilder()
                .method(NetworkManager.Method.GET)
                .viewModel(this)
                .url(url)
//                .putParam("userid", SharedData.getInstance().getUser().getUserID())
                .build(new ResponseCallback() {
                    @Override
                    public void onSuccess(String msg, FpcDataSource data) throws Exception {
                        /*if (data.getTables().size() > 0 && data.getTables().get(0).getDatas().size() > 0) {
                            List<HomeCardNum> homeCardNums = ParseNetData.parseData(data.getTables().get(0), HomeCardNum.class);
                            RxBus.get().post("homeCardNums", homeCardNums);
                        }*/
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
