package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.StarInterpolator;

public class StarShape extends FlashShape {
    private static final int MAX_COMPONENT_COUNT = 6;

    private StarInterpolator mStarInterpolator;

    public StarShape() {
        super(Type.STAR);
    }

    public StarInterpolator getStarInterpolator() {
        return mStarInterpolator;
    }

    public void setStarInterpolator(
            StarInterpolator starInterpolator) {
        mStarInterpolator = starInterpolator;
    }

    @Override
    public int getMaxComponents() {
        return MAX_COMPONENT_COUNT;
    }
}
