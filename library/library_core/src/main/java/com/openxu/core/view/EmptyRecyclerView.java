package com.openxu.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.openxu.core.R;
import com.openxu.core.utils.XConversUtils;
import com.openxu.core.utils.XLog;


/**
 * Author: openXu
 * Time: 2019/3/13 18:21
 * class: EmptyRecyclerView
 * Description: BaseListActivity中使用，能识别无数据时显示空页面
 */
public class EmptyRecyclerView extends RecyclerView {

    private int noDataIcon = R.mipmap.core_icon_page_empty;
    private String hint = "暂无数据";
    private Paint mPaint;
    private View headerView;
    private boolean haveHeader = false;
    private boolean haveFooter = false;


    public EmptyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public EmptyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_level_mid));
    }

    public void setHaveHeader(View headerView) {
        this.headerView = headerView;
        if (headerView != null) {
            haveHeader = true;
        }
    }

    public void setHaveFooter(boolean have) {
        this.haveFooter = have;
    }

    private int realDataCount() {
        int count = getAdapter().getItemCount();
        if (haveHeader) {
            count--;
        }
        if (haveFooter) {
            count--;
        }
        return count;
    }


    public void setNoData(boolean noData) {
        invalidate();
        requestLayout();
    }

    public void setEmptyHint(String hint) {
        this.hint = hint;
        invalidate();
    }

    float startX = 0;
    float startY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (null != getAdapter() && realDataCount() <= 0) {
            XLog.w("点击空页面");
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = e.getX();
                    startY = e.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = e.getX();
                    float endY = e.getY();
                    if (Math.abs(endX - startX) < 10 && Math.abs(endY - startY) < 10) {
                        performClick();   //调用RecyclerView的点击事件
                    }
                    break;
            }
        }
        return super.onTouchEvent(e);
    }


    @Override
    public void draw(Canvas c) {
        super.draw(c);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        float headerViewHeight = 0;
        boolean canDraw = true;
        if (headerView != null) {
            if (headerView.getMeasuredHeight() < getMeasuredHeight() / 2) {
                headerViewHeight = headerView.getMeasuredHeight()/2;
            } else {
                canDraw = false;
            }
        }

        if (canDraw && null != getAdapter() && realDataCount() <= 0) {
            XLog.w("绘制空页面");
            int iconSpace = XConversUtils.dip2px(getContext(), 20);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), noDataIcon);
            Paint.FontMetrics fm = mPaint.getFontMetrics();


            float topSpace = (getMeasuredHeight() - (fm.bottom - fm.top + iconSpace + bitmap.getHeight())) / 8;
            float top = (getMeasuredHeight() - (fm.bottom - fm.top + iconSpace + bitmap.getHeight())) / 2 - topSpace + headerViewHeight;
            c.drawBitmap(bitmap, (getMeasuredWidth() - bitmap.getWidth()) / 2, top, mPaint);
            c.drawText(hint, (getMeasuredWidth() - mPaint.measureText(hint)) / 2,
                    top + bitmap.getHeight() + iconSpace + (fm.leading - fm.ascent), mPaint);
        }
    }


}
