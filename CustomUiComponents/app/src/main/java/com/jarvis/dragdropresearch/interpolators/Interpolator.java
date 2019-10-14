package com.jarvis.dragdropresearch.interpolators;

public class Interpolator {

    private int mMaxValue;

    private int mValue;

    private float mInterpolation;

    public Interpolator(int maxValue) {
        this.mMaxValue = maxValue;
    }

    public int getValue() {
        return mValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void updateValue(int value) {
        mValue = value;
        calculateInterpolatedValue();
    }

    private void calculateInterpolatedValue() {
        if (mValue >= mMaxValue) {
            mInterpolation = 1.0f;
        } else if (mMaxValue <= 0) {
            mInterpolation = 0f;
        } else {
            mInterpolation = ((float)Math.abs(mValue) / (float)mMaxValue);
        }
    }

    public float getInterpolation() {
        return mInterpolation;
    }
}
