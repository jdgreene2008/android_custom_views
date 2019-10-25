package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.funwithshapes.ArcShape;
import com.jarvis.dragdropresearch.funwithshapes.FlashShape;
import com.jarvis.dragdropresearch.funwithshapes.RectangleShape;
import com.jarvis.dragdropresearch.funwithshapes.SpiralShape;
import com.jarvis.dragdropresearch.funwithshapes.StarShape;
import com.jarvis.dragdropresearch.funwithshapes.TriangleShape;
import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.interpolators.RectangleInterpolator;
import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;
import com.jarvis.dragdropresearch.interpolators.StarInterpolator;
import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;
import com.jarvis.dragdropresearch.utils.DrawingUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class FlashShapeView extends AbsCustomScrollingView<FlashShapePage> {

    private static final String TAG = FlashShapeView.class.getName();
    private static final int PAGE_COUNT = 50;
    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.LTGRAY, Color.BLACK};
    private static final int[] SHAPE_COLORS =
            new int[] {Color.RED, Color.WHITE, Color.BLUE, Color.GREEN,
                    Color.YELLOW};

    private float mMaxShapeWidth;
    private float mMaxShapeHeight;

    public FlashShapeView(@NonNull Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public FlashShapeView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public FlashShapeView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void initializePages() {
        setInitializedPages(true);
        setupPages();
    }

    private void setupPages() {
        mPages = new ArrayList<>(PAGE_COUNT);
        mMaxShapeHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2f;
        mMaxShapeWidth = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2f;

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

            FlashShape shape;
            if (i % 5 == 0) {
                shape = getArcShape(page);
            } else if (i % 5 == 1) {
                shape = getTriangleShape(page);
            } else if (i % 5 == 2) {
                shape = getRectangleShape(page);
            } else if (i % 5 == 3) {
                shape = getSpiralShape(page);
            } else {
                shape = getStarShape(page);
            }

            ColorInterpolator shapeColorInterpolator = new ColorInterpolator(page.getHeight());
            shapeColorInterpolator
                    .setColor(SHAPE_COLORS[random.nextInt(500) % SHAPE_COLORS.length]);
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

    //region Random FlashShape Generation.
    private TriangleShape getTriangleShape(FlashShapePage page) {
        Random random = new Random(System.currentTimeMillis());
        TriangleShape shape = new TriangleShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));
        shape.generateRandomComponentColors();
        shape.setAllowMulticoloredComponents(true);

        TriangleInterpolator interpolator =
                new TriangleInterpolator(page.getHeight(), mMaxShapeHeight, mMaxShapeWidth,
                        true);
        shape.setTriangleInterpolator(interpolator);
        return shape;
    }

    private SpiralShape getSpiralShape(FlashShapePage page) {
        Random random = new Random();
        SpiralShape shape = new SpiralShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));
        shape.generateRandomComponentColors();
        shape.setAllowMulticoloredComponents(random.nextInt(300) % 10 < 5);

        SpiralInterpolator interpolator =
                new SpiralInterpolator(page.getHeight(), (int)mMaxShapeHeight, (int)mMaxShapeWidth,
                        1 + random.nextInt(300) % shape.getMaxComponents());
        shape.setSpiralInterpolator(interpolator);
        return shape;
    }

    private RectangleShape getRectangleShape(FlashShapePage page) {
        RectangleShape shape = new RectangleShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));
        shape.setAllowMulticoloredComponents(true);
        shape.generateRandomComponentColors();

        RectangleInterpolator interpolator =
                new RectangleInterpolator(page.getHeight(), mMaxShapeHeight, mMaxShapeWidth,
                        true);
        shape.setRectangleInterpolator(interpolator);
        return shape;
    }

    private ArcShape getArcShape(FlashShapePage page) {
        ArcShape shape = new ArcShape();
        shape.setXOffset((int)((page.getWidth() / 2 -
                mMaxShapeWidth / 2) + getPaddingStart()));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2 + getPaddingTop()));
        shape.setAllowMulticoloredComponents(true);
        shape.generateRandomComponentColors();

        AngleInterpolator angleInterpolator = new AngleInterpolator(page.getHeight());
        angleInterpolator.setMaxAngle(360.0f);
        shape.setAngleInterpolator(angleInterpolator);
        return shape;
    }

    private StarShape getStarShape(FlashShapePage page) {
        StarShape shape = new StarShape();
        shape.setXOffset((int)((page.getWidth() / 2 -
                mMaxShapeWidth / 2) + getPaddingStart()));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2 + getPaddingTop()));
        shape.setAllowMulticoloredComponents(true);
        shape.generateRandomComponentColors();

        StarInterpolator.Builder builder = new StarInterpolator.Builder(page.getHeight());
        builder.setHeight(mMaxShapeHeight)
                .setWidth(mMaxShapeWidth);
        shape.setStarInterpolator(builder.build());
        return shape;
    }
    //endregion

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupPages();
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

    private void drawBackground(Canvas canvas, FlashShapePage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getBackgroundColorInterpolator();
            interpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            drawShadedBackground(canvas, interpolator, page);
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
        } else if (shape instanceof RectangleShape) {
            drawRectangleShape(canvas, page);
        } else if (shape instanceof SpiralShape) {
            drawSpiralShape(canvas, page);
        } else if(shape instanceof StarShape){
            drawStarShape(canvas,page);
        }
    }

    //region Arc Drawing
    private void drawArcShape(Canvas canvas, FlashShapePage page) {
        ArcShape shape = (ArcShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now

            // Draw the arc
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils.drawArcShape(canvas, shape, getCommonShapeBoundingRect(page, shape, true),
                    paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            shape.getAngleInterpolator()
                    .updateValue(getContentBoundsBottom() - page.getYPosition());

            // Draw the arc
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils.drawArcShape(canvas, shape, getCommonShapeBoundingRect(page, shape, false),
                    paint);
        }
    }

    //endregion

    //region Rectangle Drawing
    // TODO: Move Individual drawing helpers to a utils class.
    private void drawRectangleShape(Canvas canvas, FlashShapePage page) {
        RectangleShape shape = (RectangleShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now

            // Draw the rectangle.
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils.drawRectangleShape(canvas, shape,
                    getCommonShapeBoundingRect(page, shape, true), paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            shape.getRectangleInterpolator()
                    .updateValue(getContentBoundsBottom() - page.getYPosition());

            // Draw the rectangle
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils.drawRectangleShape(canvas, shape,
                    getCommonShapeBoundingRect(page, shape, false), paint);
        }
    }

    //endregion

    //region Triangle Drawing
    private void drawTriangleShape(Canvas canvas, FlashShapePage page) {
        TriangleShape shape = (TriangleShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        TriangleInterpolator triangleInterpolator = shape.getTriangleInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top of the visible part of the view.
            // Only drawing arcs for now
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils
                    .drawTriangleShape(canvas, shape, getCommonShapeBoundingRect(page, shape, true),
                            paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            triangleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils.drawTriangleShape(canvas, shape,
                    getCommonShapeBoundingRect(page, shape, false), paint);
        }
    }

    //endregion

    //region Spiral Shape Drawing
    private void drawSpiralShape(Canvas canvas, FlashShapePage page) {
        SpiralShape shape = (SpiralShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setStrokeWidth(10);
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils
                    .drawSpiralShape(canvas, shape, getCommonShapeBoundingRect(page, shape, true),
                            paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            shape.getSpiralInterpolator()
                    .updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils
                    .drawSpiralShape(canvas, shape, getCommonShapeBoundingRect(page, shape, false),
                            paint);
        }
    }

    //endregion
    //region Star Shape Drawing
    private void drawStarShape(Canvas canvas, FlashShapePage page) {
        StarShape shape = (StarShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setStrokeWidth(10);
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils
                    .drawStarShape(canvas, shape, getCommonShapeBoundingRect(page, shape, true),
                            paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            shape.getStarInterpolator()
                    .updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            DrawingUtils
                    .drawStarShape(canvas, shape, getCommonShapeBoundingRect(page, shape, false),
                            paint);
        }
    }

    //endregion

    private RectF getCommonShapeBoundingRect(FlashShapePage page, FlashShape shape,
            boolean scrolledToTop) {
        if (scrolledToTop) {
            float boundingRectTop = getContentBoundsTop() + shape.getYOffset();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            return new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
        } else {
            float boundingRectTop = page.getYPosition() + shape.getYOffset() + getPaddingTop();
            float boundingRectLeft = page.getXPosition() + shape.getXOffset();
            float boundingRectRight = boundingRectLeft + mMaxShapeWidth;
            float boundingRectBottom = boundingRectTop + mMaxShapeHeight;

            return new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
        }
    }

    private boolean pageShapeScrolledToTop(FlashShapePage page, FlashShape shape) {
        return page.getYPosition() + shape.getYOffset() <= getContentBoundsTop();
    }
}
