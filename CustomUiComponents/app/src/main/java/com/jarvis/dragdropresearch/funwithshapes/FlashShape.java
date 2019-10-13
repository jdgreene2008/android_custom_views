package com.jarvis.dragdropresearch.funwithshapes;

import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;

public class FlashShape {

    private Type mType;

    private int mXOffset;

    private int mYOffset;

    private ColorInterpolator mColorInterpolator;

    private AlphaInterpolator mAlphaInterpolator;

    public FlashShape(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public int getXOffset() {
        return mXOffset;
    }

    public void setXOffset(int XOffset) {
        mXOffset = XOffset;
    }

    public int getYOffset() {
        return mYOffset;
    }

    public void setYOffset(int YOffset) {
        mYOffset = YOffset;
    }

    public ColorInterpolator getColorInterpolator() {
        return mColorInterpolator;
    }

    public void setColorInterpolator(
            ColorInterpolator colorInterpolator) {
        mColorInterpolator = colorInterpolator;
    }

    public AlphaInterpolator getAlphaInterpolator() {
        return mAlphaInterpolator;
    }

    public void setAlphaInterpolator(
            AlphaInterpolator alphaInterpolator) {
        mAlphaInterpolator = alphaInterpolator;
    }

    public enum Type {
        ARC,
        SPIRAL,
        RECTANGLE,
        TRIANGLE
    }
}
