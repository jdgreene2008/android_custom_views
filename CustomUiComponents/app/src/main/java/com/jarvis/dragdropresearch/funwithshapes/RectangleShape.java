package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.RectangleInterpolator;

public class RectangleShape extends FlashShape {
    private static final int MAX_COMPONENT_COUNT = 4;

    private RectangleInterpolator mRectangleInterpolator;

    public RectangleShape() {
        super(Type.RECTANGLE);
    }

    public RectangleInterpolator getRectangleInterpolator() {
        return mRectangleInterpolator;
    }

    /**
     * @param rectangleInterpolator The {@link RectangleInterpolator} that describes how
     * this rectangle will be drawn.
     */
    public void setRectangleInterpolator(
            RectangleInterpolator rectangleInterpolator) {
        mRectangleInterpolator = rectangleInterpolator;
    }

    @Override
    public int getMaxComponents() {
        return MAX_COMPONENT_COUNT;
    }
}
