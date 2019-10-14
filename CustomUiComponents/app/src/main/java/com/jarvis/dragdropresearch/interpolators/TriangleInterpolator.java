package com.jarvis.dragdropresearch.interpolators;

public class TriangleInterpolator extends Interpolator {
    public static final int INTERPOLATION_VALUES_BASE = 0;
    public static final int INTERPOLATION_VALUES_ALTITUDE = 1;

    private float mBase;

    private float mAltitude;

    private boolean mInterpolateOnAltitude = true;

    private int mInterpolatedAltitude;

    private float[] mInterpolatedValues = new float[2];

    private boolean mSymmetric;

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
        this(maxValue, altitude, base, false);
    }

    /**
     * Create a new triangle interpolator.
     *
     * @param maxValue Max value for the units upon which the interpolation fraction will be calculated.
     * @param altitude Max altitude for the triangle. Should be greater than 0. If not, a value of 1
     * will be used.
     * @param base Max value for the base of the triangle. Should be greater than 0. If not, a value of
     * 1 will be used.
     * @param symmetric True if this triangle should be drawn as two triangles joining into one.
     * When true, the base will be halved and the interpolation will be based on the halved base.
     */
    public TriangleInterpolator(int maxValue, float altitude, float base, boolean symmetric) {
        super(maxValue);
        mBase = base > 0 ? base : 1;
        mBase = symmetric ? base / 2 : base;
        mAltitude = altitude > 0 ? altitude : 1;
        mSymmetric = symmetric;
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

    public boolean isSymmetric() {
        return mSymmetric;
    }

    /**
     * @param symmetric Set to true if the triangle should be drawn as two triangles formed into
     * one. In that scenario, the base is halved and the interpolation is calculated based on
     * the new base.
     */
    public void setSymmetric(boolean symmetric) {
        if (symmetric == mSymmetric) return;

        if (symmetric) {
            mBase = mBase / 2;
        } else {
            mBase = 2 * mBase;
        }

        mSymmetric = symmetric;
    }

    public float[] getInterpolatedValues() {

        if (mInterpolateOnAltitude) {
            float interpolatedAltitude = getInterpolation() * mAltitude;
            float interpolatedBase = (interpolatedAltitude * mBase) / mAltitude;

            mInterpolatedValues[INTERPOLATION_VALUES_BASE] = interpolatedBase;
            mInterpolatedValues[INTERPOLATION_VALUES_ALTITUDE] = interpolatedAltitude;
        } else {
            float interpolatedBase = getInterpolation() * mBase;
            float interpolatedAltitude = (mAltitude * interpolatedBase) / mBase;

            mInterpolatedValues[INTERPOLATION_VALUES_BASE] = interpolatedBase;
            mInterpolatedValues[INTERPOLATION_VALUES_ALTITUDE] = interpolatedAltitude;
        }

        return mInterpolatedValues;
    }

    private int calculateAltitudeForInterpolatedBase() {
        float interpolatedBase = (int)(getInterpolation() * mBase);
        return (int)((mAltitude * interpolatedBase) / mBase);
    }
}
