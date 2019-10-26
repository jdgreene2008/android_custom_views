package com.jarvis.dragdropresearch.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.Pair;

import com.jarvis.dragdropresearch.funwithshapes.ArcShape;
import com.jarvis.dragdropresearch.funwithshapes.RectangleShape;
import com.jarvis.dragdropresearch.funwithshapes.SpiralArcDescriptor;
import com.jarvis.dragdropresearch.funwithshapes.SpiralSegment;
import com.jarvis.dragdropresearch.funwithshapes.SpiralShape;
import com.jarvis.dragdropresearch.funwithshapes.StarShape;
import com.jarvis.dragdropresearch.funwithshapes.TriangleShape;
import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;
import com.jarvis.dragdropresearch.interpolators.RectangleInterpolator;
import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;
import com.jarvis.dragdropresearch.interpolators.StarInterpolator;
import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of utility functions for managing drawing of certain objects.
 */
public class DrawingUtils {

    private DrawingUtils() {

    }

    /**
     * Draw a {@link ArcShape} within the given bounds.
     *
     * @param canvas {@link Canvas} upon which to draw the arc.
     * @param shape
     * @param boundingRect {@link RectF} representing the bounds within the canvas.
     * @param paint
     */
    public static void drawArcShape(Canvas canvas, ArcShape shape, RectF boundingRect,
            Paint paint) {
        AngleInterpolator angleInterpolator = shape.getAngleInterpolator();

        List<Pair<Float, Float>> drawingAngles =
                angleInterpolator.getDrawingDescriptor().getDrawingAngles();

        if (drawingAngles == null || drawingAngles.isEmpty()) return;

        for (int i = 0; i < drawingAngles.size(); i++) {
            Pair<Float, Float> current = drawingAngles.get(i);
            if (shape.allowMultiColoredComponents()) {
                paint.setColor(shape.getComponentColors()[i % shape.getComponentColors().length]);
            }
            canvas.drawArc(boundingRect, current.first,
                    current.second, true, paint);
        }
    }

    /**
     * Draw a {@link RectangleShape} onto {@link Canvas} within provided bounds.
     *
     * @param canvas {@link Canvas} upon which to draw the arc.
     * @param shape
     * @param bounds {@link RectF} representing the bounds within the canvas.
     * @param paint
     */
    public static void drawRectangleShape(Canvas canvas, RectangleShape shape, RectF bounds,
            Paint paint) {
        RectangleInterpolator interpolator = shape.getRectangleInterpolator();
        RectangleInterpolator.DrawingDescriptor drawingDescriptor =
                interpolator.getDrawingDescriptor();
        List<RectF> components = drawingDescriptor.getRectangleComponents();

        int[] componentColors = shape.getComponentColors();
        boolean allowMultiColoredComponents = shape.allowMultiColoredComponents();
        int count = 0;
        for (RectF component : components) {
            if (allowMultiColoredComponents) {
                paint.setColor(componentColors[count++ % componentColors.length]);
            }
            canvas.drawRect(bounds.left + component.left, bounds.top + component.top,
                    bounds.left + component.right,
                    bounds.top + component.bottom, paint);
        }
    }

    /**
     * Draw a {@link TriangleShape} onto {@link Canvas} within provided bounds.
     *
     * @param canvas {@link Canvas} upon which to draw the arc.
     * @param shape
     * @param bounds {@link RectF} representing the bounds within the canvas.
     * @param paint
     */
    public static void drawTriangleShape(Canvas canvas, TriangleShape shape, RectF bounds,
            Paint paint) {
        TriangleInterpolator triangleInterpolator = shape.getTriangleInterpolator();
        float baseInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_BASE];
        float altitudeInterpolation = triangleInterpolator
                .getInterpolatedValues()[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];
        int[] colors = new int[] {paint.getColor(), paint.getColor()};

        if (shape.allowMultiColoredComponents()
                && triangleInterpolator.isSymmetric()) {
            int[] componentColors = shape.getComponentColors();
            colors[0] = shape.getComponentColors()[0 % componentColors.length];
            colors[1] = shape.getComponentColors()[1 % componentColors.length];
        }

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

        paint.setColor(colors[0]);
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

