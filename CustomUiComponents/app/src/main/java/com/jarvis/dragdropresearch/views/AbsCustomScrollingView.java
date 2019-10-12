package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public abstract class AbsCustomScrollingView<T extends ScrollPage> extends FrameLayout {
    private static final String TAG = AbsCustomScrollingView.class.getName();
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    protected int mContentHeight = 5000;
    protected int mContentWidth = 8000;

    private OverScroller mScroller;

    private ScrollListener mListener;

    /**
     * Position of the last motion event.
     */
    private int mLastMotionY;

    protected boolean mInitializedPages;

    protected List<T> mPages;

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;
    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mOverscrollDistance;
    private int mOverflingDistance;

    public AbsCustomScrollingView(@NonNull Context context) {
        super(context);
        initGestureExperimentView();
    }

    public AbsCustomScrollingView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        initGestureExperimentView();
    }

    public AbsCustomScrollingView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGestureExperimentView();
    }

    private void initGestureExperimentView() {
        mScroller = new OverScroller(getContext());
        setFocusable(true);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        mOverflingDistance = configuration.getScaledOverflingDistance();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mInitializedPages) {
            initializePages();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        updatePageStates();
    }

    /**
     * Update visibility of pages.
     */
    private void updatePageStates() {
        int upperBound = getContentUpperBound();
        if (mPages != null) {
            for (int i = 0; i < mPages.size(); i++) {
                T page = mPages.get(i);

                int lowerBound = upperBound + page.getHeight();

                boolean pageInRange =
                        page.getYPosition() <= lowerBound &&
                                (page.getYPosition() + page.getHeight()) >= upperBound;

                page.setVisible(pageInRange);

                page.setScrolledToTop(page.getYPosition() <= upperBound);
            }
        }
    }

    protected int getContentUpperBound() {
        return getScrollY() + getPaddingTop();
    }

    /**
     * This method is called after views have been measured. Once the measurements are known,
     * the user can assign dimensions to the pages.
     */
    protected abstract void initializePages();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        scrollTo(getScrollX(), getScrollY());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */
        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
                /*
                 * Locally do absolute value. mLastMotionY is set to the y value
                 * of the down event.
                 */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                final int y = (int)ev.getY(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);

                if (yDiff > mTouchSlop) {
                    updateDragState(true);
                    mLastMotionY = y;
                    initVelocityTrackerIfNotExists();
                    mVelocityTracker.addMovement(ev);
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                break;
            }
            case MotionEvent.ACTION_DOWN: {
                final int y = (int)ev.getY();
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mActivePointerId = ev.getPointerId(0);
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                updateDragState(!mScroller.isFinished());
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                updateDragState(false);
                mActivePointerId = INVALID_POINTER;
                recycleVelocityTracker();
                if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRangeY())) {
                    postInvalidateOnAnimation();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // TODO:   onSecondaryPointerUp(ev);
                break;
        }
        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                updateDragState(!mScroller.isFinished());
                if (mIsBeingDragged) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                // Remember where the motion event started
                mLastMotionY = (int)ev.getY();
                mActivePointerId = ev.getPointerId(0);

                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);

                final int y = (int)ev.getY(activePointerIndex);

                int deltaY = mLastMotionY - y;

                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    updateDragState(true);
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    mLastMotionY = y;

                    final int oldX = getScrollX();
                    final int oldY = getScrollY();
                    final int rangeY = getScrollRangeY();

                    if (overScrollBy(0, deltaY, 0, getScrollY(),
                            0, rangeY, 0, mOverscrollDistance, true)) {
                        // Break our velocity if we hit a scroll barrier.
                        mVelocityTracker.clear();
                    }
                    onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                    int initialVelocity = (int)velocityTracker.getYVelocity(mActivePointerId);

                    if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                        flingY(-initialVelocity);
                    } else {
                        if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0,
                                getScrollRangeY())) {
                            postInvalidateOnAnimation();
                        }
                    }

                    mActivePointerId = INVALID_POINTER;

                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    if (mScroller
                            .springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRangeY())) {
                        postInvalidateOnAnimation();
                    }
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mLastMotionY = (int)ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionY = (int)ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = (int)ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private int getScrollRangeY() {
        return Math.max(0, mContentHeight - getHeight());
    }

    private int getScrollRangeX() {
        return Math.max(0, mContentWidth - getWidth());
    }

    /**
     * <p>The vertical scroll range of the view is the overall height of all of its
     * children.</p>
     */
    @Override
    protected int computeVerticalScrollRange() {
        int scrollRange = mContentHeight;
        final int scrollY = getScrollY();
        final int overscrollBottom = Math.max(0, mContentHeight - getHeight());
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overscrollBottom) {
            scrollRange += scrollY - overscrollBottom;
        }

        return scrollRange;
    }

    /**
     * <p>The horizontal scroll range of the view is the overall width of all of its
     * children.</p>
     */
    @Override
    protected int computeHorizontalScrollRange() {
        int scrollRange = mContentWidth;
        final int scrollX = getScrollX();
        final int overscrollRight = Math.max(0, mContentWidth - getWidth());

        if (scrollX < 0) {
            scrollRange -= scrollX;
        } else if (scrollX > overscrollRight) {
            scrollRange += scrollX - overscrollRight;
        }

        return scrollRange;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY,
            boolean clampedX, boolean clampedY) {
        // Treat animating scrolls differently; see #computeScroll() for why.
        if (!mScroller.isFinished()) {
            final int oldX = getScrollX();
            final int oldY = getScrollY();
            setScrollX(scrollX);
            setScrollY(scrollY);
            onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            if (clampedY) {
                mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRangeY());
            }
        } else {
            super.scrollTo(scrollX, scrollY);
        }
    }

    /**
     * Fling the view.
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     * numbers mean that the finger/cursor is moving down the screen,
     * which means we want to scroll towards the top.
     */
    public void flingY(int velocityY) {
        int height = getHeight();
        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0,
                Math.max(0, mContentHeight - height), 0, height / 2);
        postInvalidateOnAnimation();
    }

    private void endDrag() {
        updateDragState(false);
        recycleVelocityTracker();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // This is called at drawing time by ViewGroup.
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y) {
                final int range = getScrollRangeY();
                overScrollBy(x - oldX, y - oldY, oldX, oldY, 0, range,
                        0, mOverflingDistance, false);
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            if (!awakenScrollBars()) {
                // Keep on drawing until the animation has finished.
                postInvalidateOnAnimation();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>This version also clamps the scrolling to the bounds of our child.
     */
    @Override
    public void scrollTo(int x, int y) {
        x = clamp(x, getWidth(), mContentWidth);
        y = clamp(y, getHeight(), mContentHeight);
        if (x != getScrollX() || y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    private static int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            /* my >= child is this case:
             *                    |--------------- me ---------------|
             *     |------ child ------|
             * or
             *     |--------------- me ---------------|
             *            |------ child ------|
             * or
             *     |--------------- me ---------------|
             *                                  |------ child ------|
             *

             * n < 0 is this case:
             *     |------ me ------|
             *                    |-------- child --------|
             *     |-- mScrollX --|
             */
            return 0;
        }
        if ((my + n) > child) {
            /* this case:
             *                    |------ me ------|
             *     |------ child ------|
             *     |-- mScrollX --|
             */
            return child - my;
        }
        return n;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void updateDragState(boolean isBeingDragged) {
        mIsBeingDragged = isBeingDragged;
        if (mListener != null) {
            if (mIsBeingDragged) {
                mListener.onDragStart();
            } else {
                mListener.onDragEnd();
            }
        }
    }

    protected void drawShadedBackground(Canvas canvas, ColorInterpolator interpolator,
            ScrollPage page) {
        // Determine bounds of the shaded region.
        int rectTop =
                page.isScrolledToTop() ? (getScrollY() + getPaddingTop()) : page.getYPosition();
        int rectLeft = getPaddingStart();
        int rectRight = getMeasuredWidth() - getPaddingEnd();
        int rectBottom = page.isScrolledToTop() ? rectTop + page.getHeight() :
                interpolator.getValue() + rectTop - getPaddingBottom();
        Rect shadeRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        // Compute shade based on interpolation.
        Paint paint = new Paint();
        paint.setColor(interpolator.getInterpolatedShade());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadeRect, paint);
    }

    public int getContentHeight() {
        return mContentHeight;
    }

    protected void setContentHeight(int contentHeight) {
        mContentHeight = contentHeight;
    }

    public int getContentWidth() {
        return mContentWidth;
    }

    public void setContentWidth(int contentWidth) {
        mContentWidth = contentWidth;
    }

    public boolean isInitializedPages() {
        return mInitializedPages;
    }

    public void setInitializedPages(boolean initializedPages) {
        mInitializedPages = initializedPages;
    }

    public ScrollListener getListener() {
        return mListener;
    }

    public void setListener(ScrollListener listener) {
        mListener = listener;
    }

    public interface ScrollListener {

        void onDragStart();

        void onDragEnd();
    }
}
