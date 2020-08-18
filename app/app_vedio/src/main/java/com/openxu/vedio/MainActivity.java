package com.openxu.vedio;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.base.XFragmentActivity;
import com.openxu.core.base.XBaseListActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.base.adapter.CommandItemDecoration;
import com.openxu.core.databinding.CoreActivityBaseListBinding;
import com.openxu.vedio.ui.MediaPlayerActivity;
import com.openxu.vedio.ui.VedioViewActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends XBaseListActivity<CoreActivityBaseListBinding, XBaseViewModel, String> {


    Handler subHandler;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.w("handler", "主线程创建Handler收到消息："+msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void initListPageParams() {
        titleLayout.setTextcenter("测试").show();  //设置标题
        itemLayout = R.layout.main_test_item;        //item布局id
        itemClickId.add(R.id.tv_item);
        binding.refreshLayout.setEnableRefresh(false);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.recyclerView.addItemDecoration(new CommandItemDecoration(mContext, LinearLayoutManager.VERTICAL,
                getResources().getColor(R.color.line_bg_color),
                (int)(getResources().getDimension(R.dimen.line_height))));

    }

    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Override
    protected void getListData() {
        for(int i = 1; i<=5; i++){
            new Thread(() -> {
                threadLocal.set(Thread.currentThread().getName());
                System.out.println(threadLocal.get());
            }, "线程"+i).start();
        }







        List<String> data = new ArrayList<>();
        data.add("MediaPlayer");
        data.add("VedioView");
        data.add("JavaCV");
        data.add("Ijkplayer");
        data.add("Vitamio");
        data.add("WebView");

        //https://github.com/yixia/VitamioBundle
        responseData(data);

        new Thread(() -> {
            //1：Looper.prepare()
            Looper.prepare();
            subHandler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1:
                            Log.w("handler", "子线程创建Handler收到消息："+msg.obj);
                            break;
                    }
                }
            };
            //2：Looper.loop()
            Looper.loop();
        }).start();

    }

    @Override
    protected void onViewClick(int id, String data, int position) {
        super.onViewClick(id, data, position);
        if(position==0){

            new Thread(() -> {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = "这是子线程发送的消息";
                handler.sendMessage(msg);
            }).start();
            //startActivity(new Intent(this, MediaPlayerActivity.class));
        }else if(position==1){


            //new Message对象（但是获取消息的首选方法是调用Message.obtain())
            Message msg1 = new Message();
            //从全局池返回新的消息实例，避免在许多情况下分配新对象。
            Message msg2 = Message.obtain();
            //
            Message msg3 = subHandler.obtainMessage();
            msg3.what = 1;
            msg3.obj = "这是子线程发送的消息";
            subHandler.sendMessage(msg3);


            startActivity(new Intent(this, VedioViewActivity.class));
        }else if(position==2){
            /*FragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_IJKPLAYER));*/
        }else if(position==3){
            XFragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_IJKPLAYER));
        }else if(position==4){
            XFragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_VITAMIO));
        }else if(position==5){
            XFragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_WEBVIEW));
        }
    }

    @Override
    protected void onItemClick(String data, int position) {
    }



    @Override
    public void registObserve() {

    }

}
