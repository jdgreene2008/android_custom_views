package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.funwithshapes.ArcShape;
import com.jarvis.dragdropresearch.funwithshapes.FlashShape;
import com.jarvis.dragdropresearch.funwithshapes.TriangleShape;
import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class ShapeFlashView extends AbsCustomScrollingView<FlashShapePage> {

    private static final String TAG = ShapeFlashView.class.getName();
    private static final int PAGE_COUNT = 8;
    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.CYAN, Color.LTGRAY, Color.BLACK};
    private static final int[] SHAPE_COLORS = new int[] {Color.RED, Color.WHITE, Color.BLUE};

    private float mMaxShapeWidth;
    private float mMaxShapeHeight;

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
        mMaxShapeHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        mMaxShapeWidth = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2;

        // Assumes fixed-size pages for now.
        final Random random = new Random(System.currentTimeMillis());
        int startPosition = 0;
        for (int i = 1; i <= PAGE_COUNT; i++) {
            FlashShapePage page = new FlashShapePage();
            page.setHeight(getMeasuredHeight() - (getPaddingTop() + getPaddingBottom()));
            page.setWidth(getMeasuredWidth() - (getPaddingStart() + getPaddingEnd()));
            page.setXPosition(getPaddingStart());

            if (startPosition == 0) {
                startPosition = i * (getMeasuredHeight() - getPaddingBottom());
            } else {
                startPosition += page.getHeight();
            }
            page.setYPosition(startPosition);

            mPages.add(page);
            mPages.add(page);

            FlashShape shape;

            if (i % 2 == 0) {
                shape = getArcShape(page);
            } else {
                shape = getTriangleShape(page);
            }

            ColorInterpolator shapeColorInterpolator = new ColorInterpolator(page.getHeight());
            shapeColorInterpolator.setColor(SHAPE_COLORS[(i - 1) % SHAPE_COLORS.length]);
            shape.setColorInterpolator(shapeColorInterpolator);

            page.setFlashShape(shape);

            ColorInterpolator pageBackgroundInterpolator = new ColorInterpolator(page.getHeight());
            pageBackgroundInterpolator
                    .setColor(COLORS_BACKGROUNDS[(i - 1) % COLORS_BACKGROUNDS.length]);
            page.setBackgroundColorInterpolator(pageBackgroundInterpolator);
        }

        setContentHeight(
                (getMeasuredHeight() + getPaddingTop() + getPaddingBottom()) * (PAGE_COUNT + 1));
    }

    private TriangleShape getTriangleShape(FlashShapePage page) {
        TriangleShape shape = new TriangleShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));
        shape.setSymmetric(true);

        float triangleWidth = shape.isSymmetric() ? (mMaxShapeWidth / 2) : mMaxShapeWidth;
        TriangleInterpolator interpolator =
                new TriangleInterpolator(page.getHeight(), mMaxShapeHeight, triangleWidth);
        shape.setTriangleInterpolator(interpolator);
        return shape;
    }

    private ArcShape getArcShape(FlashShapePage page) {
        ArcShape shape = new ArcShape();
        shape.setXOffset((int)((page.getWidth() / 2 -
                mMaxShapeWidth / 2) + getPaddingStart()));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2 + getPaddingTop()));

        AngleInterpolator angleInterpolator = new AngleInterpolator(page.getHeight());
        angleInterpolator.setMaxAngle(360.0f);
        shape.setAngleInterpolator(angleInterpolator);
        return shape;
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
        if (page.getYPosition() + shape.getYOffset() > getContentBoundsBottom()) {
            // Shape has not come into view. Do not draw.
            return;
        }

        if (shape instanceof ArcShape) {
            drawArcShape(canvas, page);
        } else if (shape instanceof TriangleShape) {
            drawTriangleShape(canvas, page);
        }
    }

    private void drawArcShape(Canvas canvas, FlashShapePage page) {
        ArcShape shape = (ArcShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        AngleInterpolator angleInterpolator = shape.getAngleInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now

            // Draw the arc
            float boundingRectTop = getScrollY() + shape.getYOffset() + getPaddingTop();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            RectF ovalBounds = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(ovalBounds, 0, angleInterpolator.getInterpolatedAngle(), true, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            angleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());

            // Draw the arc
            float boundingRectTop = page.getYPosition() + shape.getYOffset() + getPaddingTop();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            RectF ovalBounds = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(ovalBounds, 0, angleInterpolator.getInterpolatedAngle(), true, paint);
        }
    }

    private void drawTriangleShape(Canvas canvas, FlashShapePage page) {
        TriangleShape shape = (TriangleShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        TriangleInterpolator triangleInterpolator = shape.getTriangleInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top of the visible part of the view.
            // Only drawing arcs for now

            float boundingRectTop = getContentBoundsTop() + shape.getYOffset();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            RectF boundingRect = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawTriangleShape(canvas, boundingRect, triangleInterpolator, shape, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            triangleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            float boundingRectTop = page.getYPosition() + shape.getYOffset() + getPaddingTop();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeWidth;

            RectF boundingRect = new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);

            drawTriangleShape(canvas, boundingRect, triangleInterpolator, shape, paint);
        }
    }

    private void drawTriangleShape(Canvas canvas, RectF bounds,
            TriangleInterpolator triangleInterpolator, TriangleShape shape, Paint paint) {
        float baseInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_BASE];
        float altitudeInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];

        // Construct the Left Triangle
        // Bottom left vertex
        float bottomLeftX = bounds.left;
        float bottomLeftY = bounds.bottom;

        // Bottom right corner
        float bottomRightX = bottomLeftX + baseInterpolation;
        float bottomRightY = bounds.bottom;

        //Top Vertex
        float topX = bottomRightX;
        float topY = bottomRightY - altitudeInterpolation;

        Path leftTriangle = new Path();
        leftTriangle.lineTo(bottomLeftX, bottomLeftY);
        leftTriangle.lineTo(bottomRightX, bottomRightY);
        leftTriangle.lineTo(topX, topY);
        leftTriangle.lineTo(bottomLeftX, bottomLeftY);

        canvas.drawPath(leftTriangle, paint);

        if (shape.isSymmetric()) {
            // Construct right triangle.
            bottomLeftX = bounds.right - baseInterpolation;
            bottomLeftY = bounds.bottom;

            bottomRightX = bounds.right;
            bottomRightY = bounds.bottom;

            topX = bottomLeftX;
            topY = bottomRightY - altitudeInterpolation;

            Path rightTriangle = new Path();
            rightTriangle.lineTo(bottomLeftX, bottomLeftY);
            rightTriangle.lineTo(bottomRightX, bottomRightY);
            rightTriangle.lineTo(topX, topY);
            rightTriangle.lineTo(bottomLeftX, bottomLeftY);

            canvas.drawPath(rightTriangle, paint);
        }
    }

    private void drawBackground(Canvas canvas, FlashShapePage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getBackgroundColorInterpolator();
            interpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            drawShadedBackground(canvas, interpolator, page);
        }
    }

    private boolean pageShapeScrolledToTop(FlashShapePage page, FlashShape shape) {
        return page.getYPosition() + shape.getYOffset() <= getContentBoundsTop();
    }
}
