package com.openxu.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.openxu.core.BR;
import com.openxu.core.R;
import com.openxu.core.base.adapter.CommandRecyclerAdapter;
import com.openxu.core.base.adapter.ViewHolder;
import com.openxu.core.utils.XClickUtil;
import com.openxu.core.utils.XLog;
import com.openxu.core.view.EmptyRecyclerView;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: openXu
 * Time: 2019/3/13 9:58
 * class: BaseListActivity
 * Description: 通用的下拉刷新、上拉加载列表页面
 *      继承BaseListActivity<Binding类, ViewModel类, 数据类型>
 *      public class ListActivity extends BaseListActivity<CoreActivityBaseListBinding, RefreshListViewModel, RefreshListItem>
 * 注意：
 * 1、如果使用通用布局（不重写getContentView(Bundle savedInstanceState)）,Binding泛型请传入CoreActivityBaseListBinding
 *    如果自定义布局后，请传入自定义布局的Binding类型
 * 2、自定义布局时，请确保
 *    SmartRefreshLayout的id属性为android:id="@id/refreshLayout"
 *    MaterialHeader的id属性为android:id="@id/refreshHeader"
 *    EmptyRecyclerView的id属性为android:id="@id/recyclerView"
 *    ClassicsFooter的id属性为android:id="@id/refreshFooter"
 *
 * 使用说明：
 *
 * 1、覆盖initListPageParams()完成基本初始化操作：
     @Override
     protected void initListPageParams() {
     titleLayout.setTextcenter("xxxx").show();  //设置标题
         itemLayout = R.layout.item_refresh_list;        //item布局id
         extrHeaderLayout = R.layout.inspect_list_head;  //额外的头部布局id（比如一些提示信息）
         extrFooterLayout = R.layout.inspect_list_foot;  //额外的尾部布局id（比如巡检中下方需要添加快捷扫码按钮）
         itemClickId.add(R.id.iv_image);                 //item中某些子控件需要绑定点击事件，需要添加到itemClickId中
         //设置刷新机制
         binding.refreshLayout.setEnableRefresh(true);
         binding.refreshLayout.setEnableLoadMore(true);
         //分割线
         binding.recyclerView.addItemDecoration(new CommandItemDecoration(mContext,LinearLayoutManager.VERTICAL,
         getResources().getColor(com.fpc.libs.R.color.line_bg_color),
         (int)(getResources().getDimension(R.dimen.line_height))));
     }
 *
 * 2、设置item长点击事件以及额外的view初始化操作，请覆盖initView(),注意一定要调用super.initView();
     @Override
     protected void initView() {
        super.initView();
        adapter.setLongClickAble(true);
     }
 *
 * 3、点击事件请按需重写一下方法
    protected abstract void onItemClick(D data,int position);   //条目点击事件
    protected void onItemLongClick(D data,int position){}       //条目长点击事件
    protected void onViewClick(int id,D data,int position){}    //条目中某个控件点击事件
 *
 * 4、请重写getListData()获取数据，请实现自己的ViewModel
     @Override
     protected void getListData() {
         Map<String, String> params = new HashMap<>();
         params.put("companyId", "e3c6f838-13df-11e9-bf3a-fa163e4635ff");
         //当设置分页时，请带上
         params.put("PageIndex", String.valueOf(PageIndex));
         params.put("PageSize", String.valueOf(PageSize));
         viewModel.getData("demo", params, PageIndex == FIRST_PAGE);
     }
 *
 * 5、额外头尾部控件操作使用
      (强转具体DataBinding)headerBinding.控件BR中的id.xxx
      (强转具体DataBinding)footerBinding.控件BR中的id.xxx
 *
 * 6、注意：
 *    ViewModel中请求数据失败时，请重写onFail(String)方法
    @Override
    public void onFail(String message) {
        super.onFail(message);
        listLiveData.setValue(null);  //设置空数据，表示请求失败
    }
 *
 *
 */
public abstract class XBaseListActivity<V extends ViewDataBinding, VM extends XBaseViewModel, D> extends XBaseActivity<V, VM> {

//    protected List<D> dataList = new ArrayList<>();
    protected CommandRecyclerAdapter<D> adapter;
    protected final int FIRST_PAGE = 0;   //与后台约定的分页起始数字
    protected int PageIndex = FIRST_PAGE;   //索引页
    private boolean mEnableRefresh, mEnableLoadMore;
    //需要初始化设置
    protected int PageSize = 30;   //每页记录数
    protected int itemLayout, extrHeaderLayout, extrFooterLayout;
    protected List<Integer> itemClickId = new ArrayList<>();  //item上需要绑定点击事件的id

    /**布局中额外添加的头部和尾部*/
    protected ViewDataBinding headerBinding, footerBinding;

