package com.openxu.vedio;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.openxu.core.base.FragmentActivity;
import com.openxu.core.base.XBaseListActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.base.adapter.CommandItemDecoration;
import com.openxu.core.databinding.CoreActivityBaseListBinding;
import com.openxu.vedio.ui.MediaPlayerActivity;
import com.openxu.vedio.ui.VedioViewActivity;

import java.io.InputStream;
import java.security.MessageDigest;
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
            FragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_IJKPLAYER));
        }else if(position==4){
            FragmentActivity.start(this,
                    ARouter.getInstance().build(RouterPathVedio.PAGE_VITAMIO));
        }else if(position==5){
            FragmentActivity.start(this,
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
