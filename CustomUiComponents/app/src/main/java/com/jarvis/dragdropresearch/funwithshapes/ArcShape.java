package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.AngleInterpolator;

public class ArcShape extends FlashShape {

    private AngleInterpolator mAngleInterpolator;

    public ArcShape() {
        super(Type.ARC);
    }

    public AngleInterpolator getAngleInterpolator() {
        return mAngleInterpolator;
    }

    public void setAngleInterpolator(
            AngleInterpolator angleInterpolator) {
        mAngleInterpolator = angleInterpolator;
    }
}