    protected SmartRefreshLayout refreshLayout;
    protected MaterialHeader refreshHeader;
    protected EmptyRecyclerView recyclerView;
    protected ClassicsFooter refreshFooter;
    protected FrameLayout extrHeader;
    protected FrameLayout extrFooter;

    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.core_activity_base_list;
    }

    @Override
    public void initView() {
        initListPageParams();
        initListAdapter();
    }

    /**初始化列表页相关参数*/
    protected abstract void initListPageParams();

    /**点击事件*/
    protected abstract void onItemClick(D data,int position);
    protected void onItemLongClick(D data,int position){}
    protected void onViewClick(int id, D data,int position){}

    /**重写此方法，调用viewModel实现数据请求*/
    protected abstract void getListData();

    /**进入界面时首次获取数据*/
    @Override
    public void initData() {
        getListData();
    }


    protected void initListAdapter(){

        //通过反射获取binding中的View对象
        try {
            Field field = binding.getClass().getField("refreshLayout");
            field.setAccessible(true); // 参数值为true，禁止访问控制检查
            refreshLayout = (SmartRefreshLayout)field.get(binding);
            field = binding.getClass().getField("refreshHeader");
            refreshHeader = (MaterialHeader)field.get(binding);
            field = binding.getClass().getField("recyclerView");
            recyclerView = (EmptyRecyclerView)field.get(binding);
            field = binding.getClass().getField("refreshFooter");
            refreshFooter = (ClassicsFooter)field.get(binding);
        }catch (Exception e){
            XLog.e("列表页面layout文件配置错误，请查看使用说明");
        }
        try {
            Field field = binding.getClass().getField("extrHeader");
            extrHeader = (FrameLayout)field.get(binding);
        }catch (Exception e){
            XLog.e("列表页面没有额外的header布局");
        }
        try {
            Field field = binding.getClass().getField("extrFooter");
            extrFooter = (FrameLayout)field.get(binding);
        }catch (Exception e){
            XLog.e("列表页面没有额外的footer布局");
        }
        //获取是否可加载更多
        try {
            Field field = refreshLayout.getClass().getDeclaredField("mEnableRefresh");
            field.setAccessible(true);
            mEnableRefresh = (boolean)field.get(refreshLayout);
            field = refreshLayout.getClass().getDeclaredField("mEnableLoadMore");
            field.setAccessible(true);
            mEnableLoadMore = (boolean)field.get(refreshLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //避免第一次获取数据失败的情况下能上拉加载，只有首次获取数据成功的情况下才能加载更多
        refreshLayout.setEnableLoadMore(false);
        //额外的头部和尾部
        if(extrHeaderLayout!=0 && null!=extrHeader)
            headerBinding = DataBindingUtil.inflate(LayoutInflater.from(this), extrHeaderLayout, extrHeader, true);
        if(extrFooterLayout!=0 && null!=extrFooter)
            headerBinding = DataBindingUtil.inflate(LayoutInflater.from(this), extrFooterLayout, extrFooter, true);

        //refreshLayout
        refreshLayout.setDragRate(1);
        refreshLayout.setEnableHeaderTranslationContent(false);//是否下拉Header的时候向下平移列表或者内容
        refreshLayout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        //refreshHeader
        refreshHeader.setColorSchemeColors(getResources().getColor(R.color.title_bar_color));  //设置颜色
        //footer
        refreshFooter.setPrimaryColor(getResources().getColor(R.color.main_style));//设置背景颜色
        refreshFooter.setTextSizeTitle(15);//设置标题文字大小（sp单位）
        refreshFooter.setFinishDuration(300);//设置刷新完成显示的停留时间
        refreshFooter.setDrawableSize(18);//同时设置箭头和图片的大小（dp单位）
//        refreshFooter.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
//        refreshFooter.setDrawableProgressSize(20);//设置图片的大小（dp单位）
        refreshFooter.setDrawableMarginRight(15);//设置图片和箭头和文字的间距（dp单位）
//        refreshFooter.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
//        refreshFooter.setProgressResource(R.drawable.ic_progress);//设置图片资源

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                PageIndex = FIRST_PAGE;
                getListData();
            }
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                PageIndex += 1;
                getListData();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommandRecyclerAdapter<D>(this, itemLayout, null) {
            @Override
            public void convert(ViewHolder holder, D data, int position) {
                if(null!=holder.getBinding())
                    holder.getBinding().setVariable(BR.data, data);
                if(null!=itemClickId && itemClickId.size()>0)
                    for(int id : itemClickId)
                        holder.setOnClickListener(id, v -> onViewClick(id, mDatas.get(position), position));
            }
            @Override
            public void onItemClick(D data,int position) {
                XBaseListActivity.this.onItemClick(data, position);
            }
            @Override
            public void onItemLongClick(D data, int position) {
                XBaseListActivity.this.onItemLongClick(data, position);
            }
        };
        recyclerView.setAdapter(adapter);
        //RecyclerView数据变化监听
        adapter.setDataObserver((noData, netError) -> {
            recyclerView.setNoData(noData);
            recyclerView.setEmptyHint(netError?
                    getResources().getString(R.string.core_lable_list_empty_hint_net):
                    getResources().getString(R.string.core_lable_list_empty_hint_null));
        });

        XClickUtil.onClick(this, recyclerView, () -> {
            XLog.w("空页面点击重试");
            //无数据时，点击重新加载
            initData();
        });
    }

    /**
     * 数据请求完成后，刷新
     */
    public void responseData(List<D> list) {
        dismissProgressDialog();
        if(PageIndex == FIRST_PAGE) {
            adapter.setData(list);
            if(null!=list && list.size() > 0){
                //首次请求数据成功后，重置是否可加载更多
                refreshLayout.setEnableLoadMore(mEnableLoadMore);
            }
        }else{
            adapter.addData(list);
            if(null!=list && list.size() <= 0){
                //设置没有更多数据
                refreshLayout.finishLoadMore(0, true, true);
//            refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }


    @Override
    public void showProgressDialog() {
        if(!mEnableRefresh)   //如果不支持下拉刷新，显示让BaseActivity显示对话框
            super.showProgressDialog();
    }
    @Override
    public void dismissProgressDialog(){
        super.dismissProgressDialog();
        if(PageIndex == FIRST_PAGE){
            refreshLayout.finishRefresh();
        }else{
            refreshLayout.finishLoadMore();
        }
    }


}
