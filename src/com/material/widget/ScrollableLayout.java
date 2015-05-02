package com.material.widget;

import com.material.R;
import com.material.widget.scrollable.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class ScrollableLayout extends FrameLayout {

    private Scroller mScroller;
    private GestureDetector mScrollDetector;
    private GestureDetector mFlingDetector;

    private CanScrollVerticallyDelegate mCanScrollVerticallyDelegate;
    private OnScrollChangedListener mOnScrollChangedListener;

    private int mMaxScrollY;

    private boolean mIsScrolling;

    private View mDraggableView;
    private boolean mIsDraggingDraggable;
    private final Rect mDraggableRect;
    {
        mDraggableRect = new Rect();
    }

    public ScrollableLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("NewApi")
	private void init(Context context, AttributeSet attributeSet) {

        final TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollableLayout);
        try {

            final boolean flyWheel = array.getBoolean(R.styleable.ScrollableLayout_scrollable_scrollerFlywheel, false);

            mScroller = initScroller(context, null, flyWheel);

            mMaxScrollY = array.getDimensionPixelSize(R.styleable.ScrollableLayout_scrollable_maxScroll, 0);

        } finally {
            array.recycle();
        }

        setVerticalScrollBarEnabled(true);

        mScrollDetector = new GestureDetector(context, new ScrollGestureListener());
        mFlingDetector  = new GestureDetector(context, new FlingGestureListener());
    }

    /**
     * Override this method if you wish to create own {@link android.widget.Scroller}
     * @param context {@link android.content.Context}
     * @param interpolator {@link android.view.animation.Interpolator}, the default implementation passes <code>null</code>
     * @param flywheel {@link android.widget.Scroller#Scroller(android.content.Context, android.view.animation.Interpolator, boolean)}
     * @return new instance of {@link android.widget.Scroller} must not bu null
     */
    @SuppressLint("NewApi")
	protected Scroller initScroller(Context context, Interpolator interpolator, boolean flywheel) {
        return new Scroller(context, interpolator, flywheel);
    }

    /**
     * Sets friction for current {@link android.widget.Scroller}
     * @see android.widget.Scroller#setFriction(float)
     * @param friction to be applied
     */
    public void setFriction(float friction) {
        mScroller.setFriction(friction);
    }

    /**
     * @see ru.noties.scrollable.CanScrollVerticallyDelegate
     * @param delegate which will be invoked when scroll state of scrollable children is needed
     */
    public void setCanScrollVerticallyDelegate(CanScrollVerticallyDelegate delegate) {
        this.mCanScrollVerticallyDelegate = delegate;
    }

    /**
     * Also can be set via xml attribute <code>scrollable_maxScroll</code>
     * @param maxY the max scroll y available for this View.
     * @see #getMaxScrollY()
     */
    public void setMaxScrollY(int maxY) {
        this.mMaxScrollY = maxY;
    }

    /**
     * @return value which represents the max scroll distance to <code>this</code> View (aka <code>header</code> height)
     * @see #setMaxScrollY(int)
     */
    public int getMaxScrollY() {
        return mMaxScrollY;
    }

    /**
     * Pass an {@link ru.noties.scrollable.OnScrollChangedListener}
     * if you wish to get notifications when scroll state of <code>this</code> View has changed.
     * It\'s helpful for implementing own logic which depends on scroll state (e.g. parallax, alpha, etc)
     * @param listener to be invoked when {@link #onScrollChanged(int, int, int, int)} has been called.
     *                 Might be <code>null</code> if you don\'t want to receive scroll notifications anymore
     */
    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.mOnScrollChangedListener = listener;
    }

    /**
     * @see android.view.View#onScrollChanged(int, int, int, int)
     * @see ru.noties.scrollable.OnScrollChangedListener#onScrollChanged(int, int, int)
     */
    @Override
    public void onScrollChanged(int l, int t, int oldL, int oldT) {
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(t, oldT, mMaxScrollY);
        }
    }

    /**
     * @see View#scrollTo(int, int)
     * @see #setCanScrollVerticallyDelegate(CanScrollVerticallyDelegate)
     * @see #setMaxScrollY(int)
     */
    @Override
    public void scrollTo(int x, int y) {

        final int newY = getNewY(y);
        if (newY < 0) {
            return;
        }

        super.scrollTo(0, newY);
    }

    protected int getNewY(int y) {

        final int currentY = getScrollY();

        if (currentY == y) {
            return -1;
        }

        final int direction = y - currentY;
        final boolean isScrollingBottomTop = y - currentY < 0;

        if (mCanScrollVerticallyDelegate != null) {

            if (isScrollingBottomTop) {

                // if not dragging draggable then return, else do not return
                if (!mIsDraggingDraggable
                        && mCanScrollVerticallyDelegate.canScrollVertically(direction)) {
                    return -1;
                }
            } else {

                // else check if we are at max scroll
                if (currentY == mMaxScrollY
                        && !mCanScrollVerticallyDelegate.canScrollVertically(direction)) {
                    return -1;
                }
            }
        }

        if (y < 0) {
            y = 0;
        } else if (y > mMaxScrollY) {
            y = mMaxScrollY;
        }

        return y;
    }

    /**
     * Sets View which should be included in receiving scroll gestures.
     * Maybe be null
     * @param view you wish to include in scrolling gestures (aka tabs)
     */
    public void setDraggableView(View view) {
        mDraggableView = view;
    }

    @SuppressLint("NewApi")
	@Override
    public boolean dispatchTouchEvent(@SuppressWarnings("NullableProblems") MotionEvent event) {

        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {

            mScroller.abortAnimation();
            removeCallbacks(mScrollRunnable);

            if (mDraggableView != null && mDraggableView.getGlobalVisibleRect(mDraggableRect)) {
                final int x = (int) (event.getRawX() + .5F);
                final int y = (int) (event.getRawY() + .5F);
                mIsDraggingDraggable = mDraggableRect.contains(x, y);
            } else {
                mIsDraggingDraggable = false;
            }
        }

        final boolean prevScrollingState = mIsScrolling;
        mIsScrolling = mScrollDetector.onTouchEvent(event);
        final boolean flingResult = mFlingDetector.onTouchEvent(event);

        if (mIsDraggingDraggable && mIsScrolling
                || (prevScrollingState && action == MotionEvent.ACTION_UP && flingResult)) {
            final MotionEvent cancelEvent = MotionEvent.obtain(event);
            try {
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                super.dispatchTouchEvent(cancelEvent);
            } finally {
                cancelEvent.recycle();
            }
            return true;
        }

        super.dispatchTouchEvent(event);

        return true;
    }

    private final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {

            if (mScroller.computeScrollOffset()) {

                final int y = mScroller.getCurrY();
                final int nowY = getScrollY();
                final int diff = y - nowY;

                if (diff != 0) {
                    scrollBy(0, diff);
                }

                post(this);
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int oldY = getScrollY();
            final int nowY = mScroller.getCurrY();
            scrollTo(0, nowY);
            if (oldY != nowY) {
                onScrollChanged(0, getScrollY(), 0, oldY);
            }
            postInvalidate();
        }
    }

    @Override
    protected int computeVerticalScrollRange() {
        return mMaxScrollY;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childTop = top;
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            view.layout(left, childTop, right, childTop + view.getMeasuredHeight());
            childTop += view.getMeasuredHeight();
        }
    }

    @SuppressLint("NewApi")
	private class ScrollGestureListener extends GestureListenerAdapter {

        private final int mTouchSlop;
        {
            final ViewConfiguration vc = ViewConfiguration.get(getContext());
            mTouchSlop = vc.getScaledTouchSlop();
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            final float absX = Math.abs(distanceX);

            if (absX > Math.abs(distanceY)
                    || absX > mTouchSlop) {
                return false;
            }

            scrollBy(0, (int) distanceY);

            return true;
        }
    }

    private class FlingGestureListener extends GestureListenerAdapter {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                return false;
            }

            final int nowY = getScrollY();
            if (nowY < 0 || nowY > mMaxScrollY) {
                return false;
            }

            removeCallbacks(mScrollRunnable);
            mScroller.fling(0, nowY, 0, -(int) (velocityY + .5F), 0, 0, 0, mMaxScrollY);
            post(mScrollRunnable);

            if (mScroller.computeScrollOffset()) {

                final int finalY = mScroller.getFinalY();
                final int newY = getNewY(finalY);

                return !(finalY == nowY || newY < 0);
            }

            return false;
        }
    }
}
