package com.openxu.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.openxu.english.R;

/**
 * User: lizheng<br>
 * Date: 13-7-10<br>
 * Time: 上午10:28<br>
 * Email: kenny.li@itotemdeveloper.com<br>
 * 一个默认弹框的实现,使用是可以通过调整layout，style。更改为适合各自项目的弹框
 */
public abstract class ItotemBaseDialog extends Dialog {
	protected Context context;
	private ViewGroup rootView;

    /**
     * 用一个默认样式文件
     * @param context C
     * @param layoutResId 布局文件
     */
    public ItotemBaseDialog(Context context, int layoutResId) {
        super(context, R.style.ItotemTheme_Dialog);
        this.context = context;
        init(layoutResId);
    }

    /**
     *
     * @param context C
     * @param layoutResId 布局文件
     * @param styleId 样式文件
     */
    public ItotemBaseDialog(Context context, int layoutResId, int styleId) {
        super(context, styleId);
        this.context = context;
        init(layoutResId);
    }

    private void init(int layoutResId){
        setContentView(layoutResId);
        setProperty();
        initView();
        
        rootView = (ViewGroup) findViewById(R.id.rootView);
        if(rootView!=null){
        	rootView.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				if(canleOnTouchOut){
    					cancel();
    				}
    			}
    		});
    	}
        
        initData();
        setListener();
    }

   private boolean canleOnTouchOut = true;
   public void setCanleOnTouchOut(boolean cancel){
    	this.canleOnTouchOut = canleOnTouchOut;
    }

    private void setProperty() {
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        Display d = getWindow().getWindowManager().getDefaultDisplay();

        // d.getHeight()在API13以上为废弃方法。因此出现废弃警告可不予理会
        p.height = (int) (d.getHeight() * 1);
        p.width = (int) (d.getWidth() * 1);
        window.setAttributes(p);

    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListener();
}
