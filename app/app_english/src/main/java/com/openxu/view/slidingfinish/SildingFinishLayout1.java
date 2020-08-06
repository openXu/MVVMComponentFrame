package com.openxu.view.slidingfinish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.openxu.utils.MyUtil;

/**
 * 自定义可以滑动的RelativeLayout, 类似于IOS的滑动删除页面效果，当我们要使用
 * 此功能的时候，需要将该Activity的顶层布局设置为SildingFinishLayout
 * 
 * @author xiaanming
 * 
 * @blog http://blog.csdn.net/xiaanming
 * 
 */
public class SildingFinishLayout1 extends RelativeLayout{
	private String TAG = "SildingFinishLayout";
	/**
	 * SildingFinishLayout布局的父布局
	 */
	private ViewGroup mParentView;
	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop1 = 90;
	/**
	 * 按下点的X坐标
	 */
	private int downX;
	/**
	 * 按下点的Y坐标
	 */
	private int downY;
	/**
	 * 临时存储X坐标
	 */
	private int tempX;
	/**
	 * 滑动类
	 */
	private Scroller mScroller;
	/**
	 * SildingFinishLayout的宽度
	 */
	private int viewWidth;
	
	private boolean isSilding;
	
	private OnSildingFinishListener onSildingFinishListener;
	private boolean isFinish;
	

	public SildingFinishLayout1(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SildingFinishLayout1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
	}
	
	/**
	 * 事件拦截操作
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getRawX();
			//满足此条件屏蔽SildingFinishLayout里面子类的touch事件
			if (Math.abs(moveX - downX) > mTouchSlop
					&& Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
				return true;
			}
			break;
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	

	public int index;
	@SuppressLint("NewApi") @Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getRawX();
			if(index==1){
				int deltaX = tempX - moveX;
				tempX = moveX;
				if (Math.abs(moveX - downX) > mTouchSlop&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
					isSilding = true;
				}
				if (moveX - downX >= 0 && isSilding) {
					mParentView.scrollBy(deltaX, 0);
//					MyUtil.LOG_E(TAG, "mParentView.getScrollX():"+mParentView.getScrollX()+"  viewWidth/2: "+viewWidth/2);
					float juliX = mParentView.getScrollX()*-1.0f;
					if(juliX>(viewWidth/3)){
						float alpha = (juliX-(viewWidth/3))/(viewWidth/3*2);
				        //这句就是设置窗口里崆件的透明度的．０.０全透明．１.０不透明．
				        alpha = (1-alpha)*1.5f;
//				        MyUtil.LOG_E(TAG, "设置透明度："+alpha);
				        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					        mParentView.setAlpha(alpha);
						}
					}
					
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) event.getRawX();
			int upY = (int) event.getRawY();
			if(index>1){
				int deltaX = upX - downX;
				int deltaY = upY - downY;
				MyUtil.LOG_I(TAG, "抬起时X："+deltaX);
				MyUtil.LOG_I(TAG, "抬起时Y："+deltaY);
				if (Math.abs(deltaX) > mTouchSlop1&& Math.abs(deltaY) < mTouchSlop1) {
					if(deltaX>0){
						MyUtil.LOG_I(TAG, "向右滑动，展示上一个单词");
						onSildingFinishListener.showPreWord();
					}else{
						MyUtil.LOG_I(TAG, "向左滑动，展示下一个单词");
						onSildingFinishListener.showNextWord();
					}
				}
			}else{
				int deltaX = upX - downX;
				int deltaY = upY - downY;
				MyUtil.LOG_I(TAG, "抬起时X："+deltaX);
				MyUtil.LOG_I(TAG, "抬起时Y："+deltaY);
				if (Math.abs(deltaX) > mTouchSlop1&& Math.abs(deltaY) < mTouchSlop1) {
					if(deltaX<0){
						MyUtil.LOG_I(TAG, "向左滑动，展示下一个单词");
						onSildingFinishListener.showNextWord();
					}
				}
				
				isSilding = false;
				if (mParentView.getScrollX() <= -viewWidth / 2) {
					isFinish = true;
					scrollRight();
				} else {
					scrollOrigin();
					isFinish = false;
				}
			}
			
			break;
		}
		
		return true;
	}
	
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取SildingFinishLayout所在布局的父布局
			mParentView = (ViewGroup) this.getParent();
			viewWidth = this.getWidth();
		}
	}

	/**
	 * 设置OnSildingFinishListener, 在onSildingFinish()方法中finish Activity
	 * 
	 * @param onSildingFinishListener
	 */
	public void setOnSildingFinishListener(
			OnSildingFinishListener onSildingFinishListener) {
		this.onSildingFinishListener = onSildingFinishListener;
	}


	/**
	 * 滚动出界面
	 */
	@SuppressLint("NewApi") private void scrollRight() {
		final int delta = (viewWidth + mParentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			 mParentView.setAlpha(0.0f);
		}
		postInvalidate();
	}

	/**
	 * 滚动到起始位置
	 */
	@SuppressLint("NewApi") public void scrollOrigin() {
		int delta = mParentView.getScrollX();
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			 mParentView.setAlpha(1.0f);
		}
		postInvalidate();
	}


	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (mScroller.computeScrollOffset()) {
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished() && isFinish) {

				if (onSildingFinishListener != null) {
					onSildingFinishListener.onSildingFinish();
				}else{
					//没有设置OnSildingFinishListener，让其滚动到其实位置
					scrollOrigin();
					isFinish = false;
				}
			}
		}
	}
	

	public interface OnSildingFinishListener {
		public void onSildingFinish();
		public void showPreWord();
		public void showNextWord();
	}



}
