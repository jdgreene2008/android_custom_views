package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;

public class TriangleShape extends FlashShape {

    private int mBase;

    private int mAltitude;

    private boolean mSymmetric;

    private TriangleInterpolator mTriangleInterpolator;

    public TriangleShape() {
        super(Type.TRIANGLE);
    }

    public int getBase() {
        return mBase;
    }

    public void setBase(int base) {
        mBase = base;
    }

    public int getAltitude() {
        return mAltitude;
    }

    public void setAltitude(int altitude) {
        mAltitude = altitude;
    }

    /**
     * @return True if a corresponding triangle that mirrors this triangle should be drawn. If this
     * is true, one triangle will be drawn at the X start coordinate of the base and the
     * other triangle will be drawn at the X end coordinate of the base. The base for each triangle
     * will be 1/2 the value passed into {@link TriangleShape#setBase(int)}.
     */
    public boolean isSymmetric() {
        return mSymmetric;
    }

    public void setSymmetric(boolean symmetric) {
        mSymmetric = symmetric;
    }

    public TriangleInterpolator getTriangleInterpolator() {
        return mTriangleInterpolator;
    }

    /**
     * @param triangleInterpolator {@link TriangleInterpolator} to be used when constructing this triangle.
     * If an interpolator is provided, the altitude and base will be calculated as a function of the
     * values passed in the interpolator. If not set, the triangle will always be drawn with the
     * provided base and altitude.
     */
    public void setTriangleInterpolator(
            TriangleInterpolator triangleInterpolator) {
        mTriangleInterpolator = triangleInterpolator;
    }
}
