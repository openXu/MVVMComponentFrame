package cn.sharesdk.my;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

/**
 * TODO<分享工具>
 * @data: 2014-7-21 下午2:45:38
 * @version: V1.0
 */

public class SharePopupWindow extends PopupWindow {

    private Context context;
    private PlatformActionListener platformActionListener;
    private ShareModel shareModel;
    private int SHARED_TYPE;
    
    public SharePopupWindow(Context cx, int type) {
    	ShareSDK.initSDK(cx);
        this.context = cx;
        this.SHARED_TYPE =type;
    }

    public PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void setPlatformActionListener(
            PlatformActionListener platformActionListener) {
        this.platformActionListener = platformActionListener;
    }

    public void showShareWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.share_layout,null);
        GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
        ShareAdapter adapter = new ShareAdapter(context);
        gridView.setAdapter(adapter);

        ((TextView)view.findViewById(R.id.share_title)).setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setBackgroundResource(MyApplication.pf.btn_selector);
        // 取消按钮
        tv_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        gridView.setOnItemClickListener(new ShareItemClickListener(this));

    }

    private class ShareItemClickListener implements OnItemClickListener {
        private PopupWindow pop;

        public ShareItemClickListener(PopupWindow pop) {
            this.pop = pop;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            share(position);
            pop.dismiss();

        }
    }

    /**
     * 初始化分享参数
     * @param shareModel
     */
    public void setShareModel(ShareModel shareModel) {
       this.shareModel = shareModel;
    }
    /**
     * 分享
     * @param position
     */
    private void share(int position) {
        if (position == 1) {
            qq();
        }else if(position==2){
        	SinaWeibo();
        } else if (position == 4) {
            qzone();
        } else if(position==0){
        	share_weixin("Wechat");
        } else if(position==3){
        	share_weixin("WechatMoments");
        }
    }
    /**
     * 获取平台
     * @param position
     * @return
     */
    private String getPlatform(int position) {
        String platform = "";
        switch (position) {
        case 0:
            platform = "Wechat";
            break;
        case 1:
            platform = "QQ";
            break;
        case 2:
            platform = "SinaWeibo";
            break;
        case 3:
            platform = "WechatMoments";
            break;
        case 4:
            platform = "QZone";
            break;
        case 5:
            platform = "tencentWeibo";
            break;
        }
        return platform;
    }

    private void share_weixin(String plat) {
        ShareParams sp = new ShareParams();
        sp.setShareType(SHARED_TYPE);
        sp.setTitle(shareModel.getTitle());
        sp.setText(shareModel.getText());
        switch (SHARED_TYPE) {
		case Platform.SHARE_IMAGE:
			 sp.setImageUrl(shareModel.getImageUrl());
			break;
		case Platform.SHARE_WEBPAGE:
			if(!TextUtils.isEmpty(shareModel.getImageUrl()))
				sp.setImageUrl(shareModel.getImageUrl());
			else
				sp.setImageData(shareModel.getBitmap());
	        sp.setUrl(shareModel.getUrl());
			break;
		default:
			break;
		}
        
        Platform weixin = ShareSDK.getPlatform(context, plat);
        weixin.setPlatformActionListener(platformActionListener); // 设置分享事件回调 //
        weixin.share(sp);
    }
    
    /**
     * 分享到QQ空间
     * QQ空间分享时一定要携带title、titleUrl、site、siteUrl
     */
    private void qzone() {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareModel.getTitle());
        sp.setTitleUrl(shareModel.getUrl()); // 标题的超链接
        sp.setText(shareModel.getText());
        if(SHARED_TYPE==Platform.SHARE_IMAGE){
        	if(!TextUtils.isEmpty(shareModel.getImageUrl()))
				sp.setImageUrl(shareModel.getImageUrl());
			else
				sp.setImageData(shareModel.getBitmap());
        }
//        sp.setComment("我对此分享内容的评论");
        sp.setSite(shareModel.getTitle());
        sp.setSiteUrl(shareModel.getUrl());
        Platform qzone = ShareSDK.getPlatform(context, QZone.NAME);
        qzone.setPlatformActionListener(platformActionListener); // 设置分享事件回调 //
        qzone.share(sp);
    }

    private void qq() {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareModel.getTitle());
        sp.setTitleUrl(shareModel.getUrl()); // 标题的超链接
        sp.setText(shareModel.getText());
    	if(!TextUtils.isEmpty(shareModel.getImageUrl()))
			sp.setImageUrl(shareModel.getImageUrl());
		else
			sp.setImageData(shareModel.getBitmap());
    
        Platform qq = ShareSDK.getPlatform(context, QQ.NAME);
        qq.setPlatformActionListener(platformActionListener);
        qq.share(sp);
    }
    
    /**
     * 新浪微博支持分享文字、本地图片、网络图片和经纬度信息 新浪微博使用客户端分享不会正确回调 参数说明 text：不能超过140个汉字 image：图片最大5M，仅支持JPEG、GIF、PNG格式 latitude：有效范围:-90.0到+90.0，+表示北纬 longitude：有效范围：-180.0到+180.0，+表示东经
     * 分享文本	text	latitude(可选)	longitude(可选)
		分享图文
     */
    private void SinaWeibo() {
        ShareParams sp = new ShareParams();
        sp.setText(shareModel.getText());
        if(SHARED_TYPE==Platform.SHARE_IMAGE){
        	if(!TextUtils.isEmpty(shareModel.getImageUrl()))
				sp.setImageUrl(shareModel.getImageUrl());
			else
				sp.setImageData(shareModel.getBitmap());
        }else if(SHARED_TYPE==Platform.SHARE_WEBPAGE){
        	if(!TextUtils.isEmpty(shareModel.getImageUrl()))
				sp.setImageUrl(shareModel.getImageUrl());
			else
				sp.setImageData(shareModel.getBitmap());
        }
        Platform weibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
        weibo.setPlatformActionListener(platformActionListener);
        weibo.share(sp);
    }

}