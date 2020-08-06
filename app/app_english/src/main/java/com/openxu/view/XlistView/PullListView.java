/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.openxu.view.XlistView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.openxu.english.R;

public class PullListView extends ListView implements OnScrollListener {
	private static final String TAG = "PullListView";

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private ListViewHeader mHeaderView;
	private ImageView iv_load;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // 是否正在刷新

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static float OFFSET_RADIO = 1.5f; // support iOS like pull
													// feature.

	public PullListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public PullListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private Animation stateAnim;

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new ListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		iv_load = (ImageView) mHeaderView.findViewById(R.id.iv_load);
		addHeaderView(mHeaderView);
		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});

		// 进度条转圈圈
		stateAnim = AnimationUtils.loadAnimation(context,R.anim.open_listviewloding_anim);
		LinearInterpolator lin = new LinearInterpolator();
		stateAnim.setInterpolator(lin);
		iv_load.startAnimation(stateAnim);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (mEnablePullRefresh) { // 刷新
				if (getFirstVisiblePosition() == 0
						&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
					// Log.e(TAG, "上*****");
					updateHeaderHeight(deltaY / OFFSET_RADIO);
					invokeOnScrolling();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mLastY = -1; // reset
			if (mHeaderView.getVisiableHeight() > 0) {
				if (getFirstVisiblePosition() == 0) {
					if (mEnablePullRefresh&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
						if(!mPullRefreshing){    //mPullRefreshing为true已经处于刷新状态
							mPullRefreshing = true;   //只有不是正在刷新的才调用刷新回调
							mHeaderView.setState(ListViewHeader.STATE_REFRESHING);
							if (mListViewListener != null) 
								mListViewListener.onLoadMore();
						}
					}
				}
				resetHeaderHeight();
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void removeOnScrollListener() {
		super.setOnScrollListener(null);
	}

	public void initOnScrollListener() {
		super.setOnScrollListener(this);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * @param enable
	 */
	public void setRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
			removeHeaderView(mHeaderView);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopLoad() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh) {
			if (!mPullRefreshing) { // 未处于刷新状态，更新箭头
				if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mHeaderView.setState(ListViewHeader.STATE_READY);
				} else {
					mHeaderView.setState(ListViewHeader.STATE_NORMAL);
				}
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * 重置刷新高度
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // 不可见
			return;
		// 刷新头显示不完全
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	public void setRefreshing(boolean isRefreshing) {
		if (isRefreshing)
			mHeaderView.setState(ListViewHeader.STATE_REFRESHING);
		this.mPullRefreshing = isRefreshing;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	/**
	 * 滚动监听
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onLoadMore();
	}
	
}