            paint.setColor(colors[1]);
            canvas.drawPath(rightTriangle, paint);
        }
    }

    /**
     * Draw a {@link SpiralShape} onto {@link Canvas} within provided bounds.
     *
     * @param canvas {@link Canvas} upon which to draw the arc.
     * @param shape
     * @param bounds {@link RectF} representing the bounds within the canvas.
     * @param paint
     */
    public static void drawSpiralShape(Canvas canvas, SpiralShape shape, RectF bounds,
            Paint paint) {
        SpiralInterpolator interpolator = shape.getSpiralInterpolator();
        Path path = new Path();

        List<SpiralArcDescriptor> arcDescriptors = new ArrayList<>();
        List<SpiralSegment> segments = interpolator.getSegments();
        if (segments == null || segments.isEmpty()) {
            return;
        }

        // TODO: Populate array of random colors during initial setup to avoid excess
        // object allocation in onDraw().

        for (int i = 0; i < segments.size(); i++) {
            final SpiralSegment segment = segments.get(i);

            if (segment.getType() == SpiralSegment.Type.TOP) {
                addTopSpiralSegment(path, arcDescriptors, bounds, segment);
            } else {
                addBottomSpiralSegment(path, arcDescriptors, bounds, segment);
            }
        }

        final int defaultColor = paint.getColor();

        final int[] segmentColors = shape.getComponentColors();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                shape.allowMultiColoredComponents()) {
            for (int i = 0; i < arcDescriptors.size(); i++) {
                SpiralArcDescriptor descriptor = arcDescriptors.get(i);

                // Set segment color from poll of available segment colors.
                int segmentColor = segmentColors[i % segmentColors.length];
                if (shape.allowMultiColoredComponents() &&
                        segmentColor != SpiralSegment.SEGMENT_COLOR_DEFAULT) {
                    paint.setColor(segmentColor);
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

    private static void addTopSpiralSegment(Path path,
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

    private static void addBottomSpiralSegment(Path path,
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

    /**
     * Draw a {@link StarShape} onto {@link Canvas} within provided bounds.
     *
     * @param canvas {@link Canvas} upon which to draw the star.
     * @param shape
     * @param bounds {@link RectF} representing the bounds within the canvas.
     * @param paint
     */
    public static void drawStarShape(Canvas canvas, StarShape shape, RectF bounds,
            Paint paint) {
        StarInterpolator interpolator = shape.getStarInterpolator();
        StarInterpolator.DrawingDescriptor descriptor = interpolator.getDrawingDescriptor();

        Path path = new Path();
        int[] componentColors = shape.getComponentColors();
        if (!shape.allowMultiColoredComponents()) {
            Arrays.fill(componentColors, paint.getColor());
        }

        int colorIndex = 0;
        // Draw top triangle
        path.moveTo(bounds.left + descriptor.getTopTrianglePeak().x,
                bounds.bottom - descriptor.getTopTrianglePeak().y);
        path.lineTo(bounds.left + descriptor.getTopTriangleRightVertex().x,
                bounds.bottom - descriptor.getTopTriangleRightVertex().y);
        path.lineTo(bounds.left + descriptor.getTopTriangleLeftVertex().x,
                bounds.bottom - descriptor.getTopTriangleLeftVertex().y);
        path.close();
        paint.setColor(componentColors[colorIndex++ % componentColors.length]);
        canvas.drawPath(path, paint);

        path.reset();

        // Draw left triangle
        path.moveTo(bounds.left + descriptor.getLeftTrianglePeak().x,
                bounds.bottom - descriptor.getLeftTrianglePeak().y);
        path.lineTo(bounds.left + descriptor.getLeftTriangleTopVertex().x,
                bounds.bottom - descriptor.getLeftTriangleTopVertex().y);
        path.lineTo(bounds.left + descriptor.getLeftTriangleBottomVertex().x,
                bounds.bottom - descriptor.getLeftTriangleBottomVertex().y);
        path.close();
        paint.setColor(componentColors[colorIndex++ % componentColors.length]);
        canvas.drawPath(path, paint);

        path.reset();

        // Draw right triangle
        path.moveTo(bounds.left + descriptor.getRightTrianglePeak().x,
                bounds.bottom - descriptor.getRightTrianglePeak().y);
        path.lineTo(bounds.left + descriptor.getRightTriangleTopVertex().x,
                bounds.bottom - descriptor.getRightTriangleTopVertex().y);
        path.lineTo(bounds.left + descriptor.getRightTriangleBottomVertex().x,
                bounds.bottom - descriptor.getRightTriangleBottomVertex().y);
        path.close();
        paint.setColor(componentColors[colorIndex++ % componentColors.length]);
        canvas.drawPath(path, paint);

        path.reset();

        // Draw bottom right triangle
        path.moveTo(bounds.left + descriptor.getBottomRightTrianglePeak().x,
                bounds.bottom - descriptor.getBottomRightTrianglePeak().y);
        path.lineTo(bounds.left + descriptor.getBottomRightTriangleUpperLeftVertex().x,
                bounds.bottom - descriptor.getBottomRightTriangleUpperLeftVertex().y);
        path.lineTo(bounds.left + descriptor.getBottomRightTriangleUpperRightVertex().x,
                bounds.bottom - descriptor.getBottomRightTriangleUpperRightVertex().y);
        path.close();
        paint.setColor(componentColors[colorIndex++ % componentColors.length]);
        canvas.drawPath(path, paint);

        path.reset();

        // Draw bottom left triangle
        path.moveTo(bounds.left + descriptor.getBottomLeftTrianglePeak().x,
                bounds.bottom - descriptor.getBottomLeftTrianglePeak().y);
        path.lineTo(bounds.left + descriptor.getBottomLeftTriangleUpperLeftVertex().x,
                bounds.bottom - descriptor.getBottomLeftTriangleUpperLeftVertex().y);
        path.lineTo(bounds.left + descriptor.getBottomLeftTriangleUpperRightVertex().x,
                bounds.bottom - descriptor.getBottomLeftTriangleUpperRightVertex().y);
        path.close();
        paint.setColor(componentColors[colorIndex % componentColors.length]);
        canvas.drawPath(path, paint);
    }
}
