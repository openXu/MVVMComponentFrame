package com.openxu.core.base.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * autour : openXu
 * date : 2018/7/24 15:40
 * className : CommandItemDecoration
 * version : 1.0
 * description : RecyclerView分割线
 */
public class CommandItemDecoration extends RecyclerView.ItemDecoration{
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    /**用于绘制间隔样式*/
    private Drawable mDivider;

    /**列表的方向，水平/竖直 */
    private int mOrientation;

    private int dividerSize;

    private Paint paint;

    public CommandItemDecoration(Context context, int orientation, int dividerColor,int dividerSize) {
        // 获取默认主题的属性
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);

        this.dividerSize = dividerSize;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(dividerColor);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, dividerSize);
        } else {
            outRect.set(0, 0, dividerSize, 0);
        }
    }
    /**onDraw方法会在item绘制之前调用，这里用来绘制item的间隔，可能会被item遮挡 */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL_LIST) {
            //获取分隔线左右坐标
            final int left = parent.getPaddingLeft();   //RecyclerView的左padding值
            final int right = parent.getWidth() - parent.getPaddingRight();  //RecyclerView减去right padding值后右边坐标
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                //分割线绘制的top位置=item底端+item底部margin值+item偏移量
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                //分割线底部 = top + 20的overdraw + 底部80偏移值
                final int bottom = top + dividerSize;
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);
                c.drawRect(new Rect(left, top, right, bottom), paint);
            }
        } else {
            //此处绘制水平方向排列时的间隔样式，省略
        }
    }
    /**onDrawOver方法会在item绘制之后调用，这层绘制将会显示在最上层 */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

}