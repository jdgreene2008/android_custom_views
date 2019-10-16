package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;

public class SpiralShape extends FlashShape {
    private static final int MAX_COMPONENT_COUNT = 35;

    private SpiralInterpolator mSpiralInterpolator;

    public SpiralShape() {
        super(Type.SPIRAL);
    }

    public SpiralInterpolator getSpiralInterpolator() {
        return mSpiralInterpolator;
    }

    public void setSpiralInterpolator(
            SpiralInterpolator spiralInterpolator) {
        mSpiralInterpolator = spiralInterpolator;
    }

    @Override
    public int getMaxComponents() {
        return MAX_COMPONENT_COUNT;
    }
}
