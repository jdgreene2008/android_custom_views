package com.jarvis.dragdropresearch.interpolators;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class RectangleInterpolator extends Interpolator {
    public static final int INTERPOLATION_VALUES_WIDTH = 0;
    public static final int INTERPOLATION_VALUES_HEIGHT = 1;

    private float mWidth;
    private float mHeight;
    private float mDrawingBoundsWidth;
    private float mDrawingBoundsHeight;
    private boolean mSymmetric;

    private DrawingDescriptor mDrawingDescriptor;

    private float[] mInterpolatedDimensions = new float[2];

    /**
     * Create a new rectangle interpolator.
     *
     * @param maxValue Max value for the units upon which the interpolation fraction will be calculated.
     * @param height
     * @param width
     */
    public RectangleInterpolator(int maxValue, float height, float width) {
        this(maxValue, height, width, false);
    }

    /**
     * Create a new rectangle interpolator.
     *
     * @param maxValue Max value for the units upon which the interpolation fraction will be calculated.
     * @param height
     * @param width
     * @param symmetric True if this rectangle should be drawn as four rectangles into one.
     * When true, the dimensions will be halved and the interpolation will be based on the
     * new dimensions.
     */
    public RectangleInterpolator(int maxValue, float height, float width, boolean symmetric) {
        super(maxValue);
        mDrawingBoundsHeight = height;
        mDrawingBoundsWidth = width;
        mHeight = height > 0 ? height : 1;
        mWidth = width > 0 ? width : 1;
        mWidth = symmetric ? width / 2 : width;
        mHeight = symmetric ? height / 2 : height;

        mSymmetric = symmetric;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    /**
     * @return Width of the rectangle in which the rectangles returned in {@link DrawingDescriptor#getRectangleComponents()}  is based.
     */
    public float getDrawingBoundsWidth() {
        return mDrawingBoundsWidth;
    }

    /**
     * @return Height of the rectangle in which the rectangles returned in {@link DrawingDescriptor#getRectangleComponents()}  is based.
     */
    public float getDrawingBoundsHeight() {
        return mDrawingBoundsHeight;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public boolean isSymmetric() {
        return mSymmetric;
    }

    /**
     * @param symmetric Set to true if the rectangle should be drawn as four rectangles formed into
     * one. In that scenario, the dimensions are halved and the interpolation is calculated based on
     * the new dimensions.
     */
    public void setSymmetric(boolean symmetric) {
        if (symmetric == mSymmetric) return;

        if (symmetric) {
            mWidth = mWidth / 2;
            mHeight = mHeight / 2;
        } else {
            mWidth = mWidth * 2;
            mHeight = mHeight * 2;
        }

        mSymmetric = symmetric;
    }

    public float[] getInterpolatedDimensions() {
        float interpolatedWidth = getInterpolation() * mWidth;
        float interpolatedHeight = getInterpolation() * mHeight;

        mInterpolatedDimensions[INTERPOLATION_VALUES_WIDTH] = interpolatedWidth;
        mInterpolatedDimensions[INTERPOLATION_VALUES_HEIGHT] = interpolatedHeight;

        return mInterpolatedDimensions;
    }

    /**
     * @return {@link DrawingDescriptor} containing the rectangle components used to construct a rectangle.
     * The components are drawn within a rectangle of width {@link RectangleInterpolator#getDrawingBoundsWidth()}
     * and height of {@link RectangleInterpolator#getDrawingBoundsHeight()} .
     */
    public DrawingDescriptor getDrawingDescriptor() {
        calculateDrawingMetrics();
        return mDrawingDescriptor;
    }

    private void calculateDrawingMetrics() {
        mDrawingDescriptor = new DrawingDescriptor();
        List<RectF> rects = new ArrayList<>();
        RectF bounds = new RectF(0, 0, mDrawingBoundsWidth, mDrawingBoundsHeight);

        float interpolatedWidth = getInterpolation() * mWidth;
        float interpolatedHeight = getInterpolation() * mHeight;

        if (!mSymmetric) {
            float top = bounds.bottom - interpolatedHeight;
            float bottom = bounds.bottom;
            float left = bounds.left;
            float right = left + interpolatedWidth;
            rects.add(new RectF(left, top, right, bottom));
        } else {
            // Bottom Left Rectangle
            float top = bounds.bottom - interpolatedHeight;
            float bottom = bounds.bottom;
            float left = bounds.left;
            float right = left + interpolatedWidth;
            rects.add(new RectF(left, top, right, bottom));

            // Bottom Right Rectangle
            top = bounds.bottom - interpolatedHeight;
            bottom = bounds.bottom;
            left = bounds.right - interpolatedWidth;
            right = bounds.right;
            rects.add(new RectF(left, top, right, bottom));

            // Top Left Rectangle
            top = bounds.top;
            bottom = bounds.top + interpolatedHeight;
            left = bounds.left;
            right = bounds.left + interpolatedWidth;
            rects.add(new RectF(left, top, right, bottom));

            // Top Right Rectangle
            top = bounds.top;
            bottom = bounds.top + interpolatedHeight;
            left = bounds.right - interpolatedWidth;
            right = bounds.right;
            rects.add(new RectF(left, top, right, bottom));
        }

        mDrawingDescriptor.setRectangleComponents(rects);
    }

    /**
     * Describes drawing of the rectangular components.
     */
    public static class DrawingDescriptor {

        private List<RectF> mRectangleComponents;

        public List<RectF> getRectangleComponents() {
            return mRectangleComponents;
        }

        private void setRectangleComponents(List<RectF> rectangleComponents) {
            mRectangleComponents = rectangleComponents;
        }
    }
}
