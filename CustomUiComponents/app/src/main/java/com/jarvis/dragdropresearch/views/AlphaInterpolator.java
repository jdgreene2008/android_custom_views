package com.jarvis.dragdropresearch.views;

/**
 * Manages the alpha part of the color space.
 */
public class AlphaInterpolator extends Interpolator {

    private float mAlpha = 1.0f;

    public AlphaInterpolator(int maxValue) {
        super(maxValue);
    }

    public AlphaInterpolator(int maxValue, float alpha) {
        super(maxValue);
        mAlpha = alpha;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }
}
