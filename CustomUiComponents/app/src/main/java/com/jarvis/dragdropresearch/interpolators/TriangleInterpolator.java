package com.jarvis.dragdropresearch.interpolators;

public class TriangleInterpolator extends Interpolator {
    public static final int INTERPOLATION_VALUES_BASE = 0;
    public static final int INTERPOLATION_VALUES_ALTITUDE = 1;

    private float mBase;

    private float mAltitude;

    private boolean mInterpolateOnAltitude = true;

    private int mInterpolatedAltitude;

    private float[] mInterpolatedValues = new float[2];

    /**
     * Create a new triangle interpolator.
     *
     * @param maxValue Max value for the units upon which the interpolation fraction will be calculated.
     * @param altitude Max altitude for the triangle. Should be greater than 0. If not, a value of 1
     * will be used.
     * @param base Max value for the base of the triangle. Should be greater than 0. If not, a value of
     * 1 will be used.
     */
    public TriangleInterpolator(int maxValue, float altitude, float base) {
        super(maxValue);
        mBase = base > 0 ? base : 1;
        mAltitude = altitude > 0 ? altitude : 1;
    }

    public float getBase() {
        return mBase;
    }

    public void setBase(int base) {
        mBase = base;
    }

    public float getAltitude() {
        return mAltitude;
    }

    public void setAltitude(int altitude) {
        mAltitude = altitude;
    }

    /**
     * @return True if the interpolated value is calculated as an interpolation of the altitude. By
     * default this is set to true.
     */
    public boolean isInterpolateOnAltitude() {
        return mInterpolateOnAltitude;
    }

    /**
     * @param interpolateOnAltitude Set to true if the interpolated value is calculated as an interpolation
     * of the altitude.
     */
    public void setInterpolateOnAltitude(boolean interpolateOnAltitude) {
        mInterpolateOnAltitude = interpolateOnAltitude;
    }

    public float[] getInterpolatedValues() {

        if (mInterpolateOnAltitude) {
            float interpolatedAltitude = getInterpolatedValue() * mAltitude;
            float interpolatedBase = (interpolatedAltitude * mBase) / mAltitude;

            mInterpolatedValues[INTERPOLATION_VALUES_BASE] = interpolatedBase;
            mInterpolatedValues[INTERPOLATION_VALUES_ALTITUDE] = interpolatedAltitude;
        } else {
            float interpolatedBase = getInterpolatedValue() * mBase;
            float interpolatedAltitude = (mAltitude * interpolatedBase) / mBase;

            mInterpolatedValues[INTERPOLATION_VALUES_BASE] = interpolatedBase;
            mInterpolatedValues[INTERPOLATION_VALUES_ALTITUDE] = interpolatedAltitude;
        }

        return mInterpolatedValues;
    }

    private int calculateAltitudeForInterpolatedBase() {
        float interpolatedBase = (int)(getInterpolatedValue() * mBase);
        return (int)((mAltitude * interpolatedBase) / mBase);
    }
}
