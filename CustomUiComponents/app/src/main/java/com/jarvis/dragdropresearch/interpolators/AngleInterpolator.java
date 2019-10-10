package com.jarvis.dragdropresearch.interpolators;

public class AngleInterpolator extends Interpolator {

    private float mMaxAngle;

    public AngleInterpolator(int maxValue) {
        super(maxValue);
    }

    public AngleInterpolator(int maxValue, float maxAngle) {
        super(maxValue);
        mMaxAngle = maxAngle;
    }

    public float getMaxAngle() {
        return mMaxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        mMaxAngle = maxAngle;
    }

    public float getInterpolatedAngle() {
        return getInterpolatedValue() * mMaxAngle;
    }
}
