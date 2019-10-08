package com.jarvis.dragdropresearch.views;

import android.graphics.Color;

/**
 * Manages the background color for the drawing rails.
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
}
