package com.jarvis.dragdropresearch.rails.domain;

/**
 * Represents a component of the rail being drawn.
 */
public class MovableObject {

    private int mXPos;
    private int mYPos;
    private String mId;
    private int mColor;

    public MovableObject(int initialX, int initialY, String id, int color) {
        mXPos = initialX;
        mYPos = initialY;
        mId = id;
        mColor = color;
    }

    public int getXPos() {
        return mXPos;
    }

    public void setXPos(int XPos) {
        mXPos = XPos;
    }

    public int getYPos() {
        return mYPos;
    }

    public void setYPos(int YPos) {
        mYPos = YPos;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
