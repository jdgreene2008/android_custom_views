package com.jarvis.dragdropresearch.views;

public class Interpolator {

    private int mMaxValue;

    private int mValue;

    private float mInterpolatedValue;

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

    public void setInterpolatedValue(float interpolatedValue) {
        mInterpolatedValue = interpolatedValue;
    }

    public void updateValue(int value) {
        mValue = value;
        calculateInterpolatedValue();
    }

    private void calculateInterpolatedValue() {
        if (mValue >= mMaxValue) {
            mInterpolatedValue = 1.0f;
        } else if (mMaxValue <= 0) {
            mInterpolatedValue = 0f;
        } else {
            mInterpolatedValue = ((float)Math.abs(mValue) / (float)mMaxValue);
        }
    }

    public float getInterpolatedValue() {
        return mInterpolatedValue;
    }
}
