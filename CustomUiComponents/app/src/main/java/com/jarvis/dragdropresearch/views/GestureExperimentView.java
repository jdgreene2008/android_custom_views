package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class GestureExperimentView extends FrameLayout {
    private static final String TAG = GestureExperimentView.class.getName();
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

    private int mContentHeight = 5000;
    private int mContentWidth = 8000;

    private OverScroller mScroller;

    private List<List<MovableObject>> mMovableObjectRails;
    private List<ColorInterpolator> mColorInterpolators;

    private static final int[] COLORS_OBJECTS =
            new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.MAGENTA, Color.CYAN, Color.LTGRAY};

    private ScrollListener mListener;

    /**
     * Position of the last motion event.
     */
    private int mLastMotionY;

    private boolean mInitializedObjects;

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

    public GestureExperimentView(@NonNull Context context) {
        super(context);
        initGestureExperimentView();
    }

    public GestureExperimentView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        initGestureExperimentView();
    }

    public GestureExperimentView(@NonNull Context context, @Nullable AttributeSet attrs,
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

    public ScrollListener getListener() {
        return mListener;
    }

    public void setListener(ScrollListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mInitializedObjects) {
            initializeObjects();
        }
    }

    private void initializeObjects() {
        mInitializedObjects = true;
        final int railCount = 3;
        mMovableObjectRails = new ArrayList<>(railCount);

        final int gapY = 100;
        final int gapX = 200;

        for (int g = 0; g < railCount; g++) {
            ArrayList<MovableObject> rail = new ArrayList<>();
            final int railY = (g + 1) * getMeasuredHeight() - 100 * g;
            final int railX = ((g == 0) ? gapX :
                    ((g == railCount - 1) ? (getMeasuredWidth() - gapX) :
                            (gapX + getMeasuredWidth() / railCount)));
            for (int i = 0; i < 5; i++) {
                int color = COLORS_OBJECTS[(i % COLORS_OBJECTS.length)];
                rail.add(new MovableObject(railX, railY + gapY * i,
                        "1", color));
            }
            mMovableObjectRails.add(rail);
        }

        mContentHeight = getMeasuredHeight() * (2 + railCount) - (100 * railCount);
        mContentWidth = getMeasuredWidth() * 2;

        mColorInterpolators = new ArrayList<>(railCount);

        for (int i = 0; i < railCount; i++) {
            ColorInterpolator interpolator = new ColorInterpolator(getMeasuredHeight());
            interpolator.setColor(COLORS_BACKGROUNDS[i]);
            mColorInterpolators.add(interpolator);
        }
    }

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
        if (mMovableObjectRails != null) {
            for (int i = 0; i < mMovableObjectRails.size(); i++) {
                drawRail(canvas, mMovableObjectRails.get(i), mColorInterpolators.get(i));
            }
        }

        super.onDraw(canvas);
    }

    private void drawRail(Canvas canvas, List<MovableObject> objects,
            ColorInterpolator railBackground) {
        final int offset = 25;
        final int radius = 50;
        int count = 0;

        Paint paint = new Paint();
        for (MovableObject object : objects) {
            paint.setColor(object.getColor());
            final int objectIndex = objects.indexOf(object);

            if (object.getYPos() > (getScrollY() + getHeight())) {
                // Check to see if this is the first one. If it is, we can return immediately.

                if (objectIndex == 0) break;
                continue;
            }
            if (objectIndex == 0) {
                railBackground.setMaxValue(getMeasuredHeight());
                railBackground.updateValue(
                        railBackground.mMaxValue - (object.getYPos() - getScrollY()));
            }
            if (getScrollY() >= object.getYPos()) {
                /* Means we've scrolled to the top of the visible part of the view.
                  If the first object is being drawn, draw the background rectangle based on that
                  knowledge since it must be drawn behind the views. */
                int yPosition = getScrollY() + (offset * count);

                if (objectIndex == 0) {
                    // Background must be drawn before anything else.
                    drawShadedBackground(canvas, railBackground, yPosition);
                }

                canvas.drawCircle(object.getXPos(),
                        getScrollY() + (offset * count), radius, paint);
            } else {
                int yPosition = object.getYPos();

                if (objectIndex == 0) {
                    drawShadedBackground(canvas, railBackground, yPosition - radius);
                }

                canvas.drawCircle(object.getXPos(), yPosition, radius, paint);
            }

            count++;
        }
    }

    private void drawShadedBackground(Canvas canvas, ColorInterpolator interpolator,
            int yPosition) {
        // Determine bounds of the shaded region.
        int rectTop = yPosition;
        int rectLeft = 0;
        int rectRight = getMeasuredWidth();
        int rectBottom = interpolator.getValue() + rectTop + 50;
        Rect shadeRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        // Compute shade based on interpolation.
        int shade = ColorUtils.setAlphaComponent(interpolator.mColor,
                (int)(interpolator.getInterpolatedValue() * 255));

        Paint paint = new Paint();
        paint.setColor(shade);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadeRect, paint);
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

    public static class MovableObject {

        private int mXPos;
        private int mYPos;
        private String mId;
        private int mColor;

        public MovableObject(int initialX, int initialY, String id, int color) {
            mXPos = initialX;
            mYPos = initialY;
            mId = id;
            mColor = color;
        }

        public int getXPos() {
            return mXPos;
        }

        public void setXPos(int XPos) {
            mXPos = XPos;
        }

        public int getYPos() {
            return mYPos;
        }

        public void setYPos(int YPos) {
            mYPos = YPos;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int color) {
            mColor = color;
        }
    }

    public static class ColorInterpolator {

        private int mMaxValue;
        private int mValue;
        private float mInterpolatedValue;
        private int mColor = Color.BLACK;

        ColorInterpolator(int maxValue) {
            this.mMaxValue = maxValue;
        }

        int getValue() {
            return mValue;
        }

        public void setMaxValue(int maxValue) {
            mMaxValue = maxValue;
        }

        void updateValue(int value) {
            mValue = value;
            calculateInterpolatedValue();
        }

        private void calculateInterpolatedValue() {
            if (mValue >= mMaxValue) {
                mInterpolatedValue = 1.0f;
            } else if (mMaxValue <= 0) {
                mInterpolatedValue = 0f;
            } else {
                mInterpolatedValue = ((float)Math.abs(mValue) / (float)mMaxValue);
            }
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int color) {
            mColor = color;
        }

        public float getInterpolatedValue() {
            return mInterpolatedValue;
        }
    }

    public interface ScrollListener {

        void onDragStart();

        void onDragEnd();
    }
}
