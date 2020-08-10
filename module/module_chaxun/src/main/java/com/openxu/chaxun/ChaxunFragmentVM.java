package com.openxu.fanyi;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.http.ApiService;
import com.openxu.core.http.NetworkManager;
import com.openxu.core.http.callback.ResponseCallback;
import com.openxu.core.http.data.XResponse;
import com.openxu.core.http.rx.ParseDataFunction;
import com.openxu.core.http.rx.XTransformer;
import com.openxu.core.utils.XLog;
import com.openxu.fanyi.bean.FanyiResult;

import java.util.Map;

import io.reactivex.functions.Consumer;


/**
 * Author: openXu
 * Time: 2019/04/23 09:36
 * class: HomeFragmentVM
 * Description:
 * Update:
 */
public class ChaxunFragmentVM extends XBaseViewModel {

    public MutableLiveData<FanyiResult> fanyiData = new MutableLiveData<>();

    public FanyiFragmentVM(@NonNull Application application) {
        super(application);
    }

//    public void fanyi(String type, String from, String to, String to_zh) {
    public void fanyi(String url, Map<String, String> map) {

        /**
         * {"from":"en","to":"zh","trans_result":[{"src":"Hello","dst":"\u4f60\u597d"}]}
         */
        /**
         * get请求，支持相对路径和绝对路径，可设置显示进度框，与生命周期共存亡
         */
        NetworkManager.getInstance().build()
                //必须
                .viewModel(this)   //ViewModel对象
                .url(url)          //url，绝对路径或者相对路径都行，如果设置相对路径，请在conf_app.gradle中配置baseUrl
                .putParams(map)
                //可选
                .showDialog(true)   //是否显示进度条，默认true
                /**
                 * 调用get请求，传入回调对象，泛型为返回的数据类型
                 * 如果传递了泛型，一定要为ResponseCallback的构造方法传递对应的class对象
                 * 如果不传递泛型，onSuccess(Object o)将接受Object类型，其实为XResponse类型
                 * 注意：由于每个公司项目返回数据格式不同，如果需要对数据格式统一封装，请参照XResponse，修改ParseDataFunction类重新解析数据，然后将泛型类型全部改为您封装的类型即可
                 * 此处提供有偿解答，如有需求请私聊我
                 */
                .doGet(new ResponseCallback<FanyiResult>(FanyiResult.class) {
                    @Override
                    public void onSuccess(FanyiResult result){
                        XLog.d("请求数据结果："+result);
                        fanyiData.setValue(result);
                    }
                    @Override
                    public void onFail(String message) {
                        //错误统一Toast处理
                        super.onFail(message);
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
