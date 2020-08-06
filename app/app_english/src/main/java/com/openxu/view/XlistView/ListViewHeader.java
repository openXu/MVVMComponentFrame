/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.openxu.view.XlistView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openxu.english.R;

public class ListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView iv_load;
	private int mState = STATE_NORMAL;
	private final int ROTATE_ANIM_DURATION = 180;
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public ListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public ListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度�?
		LayoutParams lp = new LayoutParams(
				LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xmppchat_xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		iv_load = (ImageView) findViewById(R.id.iv_load);
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
			}
			if (mState == STATE_REFRESHING) {
			}
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
			}
			break;
		case STATE_REFRESHING:
			break;
		default:
		}

		mState = state;
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
