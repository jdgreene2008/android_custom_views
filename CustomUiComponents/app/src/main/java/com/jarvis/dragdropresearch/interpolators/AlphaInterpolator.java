package com.jarvis.dragdropresearch.interpolators;

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

    /**
     * @return integer in the range of 0...255 representing the alpha channel with 255 representing
     * full opaqueness and 0 representing full transparency.
     */
    public int getInterpolatedAlpha() {
        return (int)(255 * getInterpolation());
    }
}
