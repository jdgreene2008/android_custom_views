package com.jarvis.dragdropresearch.interpolators;

public class RectangleInterpolator extends Interpolator {
    public static final int INTERPOLATION_VALUES_WIDTH = 0;
    public static final int INTERPOLATION_VALUES_HEIGHT = 1;

    private float mWidth;
    private float mHeight;
    private boolean mSymmetric;

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
     * @param symmetric True if this rectangle should be drawn as four rectangled into one.
     * When true, the dimensions will be halved and the interpolation will be based on the
     * new dimensions.
     */
    public RectangleInterpolator(int maxValue, float height, float width, boolean symmetric) {
        super(maxValue);
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
        float interpolatedWidth = getInterpolatedValue() * mWidth;
        float interpolatedHeight = getInterpolatedValue() * mHeight;

        mInterpolatedDimensions[INTERPOLATION_VALUES_WIDTH] = interpolatedWidth;
        mInterpolatedDimensions[INTERPOLATION_VALUES_HEIGHT] = interpolatedHeight;

        return mInterpolatedDimensions;
    }
}
