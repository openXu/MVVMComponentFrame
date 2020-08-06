package com.openxu.vedio.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.core.base.XBaseFragment;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.vedio.R;
import com.openxu.vedio.RouterPathVedio;
import com.openxu.vedio.databinding.FragmentIjkplayerBinding;

/**
 * https://github.com/yixia/VitamioBundle/wiki/Getting-Started
 */

@Route(path = RouterPathVedio.PAGE_VITAMIO)
public class VitamioFragment extends XBaseFragment<FragmentIjkplayerBinding, XBaseViewModel> {

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_ijkplayer;
    }

    @Override
    public void initView() {
//        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
//            return;
    }
    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }

}
