package com.jarvis.dragdropresearch.interpolators;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

/**
 * Manages the background color for the drawing rails. The interpolator controls
 * the rate at which the color's alpha fades from 0 to 1 and vice versa.
 */
public class ColorInterpolator extends Interpolator {

    private int mColor = Color.BLACK;

    public ColorInterpolator(int maxValue) {
        super(maxValue);
    }

    public ColorInterpolator(int maxValue, int color) {
        super(maxValue);
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    /**
     * @return integer representing the shade of the color based on the interpolated value.
     */
    public int getInterpolatedShade() {
        return ColorUtils.setAlphaComponent(mColor,
                (int)(getInterpolation() * 255));
    }
}
