package com.dafruits.android.library.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ExtendedListView extends ListView implements OnScrollListener{

	public static interface OnPositionChangedListener
	{
		//Used to report any change of position 
		public void onPositionChanged(ExtendedListView listView, int position, View scrollBarPanel);
	}
	
	//We will use an OnScrollListener to report any change on the scroll
	private OnScrollListener mOnScrollListener = null;
	
	//mScrollBarPanel refer to the view which will be drawn near the scrollbar 
	//mScrollBarPanelPosition stands for the actual Position touched
	private View mScrollBarPanel = null;
	private int mScrollBarPanelPosition = 0;
	
	//We have a reference to our listener to use it after if needed.
	//mLastPosition will be used to report if anychange occur with the last time position changed
	private OnPositionChangedListener mPositionChangedListener;
	private int mLastPosition = -1;
	
	//The Animations used on the ScrollBarPanel
	private Animation mInAnimation = null;
	private Animation mOutAnimation = null;
	
	
	//Keep track of Measure Spec
	 
	private int mWidthMeasureSpec;
	private int mHeightMeasureSpec;
	
	//This handler make sure we can delay the fade out of the view
	private final Handler mHandler = new Handler();
	private final Runnable mScrollBarPanelFadeRunnable = new Runnable() {

		public void run() {
			if (mOutAnimation != null) {
				mScrollBarPanel.startAnimation(mOutAnimation);
			}
		}
	};	
	
	//Constructors 
	public ExtendedListView(Context context) {
		this(context, null);
	}

	public ExtendedListView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);		
	}

	
	public ExtendedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		//Let s our listView implements onScrollListener so we can check ourselves what changes happens
		super.setOnScrollListener(this);
		
		//Now we get ids of ScrollBarPanel and animations from attributes and inflate them
		//If not provided on the xml then we put the id to -1 and will inflate defaults
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtendedListView);
		final int scrollBarPanelLayoutId = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanel, -1);
		final int scrollBarPanelInAnimation = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanelInAnimation, R.anim.in_animation);
		final int scrollBarPanelOutAnimation = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanelOutAnimation, R.anim.out_animation);
		a.recycle();
				
		//scrollBarPanel inflation
		if (scrollBarPanelLayoutId != -1) {
			setScrollBarPanel(scrollBarPanelLayoutId);
		}	
		
		//Now we define the animations
		if (scrollBarPanelInAnimation > 0) {
			mInAnimation = AnimationUtils.loadAnimation(getContext(), scrollBarPanelInAnimation);
		}
		
		if (scrollBarPanelOutAnimation > 0) {
			final int scrollBarPanelFadeDuration = ViewConfiguration.getScrollBarFadeDuration();
			
			mOutAnimation = AnimationUtils.loadAnimation(getContext(), scrollBarPanelOutAnimation);
			mOutAnimation.setDuration(scrollBarPanelFadeDuration);
			
			//We add an listener to make sure the view remains hidden at the end of out animation
			mOutAnimation.setAnimationListener(new AnimationListener() {
		
				public void onAnimationStart(Animation animation) {}			
				public void onAnimationRepeat(Animation animation) {}
	
				public void onAnimationEnd(Animation animation) {
					//Of course if any panel has been provided
					if (mScrollBarPanel != null) {
						mScrollBarPanel.setVisibility(View.GONE);
					}
				}
			});
		}				
	}

	public void setScrollBarPanel(View scrollBarPanel) {
		//Basic setter for the scrollBarPanel
		mScrollBarPanel = scrollBarPanel;
		mScrollBarPanel.setVisibility(View.GONE);
		requestLayout();
	}

	public void setScrollBarPanel(int resId) {
		//Basic setter for the scrollBarPanel
		setScrollBarPanel(LayoutInflater.from(getContext()).inflate(resId, this, false));
	}

	public View getScrollBarPanel() {
		//Basic getter for the scrollBarPanel
		return mScrollBarPanel;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}	
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (null != mPositionChangedListener && null != mScrollBarPanel) {

			// Don't do anything if there is no itemviews
			if (totalItemCount > 0) {
				/*
				 * from android source code (ScrollBarDrawable.java)
				 */
				final int thickness = getVerticalScrollbarWidth();
				
				int height = Math.round((float) getMeasuredHeight() * computeVerticalScrollExtent() / computeVerticalScrollRange());
				int thumbOffset = Math.round((float) (getMeasuredHeight() - height) * computeVerticalScrollOffset() / (computeVerticalScrollRange() - computeVerticalScrollExtent()));
				
				final int minLength = thickness * 2;
				
				if (height < minLength)height = minLength;
				
				thumbOffset += height / 2;
				
				/*
				 * find out which itemviews the center of thumb is on
				 */
				final int count = getChildCount();
				
				for (int i = 0; i < count; ++i) {
					
					final View childView = getChildAt(i);
					
					if (childView != null) {
						if (thumbOffset > childView.getTop() && thumbOffset < childView.getBottom()) {
							/* 
							 * we have our candidate
							 */
							if (mLastPosition != firstVisibleItem + i) {
								mLastPosition = firstVisibleItem + i;
								
								/*
								 * inform the position of the panel has changed
								 */
								mPositionChangedListener.onPositionChanged(this, mLastPosition, mScrollBarPanel);
								
								/*
								 * measure panel right now since it has just changed
								 * 
								 * INFO: quick hack to handle TextView has ScrollBarPanel (to wrap text in
								 * case TextView's content has changed)
								 */
								measureChild(mScrollBarPanel, mWidthMeasureSpec, mHeightMeasureSpec);
							}
							break;
						}
					}
				}

				/*
				 * update panel position
				 */
				mScrollBarPanelPosition = thumbOffset - mScrollBarPanel.getMeasuredHeight() / 2;
				final int x = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
				mScrollBarPanel.layout(x, mScrollBarPanelPosition, x + mScrollBarPanel.getMeasuredWidth(),
						mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
			}
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
	
	public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
		mPositionChangedListener = onPositionChangedListener;
	}
	
	@Override
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		mOnScrollListener = onScrollListener;
	}
	
	@Override
	protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
		
		final boolean isAnimationPlayed = super.awakenScrollBars(startDelay, invalidate);
		
		if (isAnimationPlayed == true && mScrollBarPanel != null) {
			if (mScrollBarPanel.getVisibility() == View.GONE) {
				mScrollBarPanel.setVisibility(View.VISIBLE);
				if (mInAnimation != null) {
					mScrollBarPanel.startAnimation(mInAnimation);
				}
			}
			
			mHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
			mHandler.postAtTime(mScrollBarPanelFadeRunnable, AnimationUtils.currentAnimationTimeMillis() + startDelay);
		}

		return isAnimationPlayed;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (mScrollBarPanel != null && getAdapter() != null) {
			mWidthMeasureSpec = widthMeasureSpec;
			mHeightMeasureSpec = heightMeasureSpec;
			measureChild(mScrollBarPanel, widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mScrollBarPanel != null) {
			final int x = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
			
			mScrollBarPanel.layout(x, mScrollBarPanelPosition, x + mScrollBarPanel.getMeasuredWidth(),
					mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mScrollBarPanel != null && mScrollBarPanel.getVisibility() == View.VISIBLE) {
			drawChild(canvas, mScrollBarPanel, getDrawingTime());
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		mHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
	}

}
