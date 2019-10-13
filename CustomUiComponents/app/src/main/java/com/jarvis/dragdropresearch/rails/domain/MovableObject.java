package com.jarvis.dragdropresearch.rails.domain;

/**
 * Represents a component of the rail being drawn.
 */
public class MovableObject {

    private int mXOffset;
    private int mYOffset;
    private String mId;
    private int mColor;

    public MovableObject(int initialX, int initialY, String id, int color) {
        mXOffset = initialX;
        mYOffset = initialY;
        mId = id;
        mColor = color;
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

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
