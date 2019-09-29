package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class ScrollingRailsView extends CustomScrollingView {

    private List<List<MovableObject>> mMovableObjectRails;
    private List<ColorInterpolator> mColorInterpolators;

    private static final int[] COLORS_OBJECTS =
            new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.MAGENTA, Color.CYAN, Color.LTGRAY};

    public ScrollingRailsView(Context context) {
        super(context);
    }

    public ScrollingRailsView(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingRailsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initializeObjects() {
        setInitializedObjects(true);
        positionObjects(getMeasuredWidth(), getMeasuredHeight());
    }

    private void positionObjects(int viewWidth, int viewHeight) {
        final int railCount = 3;
        mMovableObjectRails = new ArrayList<>(railCount);

        final int gapY = 100;
        final int gapX = 200;

        for (int g = 0; g < railCount; g++) {
            ArrayList<MovableObject> rail = new ArrayList<>();

            final int railY = (g + 1) * viewHeight - 100 * g;
            final int railX = ((g == 0) ? gapX :
                    ((g == railCount - 1) ? (viewWidth - gapX) :
                            (gapX + viewWidth / railCount)));
            for (int i = 0; i < 5; i++) {
                int color = COLORS_OBJECTS[(i % COLORS_OBJECTS.length)];
                rail.add(new MovableObject(railX, railY + gapY * i,
                        "1", color));
            }
            mMovableObjectRails.add(rail);
        }

        setContentHeight(viewHeight * (2 + railCount) - (100 * railCount));
        setContentWidth(viewWidth * 2);

        mColorInterpolators = new ArrayList<>(railCount);

        for (int i = 0; i < railCount; i++) {
            ColorInterpolator interpolator = new ColorInterpolator(viewHeight);
            interpolator.setColor(COLORS_BACKGROUNDS[i]);
            mColorInterpolators.add(interpolator);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovableObjectRails != null) {
            for (int i = 0; i < mMovableObjectRails.size(); i++) {
                drawBackground(canvas, mMovableObjectRails.get(i), mColorInterpolators.get(i));
            }
            for (int i = 0; i < mMovableObjectRails.size(); i++) {
                drawRail(canvas, mMovableObjectRails.get(i));
            }
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        positionObjects(w, h);
        invalidate();
    }

    private void drawRail(Canvas canvas, List<MovableObject> objects) {
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

            if (getScrollY() >= object.getYPos()) {
                /* Means we've scrolled to the top of the visible part of the object to draw. */
                canvas.drawCircle(object.getXPos(),
                        getScrollY() + (offset * count), radius, paint);
            } else {
                int yPosition = object.getYPos();
                canvas.drawCircle(object.getXPos(), yPosition, radius, paint);
            }

            count++;
        }
    }

    /**
     * Separate the background drawing logic from the rail drawing logic. Backgrounds should
     * be drawn before rail drawing starts.
     */
    private void drawBackground(Canvas canvas, List<MovableObject> objects,
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
                railBackground.updateValue(
                        railBackground.mMaxValue - (object.getYPos() - getScrollY()));
            }
            if (getScrollY() >= object.getYPos()) {
                int yPosition = getScrollY() + (offset * count);
                if (objectIndex == 0) {
                    drawShadedBackground(canvas, railBackground, yPosition);
                }
            } else {
                int yPosition = object.getYPos();
                if (objectIndex == 0) {
                    drawShadedBackground(canvas, railBackground, yPosition - radius);
                }
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
}
