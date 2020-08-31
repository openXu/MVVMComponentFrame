package com.openxu.vedio;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import dalvik.system.PathClassLoader;

import com.alibaba.android.arouter.launcher.ARouter;
import com.openxu.core.base.XFragmentActivity;
import com.openxu.core.base.XBaseListActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.base.adapter.CommandItemDecoration;
import com.openxu.core.databinding.CoreActivityBaseListBinding;
import com.openxu.core.utils.hook.FixDexUtil;
import com.openxu.core.utils.XLog;
import com.openxu.vedio.ui.MediaPlayerActivity;
import com.openxu.vedio.ui.VedioViewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends XBaseListActivity<CoreActivityBaseListBinding, XBaseViewModel, String> {




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


    @Override
    protected void getListData() {

        XLog.w("activity.getClassLoder="+(getClassLoader() == getApplicationContext().getClassLoader()));
        try {
            XLog.w("activity.getClassLoder="+(getClass()==getClassLoader().loadClass("com.openxu.vedio.MainActivity")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        FixDexUtil.loadFixedDex(this);
        List<String> data = new ArrayList<>();
        data.add("MediaPlayer");
        data.add("VedioView");
        data.add("JavaCV");
        data.add("Ijkplayer");
        data.add("Vitamio");
        data.add("WebView");

        //https://github.com/yixia/VitamioBundle
        responseData(data);
    }

    @Override
    protected void onViewClick(int id, String data, int position) {
        super.onViewClick(id, data, position);
        if(position==0){
            startActivity(new Intent(this, MediaPlayerActivity.class));

        }else if(position==1){
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<String> event) {
        XLog.d("收到消息："+event);
    }
    @Override
    public void registObserve() {

    }

}
