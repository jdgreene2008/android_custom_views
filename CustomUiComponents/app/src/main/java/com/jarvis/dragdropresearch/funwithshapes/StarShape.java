package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.RectangleInterpolator;

public class StarShape extends FlashShape {
    private static final int MAX_COMPONENT_COUNT = 6;

    private RectangleInterpolator mRectangleInterpolator;

    public StarShape() {
        super(Type.RECTANGLE);
    }

    @Override
    public int getMaxComponents() {
        return MAX_COMPONENT_COUNT;
    }
}
