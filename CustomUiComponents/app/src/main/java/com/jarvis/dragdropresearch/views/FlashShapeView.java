package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.funwithshapes.ArcShape;
import com.jarvis.dragdropresearch.funwithshapes.FlashShape;
import com.jarvis.dragdropresearch.funwithshapes.RectangleShape;
import com.jarvis.dragdropresearch.funwithshapes.SpiralArcDescriptor;
import com.jarvis.dragdropresearch.funwithshapes.SpiralSegment;
import com.jarvis.dragdropresearch.funwithshapes.SpiralShape;
import com.jarvis.dragdropresearch.funwithshapes.TriangleShape;
import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.interpolators.RectangleInterpolator;
import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;
import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
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

            FlashShape shape;
            if (i % 4 == 0) {
                shape = getArcShape(page);
            } else if (i % 4 == 1) {
                shape = getTriangleShape(page);
            } else if (i % 4 == 2) {
                shape = getRectangleShape(page);
            } else {
                shape = getSpiralShape(page);
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

    private TriangleShape getTriangleShape(FlashShapePage page) {
        Random random = new Random();
        TriangleShape shape = new TriangleShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));

        TriangleInterpolator interpolator =
                new TriangleInterpolator(page.getHeight(), mMaxShapeHeight, mMaxShapeWidth,
                        random.nextInt(500) % 10 < 5);
        shape.setTriangleInterpolator(interpolator);
        return shape;
    }

    private SpiralShape getSpiralShape(FlashShapePage page) {
        SpiralShape shape = new SpiralShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));
        shape.generateRandomSegmentColors();

        SpiralInterpolator interpolator =
                new SpiralInterpolator(page.getHeight(), (int)mMaxShapeHeight, (int)mMaxShapeWidth);
        interpolator.setAllowMulticoloredSegments(true);
        shape.setSpiralInterpolator(interpolator);
        return shape;
    }

    private RectangleShape getRectangleShape(FlashShapePage page) {
        Random random = new Random();
        RectangleShape shape = new RectangleShape();
        shape.setXOffset((int)(page.getWidth() / 2 -
                mMaxShapeWidth / 2));
        shape.setYOffset((int)(page.getHeight() / 2 - mMaxShapeHeight / 2));

        RectangleInterpolator interpolator =
                new RectangleInterpolator(page.getHeight(), mMaxShapeHeight, mMaxShapeWidth,
                        random.nextInt(500) % 10 < 5);
        shape.setRectangleInterpolator(interpolator);
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
        }
    }

    //region Arc Drawing
    private void drawArcShape(Canvas canvas, FlashShapePage page) {
        ArcShape shape = (ArcShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        AngleInterpolator angleInterpolator = shape.getAngleInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now

            // Draw the arc
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(getCommonShapeBoundingRect(page, shape, true), 0,
                    angleInterpolator.getInterpolatedAngle(), true, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            angleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());

            // Draw the arc
            paint.setColor(colorInterpolator.getInterpolatedShade());

            canvas.drawArc(getCommonShapeBoundingRect(page, shape, false), 0,
                    angleInterpolator.getInterpolatedAngle(), true, paint);
        }
    }
    //endregion

    //region Rectangle Drawing
    private void drawRectangleShape(Canvas canvas, FlashShapePage page) {
        RectangleShape shape = (RectangleShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        RectangleInterpolator rectangleInterpolator = shape.getRectangleInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top top of the visible part of the view.
            // Only drawing arcs for now

            // Draw the rectangle.
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawRectangleShape(canvas, getCommonShapeBoundingRect(page, shape, true),
                    rectangleInterpolator, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            rectangleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());

            // Draw the rectangle
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawRectangleShape(canvas, getCommonShapeBoundingRect(page, shape, false),
                    rectangleInterpolator, paint);
        }
    }

    private void drawRectangleShape(Canvas canvas, RectF bounds,
            RectangleInterpolator interpolator, Paint paint) {
        float[] interpolatedDimensions = interpolator.getInterpolatedDimensions();
        float interpolatedWidth =
                interpolatedDimensions[RectangleInterpolator.INTERPOLATION_VALUES_WIDTH];
        float interpolatedHeight =
                interpolatedDimensions[RectangleInterpolator.INTERPOLATION_VALUES_HEIGHT];

        if (!interpolator.isSymmetric()) {
            float top = bounds.bottom - interpolatedHeight;
            float bottom = bounds.bottom;
            float left = bounds.left;
            float right = left + interpolatedWidth;

            canvas.drawRect(left, top, right, bottom, paint);
        } else {
            // Bottom Left Rectangle
            float top = bounds.bottom - interpolatedHeight;
            float bottom = bounds.bottom;
            float left = bounds.left;
            float right = left + interpolatedWidth;

            canvas.drawRect(left, top, right, bottom, paint);

            // Bottom Right Rectangle
            top = bounds.bottom - interpolatedHeight;
            bottom = bounds.bottom;
            left = bounds.right - interpolatedWidth;
            right = bounds.right;

            canvas.drawRect(left, top, right, bottom, paint);

            // Top Left Rectangle
            top = bounds.top;
            bottom = bounds.top + interpolatedHeight;
            left = bounds.left;
            right = bounds.left + interpolatedWidth;

            canvas.drawRect(left, top, right, bottom, paint);

            // Top Right Rectangle
            top = bounds.top;
            bottom = bounds.top + interpolatedHeight;
            left = bounds.right - interpolatedWidth;
            right = bounds.right;

            canvas.drawRect(left, top, right, bottom, paint);
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

            drawTriangleShape(canvas, getCommonShapeBoundingRect(page, shape, true),
                    triangleInterpolator, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            triangleInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawTriangleShape(canvas, getCommonShapeBoundingRect(page, shape, false),
                    triangleInterpolator, paint);
        }
    }

    private void drawTriangleShape(Canvas canvas, RectF bounds,
            TriangleInterpolator triangleInterpolator, Paint paint) {
        float baseInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_BASE];
        float altitudeInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];

        // *** Construct the Left Triangle ** //

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

        if (triangleInterpolator.isSymmetric()) {
            // *** Construct the Right Triangle ** //

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
    //endregion

    //region Spiral Shape Drawing
    private void drawSpiralShape(Canvas canvas, FlashShapePage page) {
        SpiralShape shape = (SpiralShape)page.getFlashShape();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setStrokeWidth(10);
        ColorInterpolator colorInterpolator = shape.getColorInterpolator();
        SpiralInterpolator spiralInterpolator = shape.getSpiralInterpolator();

        if (pageShapeScrolledToTop(page, shape)) {
            // Means we've scrolled the current page to the top of the visible part of the view.
            // Only drawing arcs for now
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawSpiralShape(canvas, shape, getCommonShapeBoundingRect(page, shape, true),
                    spiralInterpolator, paint);
        } else {
            colorInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            spiralInterpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
            paint.setColor(colorInterpolator.getInterpolatedShade());

            drawSpiralShape(canvas, shape, getCommonShapeBoundingRect(page, shape, false),
                    spiralInterpolator, paint);
        }
    }

    private void drawSpiralShape(Canvas canvas, SpiralShape shape, RectF bounds,
            SpiralInterpolator interpolator,
            Paint paint) {
        Path path = new Path();


        List<SpiralArcDescriptor> arcDescriptors = new ArrayList<>();
        List<SpiralSegment> segments = interpolator.getSegments();
        if (segments == null || segments.isEmpty()) {
            return;
        }

        // TODO: Populate array of random colors during initial setup to avoid excess
        // object allocation in onDraw().

        final int[] segmentColors = shape.getSegmentColors();
        for (int i = 0; i < segments.size(); i++) {
            final SpiralSegment segment = segments.get(i);

            // Set segment color from poll of available segment colors.
            if (interpolator.isAllowMulticoloredSegments()) {
                segment.setColor(segmentColors[i % segmentColors.length]);
            }

            if (segment.getType() == SpiralSegment.Type.TOP) {
                addTopSpiralSegment(path, arcDescriptors, bounds, segment);
            } else {
                addBottomSpiralSegment(path, arcDescriptors, bounds, segment);
            }
        }

        final int defaultColor = paint.getColor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                interpolator.isAllowMulticoloredSegments()) {
            for (int i = 0; i < arcDescriptors.size(); i++) {
                SpiralArcDescriptor descriptor = arcDescriptors.get(i);

                int selectedColor = segments.get(i).getColor();
                if (selectedColor != SpiralSegment.SEGMENT_COLOR_DEFAULT) {
                    paint.setColor(selectedColor);
                }

                canvas.drawArc(descriptor.getLeft(), descriptor.getTop(), descriptor.getRight(),
                        descriptor.getBottom(), descriptor.getStartAngle(),
                        descriptor.getSweepAngle(), false, paint);

                paint.setColor(defaultColor);
            }
        } else {
            canvas.drawPath(path, paint);
        }
    }

    private void addTopSpiralSegment(Path path,
            List<SpiralArcDescriptor> arcDescriptors,
            RectF bounds, SpiralSegment spiralSegment) {
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();

        float segmentBoundsLeft;
        float segmentBoundsRight;
        float segmentBoundsTop;
        float segmentBoundsBottom;

        if (path.isEmpty()) {
            segmentBoundsLeft = centerX - spiralSegment.getWidth() / 2;
            segmentBoundsRight = centerX + spiralSegment.getWidth() / 2;
            segmentBoundsTop = centerY - spiralSegment.getHeight() / 2;
            segmentBoundsBottom = centerY + spiralSegment.getHeight() / 2;
        } else {
            RectF pathBounds = new RectF();
            path.computeBounds(pathBounds, true);

            segmentBoundsLeft = pathBounds.left;
            segmentBoundsRight = segmentBoundsLeft + spiralSegment.getWidth();
            segmentBoundsTop = centerY - spiralSegment.getHeight() / 2;
            segmentBoundsBottom = centerY + spiralSegment.getHeight() / 2;
        }

        arcDescriptors.add(new SpiralArcDescriptor(segmentBoundsLeft, segmentBoundsTop,
                segmentBoundsBottom, segmentBoundsRight, 180,
                180));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path.addArc(segmentBoundsLeft, segmentBoundsTop, segmentBoundsRight,
                    segmentBoundsBottom, 180, 180);
        }
    }

    private void addBottomSpiralSegment(Path path,
            List<SpiralArcDescriptor> arcDescriptors,
            RectF bounds, SpiralSegment spiralSegment) {
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();

        float segmentBoundsLeft;
        float segmentBoundsRight;
        float segmentBoundsTop;
        float segmentBoundsBottom;

        if (path.isEmpty()) {
            segmentBoundsLeft = centerX - spiralSegment.getWidth() / 2;
            segmentBoundsRight = centerX + spiralSegment.getWidth() / 2;
            segmentBoundsTop = centerY - spiralSegment.getHeight() / 2;
            segmentBoundsBottom = centerY + spiralSegment.getHeight() / 2;
        } else {
            RectF pathBounds = new RectF();
            path.computeBounds(pathBounds, true);

            segmentBoundsLeft = pathBounds.right - spiralSegment.getWidth();
            segmentBoundsRight = pathBounds.right;
            segmentBoundsTop = centerY - spiralSegment.getHeight() / 2;
            segmentBoundsBottom = centerY + spiralSegment.getHeight() / 2;
        }

        arcDescriptors.add(new SpiralArcDescriptor(segmentBoundsLeft, segmentBoundsTop,
                segmentBoundsBottom, segmentBoundsRight, 0,
                180));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path.addArc(segmentBoundsLeft, segmentBoundsTop, segmentBoundsRight,
                    segmentBoundsBottom, 0, 180);
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
            float boundingRectBottom = boundingRectTop + mMaxShapeWidth;

            return new RectF(boundingRectLeft, boundingRectTop, boundingRectRight,
                    boundingRectBottom);
        }
    }

    private boolean pageShapeScrolledToTop(FlashShapePage page, FlashShape shape) {
        return page.getYPosition() + shape.getYOffset() <= getContentBoundsTop();
    }
}
