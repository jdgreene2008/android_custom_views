package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.funwithshapes.FlashShape;
import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class ShapeFlashView extends AbsCustomScrollingView<FlashShapePage> {

    private static final String TAG = ShapeFlashView.class.getName();
    private static final int PAGE_COUNT = 3;
    private static final int[] COLORS_BACKGROUNDS =
            new int[] { Color.CYAN, Color.LTGRAY, Color.BLACK};
    private static final int[] SHAPE_COLORS = new int[]{Color.RED,Color.WHITE,Color.BLUE};

    private int mMaxShapeWidth;
    private int mMaxShapeHeight;

    public ShapeFlashView(@NonNull Context context) {
        super(context);
    }

    public ShapeFlashView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeFlashView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initializePages() {
        setInitializedPages(true);
        setupPages();
    }

    private void setupPages() {
        mPages = new ArrayList<>(PAGE_COUNT);
        mMaxShapeHeight = getMeasuredWidth() / 2;
        mMaxShapeWidth = getMeasuredWidth() / 2;

        // Assumes fixed-size pages for now.
        final Random random = new Random(System.currentTimeMillis());
        for (int i = 1; i <= PAGE_COUNT; i++) {
            FlashShapePage page = new FlashShapePage();
            page.setHeight(getMeasuredHeight());
            page.setWidth(getMeasuredWidth());
            page.setXPosition(0);
            page.setYPosition(i * getMeasuredHeight());
            mPages.add(page);

            FlashShape shape = new FlashShape(FlashShape.Type.ARC);
            shape.setXOffset(getMeasuredWidth() / 2 - mMaxShapeWidth / 2);
            shape.setYOffset(getMeasuredHeight() / 2 - mMaxShapeHeight / 2);

            AngleInterpolator angleInterpolator = new AngleInterpolator(getMeasuredHeight());
            angleInterpolator.setMaxAngle(360.0f);
            shape.setAngleInterpolator(angleInterpolator);

            ColorInterpolator shapeColorInterpolator = new ColorInterpolator(getMeasuredHeight());
            shapeColorInterpolator.setColor(SHAPE_COLORS[i - 1]);
            shape.setColorInterpolator(shapeColorInterpolator);

            page.setFlashShape(shape);

            ColorInterpolator pageBackgroundInterpolator = new ColorInterpolator(page.getHeight());
            pageBackgroundInterpolator.setColor(COLORS_BACKGROUNDS[i - 1]);
            page.setBackgroundColorInterpolator(pageBackgroundInterpolator);
        }

        setContentHeight(getMeasuredHeight() * (PAGE_COUNT + 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPages(canvas);
        super.onDraw(canvas);
    }

    private void drawPages(Canvas canvas) {
        if (mPages != null) {
            for (FlashShapePage page : mPages) {
                drawBackground(canvas, page);
                drawPageShape(page, canvas);
            }
        }
    }

    private void drawPageShape(FlashShapePage page, Canvas canvas) {
        if (!page.isVisible()) {
            return;
        }

        FlashShape shape = page.getFlashShape();

        if (page.getYPosition() + shape.getYOffset() > getMeasuredHeight() + getScrollY()) {
            // Shape has not come into view. Do not draw.
            return;
        }

        Paint paint = new Paint();

        if (getScrollY() >= page.getYPosition() + shape.getYOffset()) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now
            ColorInterpolator colorInterpolator = shape.getColorInterpolator();
            AngleInterpolator angleInterpolator = shape.getAngleInterpolator();

            // Draw the arc
            float boundingRectTop = getScrollY() + shape.getYOffset();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            RectF ovalBounds = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(ovalBounds, 0, angleInterpolator.getInterpolatedAngle(), true, paint);
        } else {

            ColorInterpolator colorInterpolator = shape.getColorInterpolator();
            colorInterpolator.updateValue(
                    colorInterpolator.getMaxValue() - (page.getYPosition() - getScrollY()));
            AngleInterpolator angleInterpolator = shape.getAngleInterpolator();
            angleInterpolator.updateValue(
                    angleInterpolator.getMaxValue() - (page.getYPosition() - getScrollY()));

            // Draw the arc
            float boundingRectTop = page.getYPosition() + shape.getYOffset();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            RectF ovalBounds = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(ovalBounds, 0, angleInterpolator.getInterpolatedAngle(), true, paint);
        }
    }

    private void drawBackground(Canvas canvas, FlashShapePage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getBackgroundColorInterpolator();
            interpolator
                    .updateValue(interpolator.getMaxValue() - (page.getYPosition() - getScrollY()));
            drawShadedBackground(canvas, interpolator, page.getYPosition());
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
        Paint paint = new Paint();
        paint.setColor(interpolator.getInterpolatedShade());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadeRect, paint);
    }
}
