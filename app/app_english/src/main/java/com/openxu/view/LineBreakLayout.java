package com.openxu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.openxu.utils.MyUtil;

public class LineBreakLayout extends ViewGroup {

	private final static String TAG = "LineBreakLayout";

	private final static int VIEW_MARGIN_LEFT = 30;
	private final static int VIEW_MARGIN_TOP = 30;
	

	public LineBreakLayout(Context context) {
		super(context);
	}

	public LineBreakLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LineBreakLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec+
		// " heightMeasureSpec" + heightMeasureSpec);
		/*for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			// measure
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}*/
		 measureChildren(widthMeasureSpec, heightMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		Log.d(TAG, "changed = " + arg0 + " left = " + arg1 + " top = " + arg2+ " right = " + arg3 + " botom = " + arg4);
		// changed = false left = 0 top = 390 right = 1080 botom = 1920
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int right = 0;// right position of child relative to parent
		int botom = 0; // bottom position of child relative to parent
		for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			
			right += width;
			botom = row * (height + VIEW_MARGIN_TOP) + height;
			// if it can't drawing on a same line , skip to next line
			if (right > (arg3-MyUtil.dp2px(20, getContext()))) {
				right = width;
				row++;
				botom = row * (height + VIEW_MARGIN_TOP) + height;
			}
			Log.d(TAG, " left = " + (right - width) +" top = " + (botom - height)+ " right = " + right + " botom = " + botom);
			child.layout(right - width, botom - height,right,botom);
			right += VIEW_MARGIN_LEFT;
		}

	}

}