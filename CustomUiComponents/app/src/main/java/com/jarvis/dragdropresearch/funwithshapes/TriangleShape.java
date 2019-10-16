package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.TriangleInterpolator;

public class TriangleShape extends FlashShape {

    private TriangleInterpolator mTriangleInterpolator;

    public TriangleShape() {
        super(Type.TRIANGLE);
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

    @Override
    public int getMaxComponents() {
        return 2;
    }
}
