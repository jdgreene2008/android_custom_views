package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;

public class SpiralShape extends FlashShape {

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
}
